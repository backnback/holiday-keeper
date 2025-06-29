package com.planit.holiday_keeper.domain.holiday.scheduler;

import com.planit.holiday_keeper.domain.holiday.entity.Country;
import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

  @Value("${custom.poolSize}")
  private int poolSize;

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
    long totalStart = System.currentTimeMillis();

    String countriesData = fetchCountriesData();
    List<Country> countries = countryService.saveApiResponse(countriesData);
    log.info("동기화 가능한 국가 수 : {}", countries.size());

    LocalDateTime syncTime = LocalDateTime.now();
    int currentYear = syncTime.getYear();
    long apiStart = System.currentTimeMillis();  // 성능 측정용
    List<CompletableFuture<List<Holiday>>> futures = new ArrayList<>();

    try (ExecutorService executor = Executors.newFixedThreadPool(poolSize)) {
      for (int i = 0; i < years; i++) {
        int year = currentYear - i;
        for (Country country : countries) {
          CompletableFuture<List<Holiday>> future = CompletableFuture.supplyAsync(() -> {
            String yearString = String.valueOf(year);
            String holidaysJson = fetchHolidaysData(yearString, country.getCountryCode());
            return holidayService.parseHolidays(holidaysJson, country, year);
          }, executor);
          futures.add(future);
        }
      }
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
      long apiTime = System.currentTimeMillis() - apiStart;  // 성능 측정용

      long collectStart = System.currentTimeMillis();
      List<Holiday> holidays = new ArrayList<>();
      for (CompletableFuture<List<Holiday>> future : futures) {
        holidays.addAll(future.get());
      }
      long collectTime = System.currentTimeMillis() - collectStart;


      long saveStart = System.currentTimeMillis();
      holidayService.saveAllAndDeleteOld(holidays, years, syncTime);
      long saveTime = System.currentTimeMillis() - saveStart;

      long endTime = System.currentTimeMillis();


      log.info("🟢 === 성능 분석 ===");
      log.info("🟢 API 호출: {}ms", apiTime);
      log.info("🟢 데이터 수집: {}ms", collectTime);
      log.info("🟢 DB 저장: {}ms", saveTime);
      log.info("🟢 === {}년 데이터 동기화 완료 - 총 소요시간: {}ms ===", years, endTime - totalStart);

    } catch (Exception e) {
      log.error("{}년 데이터 동기화 실패", years, e);
      throw new RuntimeException("데이터 동기화 실패", e);
    }
  }


  public void fetchByYearAndCountry(Country country, int year) {
    String yearString = String.valueOf(year);
    String holidaysJson = fetchHolidaysData(yearString, country.getCountryCode());
    List<Holiday> holidays = holidayService.parseHolidays(holidaysJson, country, year);
    holidayService.syncHolidays(holidays, country, year);
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
