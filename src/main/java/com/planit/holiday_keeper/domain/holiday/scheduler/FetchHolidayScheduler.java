package com.planit.holiday_keeper.domain.holiday.scheduler;

import com.planit.holiday_keeper.domain.holiday.entity.Country;
import com.planit.holiday_keeper.domain.holiday.service.CountryService;
import com.planit.holiday_keeper.domain.holiday.service.HolidayService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class FetchHolidayScheduler {

  @Value("${schedule.useInit}")
  private boolean useInit;

  @Value("${schedule.useSchedule}")
  private boolean useSchedule;

  @Value("${nager.holidays.url}")
  private String holidaysUrl;

  @Value("${nager.countries.url}")
  private String countryUrl;

  private final HolidayService holidayService;
  private final CountryService countryService;
  private final WebClient webClient;


  @PostConstruct
  public void initData() {
    if (useInit) {
      int initYears = 5;
      log.info("최초({}년) 데이터 동기화 시작", initYears);
      fetchAllByYears(initYears);
      log.info("최초({}년) 데이터 동기화 완료", initYears);
    }
  }


  @Scheduled(cron = "${schedule.cron_for_api}", zone = "Asia/Seoul")
  public void syncData() {
    if (useSchedule) {
      int syncYears = 2;
      log.info("정기({}년) 데이터 동기화 시작", syncYears);
      fetchAllByYears(syncYears);
      log.info("정기({}년) 데이터 동기화 완료", syncYears);
    }
  }


  private void fetchAllByYears(int years) {
    try {
      String countriesData = fetchCountriesData();
      List<Country> countries = countryService.saveApiResponse(countriesData);
      log.info("가능한 국가 수 : {}", countries.size());

      LocalDateTime syncTime = LocalDateTime.now();
      int currentYear = syncTime.getYear();
      try (ForkJoinPool customPool = new ForkJoinPool(10)) {
        for (int i = 0; i < years; i++) {
          int year = currentYear - i;
          customPool.submit(() ->
              countries.parallelStream().forEach(country -> {
                try {
                  fetchByYearAndCountry(country, year, syncTime);
                } catch (Exception e) {
                  log.error("국가 {} 데이터 동기화 실패", country.getCountryCode(), e);
                }
              })
          ).get();
        }
      }

    } catch (Exception e) {
      log.error("{}년 데이터 동기화 실패", years, e);
      throw new RuntimeException("데이터 동기화 실패", e);
    }
  }

  public void fetchByYearAndCountry(Country country, int year, LocalDateTime syncTime) {
    String yearString = String.valueOf(year);
    String holidays = fetchHolidaysData(yearString, country.getCountryCode());

    if (holidays == null) {
      log.debug("{}년 {} 국가 - null 응답", year, country.getCountryCode());
      return;
    }

    holidayService.saveApiResponse(holidays, country, year, syncTime);
  }

  public String fetchCountriesData() {
    return fetchData(countryUrl);
  }


  public String fetchHolidaysData(String year, String countryCode) {
    String url = String.format("%s/%s/%s", holidaysUrl, year, countryCode);
    return fetchData(url);
  }


  private String fetchData(String url) {
    try {
      return webClient.get()
          .uri(url)
          .retrieve()
          .bodyToMono(String.class)
          .timeout(Duration.ofSeconds(30))
          .block();

    } catch (Exception e) {
      log.error("API 호출 중 에러 발생", e);
      throw new RuntimeException("API 호출 실패", e);
    }
  }

}
