package com.planit.holiday_keeper.domain.holiday.scheduler;

import com.planit.holiday_keeper.domain.holiday.service.CountryService;
import com.planit.holiday_keeper.domain.holiday.service.HolidayService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class FetchHolidayScheduler {

  @Value("${schedule.use}")
  private boolean useSchedule;

  @Value("${nager.holidays.url}")
  private String holidaysUrl;

  @Value("${nager.countries.url}")
  private String countryUrl;

  private final HolidayService holidayService;
  private final CountryService countryService;


  @Scheduled(cron = "${schedule.cron_for_api}")
  @PostConstruct
  public void fetchHolidaysInfo() {
    if (useSchedule) {
      try {
        log.info("공휴일 API 스케줄러 실행");
        String countries = fetchCountriesData();
        countryService.saveApiResponse(countries);

        String holidays = fetchHolidaysData("2025", "KR");
        holidayService.saveApiResponse(holidays);

      } catch (Exception e) {
        log.error("공휴일 API 스케줄러 호출 에러 발생", e);

      } finally {
        log.info("공휴일 API 스케줄러 종료");
      }
    }
  }

  public String fetchCountriesData() {
    log.info("가능한 국가 목록 조회");
    return fetchData(countryUrl);
  }

  public String fetchHolidaysData(String year, String countryCode) {
    log.info("연도 및 국가별 공휴일 조회");
    String url = String.format("%s/%s/%s", holidaysUrl, year, countryCode);
    return fetchData(url);
  }


  private String fetchData(String url) {
    try {
      log.info("API Request URL: {}", url);

      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");

      StringBuilder response = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
      }

      return response.toString();

    } catch (Exception e) {
      log.error("API 호출 중 에러 발생", e);
      throw new RuntimeException("API 호출 실패", e);
    }
  }

}
