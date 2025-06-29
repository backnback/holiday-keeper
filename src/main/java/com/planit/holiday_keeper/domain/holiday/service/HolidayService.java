package com.planit.holiday_keeper.domain.holiday.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.holiday_keeper.domain.holiday.dto.external.PublicHolidaysApiResponse;
import com.planit.holiday_keeper.domain.holiday.dto.response.CountryHolidayCountResponse;
import com.planit.holiday_keeper.domain.holiday.dto.response.HolidayResponse;
import com.planit.holiday_keeper.domain.holiday.entity.Country;
import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
import com.planit.holiday_keeper.domain.holiday.repository.HolidayRepository;
import com.planit.holiday_keeper.global.exceptions.CustomException;
import com.planit.holiday_keeper.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

  private final HolidayRepository holidayRepository;
  private final CountryService countryService;
  private final ObjectMapper objectMapper;


  public List<Holiday> parseHolidays(
      String jsonResponse, Country country, int year
  ) {
    try {
      List<PublicHolidaysApiResponse>
          responses = objectMapper.readValue(jsonResponse, new TypeReference<List<PublicHolidaysApiResponse>>(){});
      if (responses.isEmpty()) {
        log.info("{}년 {} 국가 - 파싱 후 빈 데이터", year, country.getCountryCode());
        return new ArrayList<>();
      }

      List<Holiday> holidays = new ArrayList<>();
      for (PublicHolidaysApiResponse response : responses) {
        holidays.add(response.toEntity(country));
      }

      return holidays;

    } catch (Exception e) {
      log.error("국가 {} 데이터 파싱 중 에러 발생: {}", country.getCountryCode(), e.getMessage());
      return new ArrayList<>();
    }
  }


  @Retryable(
      retryFor = { CannotAcquireLockException.class },
      maxAttempts = 3,
      backoff = @Backoff(delay = 2000, multiplier = 2)
  )
  @Transactional
  public void saveAllAndDeleteOld(
      List<Holiday> holidays, int years, LocalDateTime syncTime
  ) {
    if (holidays.isEmpty()) {
      log.warn("데이터 동기화 실패 - 저장할 공휴일 데이터가 없습니다.");
      return;
    }

    holidays.sort(Comparator.comparing(Holiday::getDate).thenComparing(Holiday::getName));
    holidayRepository.bulkUpsert(holidays, syncTime);

    log.info("공휴일 데이터 저장 완료 - 가져온 데이터 수: {}, syncTime: {}",
        holidays.size(), syncTime);

    deleteAllMissingHolidays(years, syncTime);
  }


  @Transactional
  public void syncHolidays(List<Holiday> holidays, Country country, int year) {
    if (holidays.isEmpty()) {
      log.warn("데이터 동기화 실패 - 저장할 공휴일 데이터가 없습니다.");
      return;
    }

    holidayRepository.deleteByCountryAndHolidayYear(country, year);
    holidayRepository.flush();
    holidayRepository.bulkUpsert(holidays, LocalDateTime.now());
  }


  @Transactional
  public void deleteAllMissingHolidays(int years, LocalDateTime syncTime) {
    int endYear = syncTime.getYear();
    int startYear = endYear - years + 1;
    LocalDateTime deleteThreshold = syncTime.minusSeconds(1);
    int deletedCount = holidayRepository.deleteMissingHolidays(startYear, endYear, deleteThreshold);
    log.info("데이터 삭제 정리 완료 - year: {} - {}, 삭제: {}개, syncTime: {}",
        startYear, endYear, deletedCount, syncTime);
  }


  public Page<HolidayResponse> findWithFilters(
      Integer year, String countryCode, LocalDate from, LocalDate to,
      String type, String name, Pageable pageable
  ) {
    if (year != null) {
      validateYear(year);
    }

    if (countryCode != null) {
      Country country = countryService.findByCountryCode(countryCode);
      countryCode = country.getCountryCode();
    }

    Page<Holiday> holidays = holidayRepository.findWithFilters(
        year, countryCode, from, to, type, name, pageable
    );

    return holidays.map(HolidayResponse::of);
  }


  @Transactional
  public int deleteAllBy(Country country, Integer year) {
    if (year != null) {
      validateYear(year);
      return holidayRepository.deleteByCountryAndHolidayYear(country, year);
    } else {
      throw new CustomException(ErrorCode.YEAR_REQUIRED);
    }
  }


  public Page<CountryHolidayCountResponse> getCountByCountry(Integer year, Pageable pageable) {
    if (year == null) {
      throw new CustomException(ErrorCode.YEAR_REQUIRED);
    }
    Page<Object[]> rows = holidayRepository.countHolidaysByCountry(year, pageable);

    return rows.map(row -> CountryHolidayCountResponse.of(
        (String) row[0], (String) row[1], (Long) row[2]
    ));
  }


  // 테스트 용도
  @Transactional
  public int updateHolidayNames(String countryCode, int year, int limitCount) {
    List<Holiday> holidays = holidayRepository.findByCountryCodeAndYear(countryCode, year);

    int updatedCount = 0;
    for (Holiday holiday : holidays) {
      if (updatedCount >= limitCount) break;

      holiday.setName("TEST_" + holiday.getId());
      updatedCount++;
    }

    return updatedCount;
  }


  // 테스트 용도
  @Transactional
  public int deleteHoliday(String countryCode, int year, int limitCount) {
    List<Holiday> holidays = holidayRepository.findByCountryCodeAndYearOrderByIdDesc(countryCode, year);
    if (holidays.isEmpty()) {
      return 0;
    }

    int deleted = 0;
    for (Holiday holiday : holidays) {
      if (deleted >= limitCount) break;

      holidayRepository.delete(holiday);
      deleted++;
      log.info("공휴일 삭제 완료 - country: {}, year: {}, 삭제된 ID: {}",
          countryCode, year, holiday.getId());
    }

    return deleted;
  }


  public List<Holiday> findByCountryCodeAndYear(String countryCode, int year) {
    return holidayRepository.findByCountryCodeAndYear(countryCode, year);
  }


  public void validateYear(int year) {
    int thisYear = LocalDate.now().getYear();
    int minYear = thisYear - 4;  // 최근 5년

    if (year < minYear || year > thisYear) {
      throw new CustomException(ErrorCode.INVALID_YEAR_RANGE);
    }
  }

}
