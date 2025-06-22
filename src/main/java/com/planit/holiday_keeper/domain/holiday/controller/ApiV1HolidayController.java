package com.planit.holiday_keeper.domain.holiday.controller;

import com.planit.holiday_keeper.domain.holiday.dto.response.HolidayResponse;
import com.planit.holiday_keeper.domain.holiday.service.HolidayService;
import com.planit.holiday_keeper.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
@Tag(name = "공휴일 조회 API", description = "국가 및 연도별 공휴일 조회 API")
public class ApiV1HolidayController {

  private final HolidayService holidayService;


  @GetMapping
  @Operation(summary = "공휴일 목록 조회 (페이징, 필터, 검색, 날짜정렬 적용)")
  public RsData<Page<HolidayResponse>> getHolidays(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) String countryCode,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "asc") String sort
  ) {
    Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;

    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "date"));
    Page<HolidayResponse> response = holidayService.findWithFilters(
        year, countryCode, from, to, type, name, pageable
    );

    return new RsData<>("200", "공휴일 목록 조회 성공", response);
  }
}
