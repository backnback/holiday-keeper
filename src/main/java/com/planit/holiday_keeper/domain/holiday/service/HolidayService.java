package com.planit.holiday_keeper.domain.holiday.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.holiday_keeper.domain.holiday.dto.external.PublicHolidaysApiResponse;
import com.planit.holiday_keeper.domain.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

  private final HolidayRepository holidayRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void saveApiResponse(String jsonResponse) {
      log.info(jsonResponse);

      try {
        List<PublicHolidaysApiResponse>
            responses = objectMapper.readValue(jsonResponse, new TypeReference<List<PublicHolidaysApiResponse>>(){});

        log.info(responses.getFirst().name());

      } catch (Exception e) {
        log.error("데이터 저장 중 에러 발생: {}", e.getMessage(), e);
        throw new RuntimeException("데이터 저장 실패", e);
      }
  }

}
