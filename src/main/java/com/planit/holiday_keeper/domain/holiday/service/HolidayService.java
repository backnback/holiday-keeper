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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

  private final HolidayRepository holidayRepository;
  private final CountryService countryService;
  private final ObjectMapper objectMapper;


  @Transactional
  public void saveApiResponse(
      String jsonResponse, Country country, int year, LocalDateTime syncTime
  ) {
    try {
      List<PublicHolidaysApiResponse>
          responses = objectMapper.readValue(jsonResponse, new TypeReference<List<PublicHolidaysApiResponse>>(){});

      if (responses.isEmpty()) {
        log.info("{}년 {} 국가 - 파싱 후 빈 데이터", year, country.getCountryCode());
        return;
      }

      for (PublicHolidaysApiResponse response : responses) {
        holidayRepository.upsert(response.toEntity(country), syncTime);
      }

      LocalDateTime deleteThreshold = syncTime.minusSeconds(1);  // 안전 장치
      int deletedCount = holidayRepository.deleteMissingHolidays(country, year, deleteThreshold);
      log.info("공휴일 동기화 완료 - country: {}, year: {}, 저장: {}개, 삭제: {}개, syncTime: {}",
          country.getCountryCode(), year, responses.size(), deletedCount, syncTime);

    } catch (Exception e) {
      log.error("공휴일 데이터 저장 중 에러 발생: {}", e.getMessage(), e);
      throw new RuntimeException("공휴일 데이터 저장 실패", e);
    }
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
