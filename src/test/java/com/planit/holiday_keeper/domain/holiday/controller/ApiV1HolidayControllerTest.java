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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiV1HolidayControllerTest {

  @Autowired
  TestRestTemplate restTemplate;
  @Autowired
  ObjectMapper objectMapper;


  @Test
  void 공휴일_목록_조회() throws Exception {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/api/v1/holidays?page=0&size=5", String.class);

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
  void 공휴일_목록_조회_국가코드및연도_필터링() throws Exception {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/api/v1/holidays?year=2025&countryCode=KR", String.class);

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
  void 공휴일_목록_조회_공휴일타입및연도_필터링() throws Exception {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/api/v1/holidays?year=2025&type=EDUCATION", String.class);

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
  void 데이터_동기화_Refresh() throws Exception {
    SyncDataRequest request = new SyncDataRequest(2025, "KR");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<SyncDataRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<String> response =
        restTemplate.postForEntity("/api/v1/holidays/sync", entity, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    JsonNode json = objectMapper.readTree(response.getBody());
    assertThat(json.get("resultCode").asText()).isEqualTo("204");
    assertThat(json.get("msg").asText()).contains("동기화");
  }
}
