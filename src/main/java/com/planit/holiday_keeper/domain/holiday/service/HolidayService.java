package com.planit.holiday_keeper.domain.holiday.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.holiday_keeper.domain.holiday.dto.external.PublicHolidaysApiResponse;
import com.planit.holiday_keeper.domain.holiday.dto.response.HolidayResponse;
import com.planit.holiday_keeper.domain.holiday.entity.Country;
import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
import com.planit.holiday_keeper.domain.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

  private final HolidayRepository holidayRepository;
  private final ObjectMapper objectMapper;


  @Transactional
  public void saveApiResponse(String jsonResponse, Country country) {
    try {
      List<PublicHolidaysApiResponse>
          responses = objectMapper.readValue(jsonResponse, new TypeReference<List<PublicHolidaysApiResponse>>(){});

      for (PublicHolidaysApiResponse response : responses) {
        holidayRepository.upsert(response.toEntity(country));
      }

    } catch (Exception e) {
      log.error("공휴일 데이터 저장 중 에러 발생: {}", e.getMessage(), e);
      throw new RuntimeException("공휴일 데이터 저장 실패", e);
    }
  }


  public Page<HolidayResponse> findWithFilters(
      Integer year, String countryCode, LocalDate from, LocalDate to,
      String type, String name, Pageable pageable
  ) {
    Page<Holiday> holidays = holidayRepository.findWithFilters(
        year, countryCode, from, to, type, name, pageable
    );

    return holidays.map(HolidayResponse::of);
  }
}
