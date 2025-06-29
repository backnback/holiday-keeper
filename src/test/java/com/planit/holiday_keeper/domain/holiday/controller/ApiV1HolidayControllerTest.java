package com.planit.holiday_keeper.domain.holiday.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.holiday_keeper.domain.holiday.dto.request.SyncDataRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=test"
)
public class ApiV1HolidayControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;


  @Test
  void 공휴일_목록_조회_API() throws Exception {

    ResponseEntity<String> response = restTemplate.getForEntity(
        "/api/v1/holidays?page=0&size=5", String.class
    );
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);  // 상태코드 확인

    // RsData 응답 내용 확인
    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("resultCode").asText()).isEqualTo("200");
    assertThat(json.get("msg").asText()).contains("성공");

    // 커스텀 페이지 응답 객체 내용 확인
    JsonNode data = json.get("data");
    assertThat(data.has("content")).isTrue();
    assertThat(data.has("totalElements")).isTrue();
    assertThat(data.get("page").asInt()).isEqualTo(0);
    assertThat(data.get("size").asInt()).isEqualTo(5);

    // 실제 데이터 확인
    JsonNode content = data.get("content");
    assertThat(content.isArray()).isTrue();
    if (!content.isEmpty()) {
      JsonNode holiday = content.get(0);
      assertThat(holiday.has("id")).isTrue();
      assertThat(holiday.has("date")).isTrue();
      assertThat(holiday.has("name")).isTrue();
      assertThat(holiday.has("countryCode")).isTrue();
    }
  }


  @Test
  void 공휴일_목록_조회_국가코드_및_연도_필터링() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity(
        "/api/v1/holidays?year=2025&countryCode=KR", String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("resultCode").asText()).isEqualTo("200");
    assertThat(json.get("msg").asText()).contains("성공");

    JsonNode content = json.get("data").get("content");
    assertThat(content.isArray()).isTrue();
    if (!content.isEmpty()) {
      JsonNode holiday = content.get(0);
      assertThat(holiday.get("countryCode").asText()).isEqualTo("KR");
      assertThat(holiday.get("date").asText()).startsWith("2025");
    }
  }


  @Test
  void 공휴일_목록_조회_공휴일타입_및_연도_필터링() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity(
        "/api/v1/holidays?year=2025&type=EDUCATION", String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("resultCode").asText()).isEqualTo("200");
    assertThat(json.get("msg").asText()).contains("성공");

    JsonNode content = json.get("data").get("content");
    assertThat(content.isArray()).isTrue();
    if (!content.isEmpty()) {
      JsonNode holiday = content.get(0);
      assertThat(holiday.get("countryCode").asText()).isEqualTo("EDUCATION");
      assertThat(holiday.get("date").asText()).startsWith("2025");
    }
  }


  @Test
  void 데이터_동기화_Refresh_API() throws Exception {
    SyncDataRequest request = new SyncDataRequest(2025, "KR");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<SyncDataRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(
        "/api/v1/holidays/sync", entity, String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("resultCode").asText()).isEqualTo("204");
    assertThat(json.get("msg").asText()).contains("동기화");
  }


  @Test
  void Refresh_API_내부_Upsert_및_삭제_검증() throws Exception {
    String countryCode = "KR";
    int year = 2025;

    // 1. 데이터 변경 전 (원본 조회)
    ResponseEntity<String> beforeResponse = restTemplate.getForEntity(
        "/api/v1/holidays?countryCode=" + countryCode + "&year=" + year, String.class
    );

    JsonNode beforeJson = objectMapper.readTree(beforeResponse.getBody());
    JsonNode beforeData = beforeJson.get("data");
    int originalCount = beforeData.get("totalElements").asInt();

    // 2. 데이터 조작
    if (originalCount > 5) {
      restTemplate.exchange(
          "/api/v1/test/holidays/2?countryCode=" + countryCode + "&year=" + year,
          HttpMethod.PATCH, null, String.class
      );

      restTemplate.exchange(
          "/api/v1/test/holidays/3?countryCode=" + countryCode + "&year=" + year,
          HttpMethod.DELETE, null, String.class
      );
    }

    // 3. 변형 후 - 데이터 조회
    ResponseEntity<String> afterModifyResponse = restTemplate.getForEntity(
        "/api/v1/holidays?countryCode=" + countryCode + "&year=" + year, String.class
    );

    JsonNode afterModifyJson = objectMapper.readTree(afterModifyResponse.getBody());
    JsonNode afterModifyData = afterModifyJson.get("data");
    int modifiedCount = afterModifyData.get("totalElements").asInt();
    JsonNode afterModifyContent = afterModifyData.get("content");

    // 3-1. 삭제 확인
    if (originalCount > 5) {
      assertThat(modifiedCount).isLessThan(originalCount);

      boolean hasModifiedData = false;
      for (JsonNode holiday : afterModifyContent) {
        if (holiday.get("name").asText().contains("TEST_")) {
          hasModifiedData = true;
          break;
        }
      }
      assertThat(hasModifiedData).isTrue();
    }

    // 4. 동기화 실행
    SyncDataRequest syncRequest = new SyncDataRequest(year, countryCode);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<SyncDataRequest> entity = new HttpEntity<>(syncRequest, headers);

    ResponseEntity<String> syncResponse = restTemplate.postForEntity(
        "/api/v1/holidays/sync", entity, String.class
    );

    assertThat(syncResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode syncJson = objectMapper.readTree(syncResponse.getBody());
    assertThat(syncJson.get("resultCode").asText()).isEqualTo("204");


    // 5. 동기화 후 데이터 조회
    ResponseEntity<String> afterSyncResponse = restTemplate.getForEntity(
        "/api/v1/holidays?countryCode=" + countryCode + "&year=" + year, String.class
    );

    JsonNode afterSyncJson = objectMapper.readTree(afterSyncResponse.getBody());
    JsonNode afterSyncData = afterSyncJson.get("data");
    int restoredCount = afterSyncData.get("totalElements").asInt();
    JsonNode afterSyncContent = afterSyncData.get("content");

    // 6. 검증
    if (originalCount > 5) {
      assertThat(restoredCount).isGreaterThan(modifiedCount);

      boolean hasOriginalData = false;
      for (JsonNode holiday : afterSyncContent) {
        String name = holiday.get("name").asText();
        if (!name.contains("TEST_") && name.length() > 3) {
          hasOriginalData = true;
          break;
        }
      }
      assertThat(hasOriginalData).isTrue();

    } else {
      fail("테스트에 필요한 충분한 데이터가 없습니다");
    }
  }


  @Test
  void 특정_연도_국가_데이터_전체_삭제_API_검증() throws Exception {
    String countryCode = "KR";
    int year = 2024;

    // 삭제 전 조회
    ResponseEntity<String> beforeResponse = restTemplate.getForEntity(
        "/api/v1/holidays?countryCode=" + countryCode + "&year=" + year, String.class
    );
    assertThat(beforeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode beforeJson = objectMapper.readTree(beforeResponse.getBody());
    assertThat(beforeJson.get("resultCode").asText()).isEqualTo("200");

    JsonNode beforeData = beforeJson.get("data");
    int beforeCount = beforeData.get("totalElements").asInt();

    if (beforeCount == 0) fail("테스트에 필요한 충분한 데이터가 없습니다");


    // 삭제 API 호출
    ResponseEntity<String> deleteResponse = restTemplate.exchange(
        "/api/v1/holidays/" + countryCode + "/" + year,
        HttpMethod.DELETE, null, String.class
    );
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode deleteJson = objectMapper.readTree(deleteResponse.getBody());
    assertThat(deleteJson.get("resultCode").asText()).isEqualTo("204");


    // 삭제 후 데이터 조회
    ResponseEntity<String> afterResponse = restTemplate.getForEntity(
        "/api/v1/holidays?countryCode=" + countryCode + "&year=" + year, String.class
    );
    assertThat(afterResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode afterJson = objectMapper.readTree(afterResponse.getBody());
    assertThat(afterJson.get("resultCode").asText()).isEqualTo("200");

    JsonNode afterData = afterJson.get("data");
    int afterCount = afterData.get("totalElements").asInt();


    // 삭제 검증
    assertThat(afterCount).isEqualTo(0);
    JsonNode afterContent = afterData.get("content");
    assertThat(afterContent.isArray()).isTrue();
    assertThat(afterContent.size()).isEqualTo(0);
  }


  @Test
  void 나라별_공휴일_개수_조회_API_검증() throws Exception {
    int year = 2025;

    ResponseEntity<String> response = restTemplate.getForEntity(
        "/api/v1/holidays/countries?page=0&size=5&year=" + year + "&sort=desc",
        String.class
    );
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("resultCode").asText()).isEqualTo("200");
    assertThat(json.get("msg").asText()).contains("성공");

    // 페이지 응답 구조 확인
    JsonNode data = json.get("data");
    assertThat(data.has("content")).isTrue();
    assertThat(data.has("totalElements")).isTrue();
    assertThat(data.get("page").asInt()).isEqualTo(0);
    assertThat(data.get("size").asInt()).isEqualTo(5);

    // 실제 데이터 확인
    JsonNode content = data.get("content");
    assertThat(content.isArray()).isTrue();
    if (!content.isEmpty()) {
      JsonNode countryData = content.get(0);
      assertThat(countryData.has("countryCode")).isTrue();
      assertThat(countryData.has("name")).isTrue();
      assertThat(countryData.has("holidayCount")).isTrue();
      assertThat(countryData.get("holidayCount").isNumber()).isTrue();
      assertThat(countryData.get("holidayCount").asInt()).isGreaterThanOrEqualTo(0);
      assertThat(countryData.get("countryCode").asText()).isNotEmpty();

      String countryCode = countryData.get("countryCode").asText();
      ResponseEntity<String> realResponse = restTemplate.getForEntity(
          "/api/v1/holidays?countryCode=" + countryCode + "&year=" + year, String.class
      );
      JsonNode realJson = objectMapper.readTree(realResponse.getBody());
      JsonNode realData = realJson.get("data");
      int realCount = realData.get("totalElements").asInt();

      assertThat(countryData.get("holidayCount").asInt()).isEqualTo(realCount);
    }
  }


  // 예외 처리
  @Test
  void 잘못된_국가코드_및_연도로_목록_조회시_예외처리() throws Exception {
    String wrongCode = "AAC";
    ResponseEntity<String> response = restTemplate.getForEntity(
        "/api/v1/holidays?countryCode=" + wrongCode, String.class
    );

    assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("message").asText()).contains("유효하지 않은 국가 코드");

    int wrongYear = 2014;
    ResponseEntity<String> response2 = restTemplate.getForEntity(
        "/api/v1/holidays?year=" + wrongYear, String.class
    );

    assertThat(response2.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
    JsonNode json2 = objectMapper.readTree(response2.getBody());
    assertThat(json2.get("message").asText()).contains("연도는 최근 5년 범위");
  }

  @Test
  void 국가코드나_연도_없이_조회시_예외발생_안함_증명() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity(
        "/api/v1/holidays", String.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("resultCode").asText()).isEqualTo("200");
    assertThat(json.get("msg").asText()).contains("성공");
    JsonNode data = json.get("data");
    assertThat(data.has("content")).isTrue();
    assertThat(data.has("totalElements")).isTrue();
  }


  @Test
  void 잘못된_국가코드_및_연도로_동기화시_예외처리() throws Exception {
    String wrongCode = "AAC";
    SyncDataRequest request = new SyncDataRequest(2025, wrongCode);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<SyncDataRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(
        "/api/v1/holidays/sync", entity, String.class
    );

    assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("message").asText()).contains("유효하지 않은 국가 코드");


    int wrongYear = 2014;
    SyncDataRequest request2 = new SyncDataRequest(wrongYear, "KR");
    HttpEntity<SyncDataRequest> entity2 = new HttpEntity<>(request2, headers);

    ResponseEntity<String> response2 = restTemplate.postForEntity(
        "/api/v1/holidays/sync", entity2, String.class
    );

    assertThat(response2.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
    JsonNode json2 = objectMapper.readTree(response2.getBody());
    assertThat(json2.get("message").asText()).contains("연도는 최근 5년 범위");
  }


  @Test
  void 동기화시_필수값_누락_예외처리() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    SyncDataRequest request1 = new SyncDataRequest(2025, null);
    HttpEntity<SyncDataRequest> entity1 = new HttpEntity<>(request1, headers);
    ResponseEntity<String> response1 = restTemplate.postForEntity(
        "/api/v1/holidays/sync", entity1, String.class
    );
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    JsonNode json1 = objectMapper.readTree(response1.getBody());
    assertThat(json1.get("message").asText()).contains("입력값이 올바르지 않습니다");


    SyncDataRequest request2 = new SyncDataRequest(null, "KR");
    HttpEntity<SyncDataRequest> entity2 = new HttpEntity<>(request2, headers);
    ResponseEntity<String> response2 = restTemplate.postForEntity(
        "/api/v1/holidays/sync", entity2, String.class
    );
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    JsonNode json2 = objectMapper.readTree(response2.getBody());
    assertThat(json2.get("message").asText()).contains("입력값이 올바르지 않습니다");
  }


  @Test
  void 잘못된_국가코드_및_연도로_삭제시_예외처리() throws Exception {
    ResponseEntity<String> response = restTemplate.exchange(
        "/api/v1/holidays/AAC/2025",
        HttpMethod.DELETE, null, String.class
    );
    assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("message").asText()).contains("유효하지 않은 국가 코드");


    int wrongYear = 2014;
    ResponseEntity<String> response2 = restTemplate.exchange(
        "/api/v1/holidays/KR/" + wrongYear,
        HttpMethod.DELETE, null, String.class
    );

    assertThat(response2.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
    JsonNode json2 = objectMapper.readTree(response2.getBody());
    assertThat(json2.get("message").asText()).contains("연도는 최근 5년 범위");
  }
}
