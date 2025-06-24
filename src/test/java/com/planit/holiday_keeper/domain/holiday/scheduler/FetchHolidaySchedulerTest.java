package com.planit.holiday_keeper.domain.holiday.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "schedule.useInit=false",
    "schedule.useSchedule=true",
    "schedule.cron_for_api=0/5 * * * * ?"
})
public class FetchHolidaySchedulerTest {

  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @Timeout(15)
  void 임의시간_설정_스케줄러_작동_테스트_및_데이터_정기적재_검증() throws Exception {

    Thread.sleep(6000);

    int year1 = 2025;
    int year2 = 2021;
    String countryCode = "KR";

    ResponseEntity<String> response = restTemplate.getForEntity(
        "/api/v1/holidays?page=0&size=5&year=" + year1 + "&countryCode=" + countryCode, String.class
    );
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode json = objectMapper.readTree(response.getBody());
    JsonNode data = json.get("data");
    Assertions.assertThat(data.has("content")).isTrue();
    Assertions.assertThat(data.has("totalElements")).isTrue();
    Assertions.assertThat(data.get("page").asInt()).isEqualTo(0);
    Assertions.assertThat(data.get("size").asInt()).isEqualTo(5);

    JsonNode content = data.get("content");
    Assertions.assertThat(content.isArray()).isTrue();
    if (!content.isEmpty()) {
      JsonNode holiday = content.get(0);
      Assertions.assertThat(holiday.has("id")).isTrue();
      Assertions.assertThat(holiday.has("date")).isTrue();
      Assertions.assertThat(holiday.has("name")).isTrue();
      Assertions.assertThat(holiday.has("countryCode")).isTrue();
    }


    ResponseEntity<String> response2 = restTemplate.getForEntity(
        "/api/v1/holidays?page=0&size=5&year=" + year2 + "&countryCode=" + countryCode, String.class
    );
    Assertions.assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
    JsonNode json2 = objectMapper.readTree(response2.getBody());
    JsonNode data2 = json2.get("data");
    Assertions.assertThat(data2.has("content")).isTrue();
    Assertions.assertThat(data2.has("totalElements")).isTrue();
    Assertions.assertThat(data2.get("page").asInt()).isEqualTo(0);
    Assertions.assertThat(data2.get("size").asInt()).isEqualTo(5);

    JsonNode content2 = data2.get("content");
    Assertions.assertThat(content2.size()).isEqualTo(0);  // 빈배열이 나와야 정상
  }

}
