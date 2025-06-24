package com.planit.holiday_keeper.domain.holiday.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.holiday_keeper.domain.holiday.dto.external.AvailableCountriesApiResponse;
import com.planit.holiday_keeper.domain.holiday.entity.Country;
import com.planit.holiday_keeper.domain.holiday.repository.CountryRepository;
import com.planit.holiday_keeper.global.exceptions.CustomException;
import com.planit.holiday_keeper.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CountryService {

  private final CountryRepository countryRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public List<Country> saveApiResponse(String jsonResponse) {
    try {
      List<AvailableCountriesApiResponse> responses = objectMapper.readValue(
          jsonResponse,
          new TypeReference<List<AvailableCountriesApiResponse>>(){}
      );

      for (AvailableCountriesApiResponse response : responses) {
        countryRepository.upsert(response.toEntity());
      }

      return countryRepository.findAll();

    } catch (Exception e) {
      log.error("국가 데이터 저장 중 에러 발생: {}", e.getMessage(), e);
      throw new RuntimeException("국가 데이터 저장 실패", e);
    }
  }


  public Country findByCountryCode(String countryCode) {
    if (countryCode == null) {
      throw new CustomException(ErrorCode.COUNTRY_CODE_REQUIRED);
    }
    return countryRepository.findByCountryCode(countryCode)
        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COUNTRY_CODE));
  }
}
