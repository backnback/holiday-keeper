package com.planit.holiday_keeper.domain.holiday.controller;

import com.planit.holiday_keeper.domain.holiday.dto.request.SyncDataRequest;
import com.planit.holiday_keeper.domain.holiday.dto.response.CountryHolidayCountResponse;
import com.planit.holiday_keeper.domain.holiday.dto.response.HolidayResponse;
import com.planit.holiday_keeper.domain.holiday.entity.Country;
import com.planit.holiday_keeper.domain.holiday.scheduler.FetchHolidayScheduler;
import com.planit.holiday_keeper.domain.holiday.service.CountryService;
import com.planit.holiday_keeper.domain.holiday.service.HolidayService;
import com.planit.holiday_keeper.global.globalDto.PageResponse;
import com.planit.holiday_keeper.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
@Tag(name = "공휴일 조회 API", description = "국가 및 연도별 공휴일 조회 API")
public class ApiV1HolidayController {

  private final HolidayService holidayService;
  private final CountryService countryService;
  private final FetchHolidayScheduler fetchHolidayScheduler;


  @GetMapping
  @Operation(summary = "공휴일 목록 조회 (페이징, 필터, 검색, 날짜정렬 적용)")
  public RsData<PageResponse<HolidayResponse>> getHolidays(
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

    return new RsData<>("200", "공휴일 목록 조회 성공", PageResponse.of(response));
  }


  @PostMapping("/sync")
  @Operation(summary = "특정 연도 및 국가 데이터 동기화 (Refresh)")
  public RsData<Void> syncHolidays(@Valid @RequestBody SyncDataRequest request) {
    Country country = countryService.findByCountryCode(request.countryCode());
    fetchHolidayScheduler.fetchByYearAndCountry(country, request.year());
    return new RsData<>("204", "동기화 완료");
  }


  @DeleteMapping("/{countryCode}/{year}")
  @Operation(summary = "특정 연도 및 국가 데이터 전체 삭제")
  public RsData<Void> deleteHolidays(
      @PathVariable Integer year, @PathVariable String countryCode
  ) {
    Country country = countryService.findByCountryCode(countryCode);
    int deleteCount = holidayService.deleteAllBy(country, year);
    return new RsData<>("204", "%d개 데이터 삭제 완료".formatted(deleteCount));
  }


  @GetMapping("/countries")
  @Operation(summary = "특정 연도의 나라별 공휴일 개수 조회")
  public RsData<PageResponse<CountryHolidayCountResponse>> getCountByCountry(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "30") int size,
      @RequestParam(defaultValue = "2025") int year,
      @RequestParam(defaultValue = "desc") String sort
  ) {
    Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;

    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "holidayCount"));
    Page<CountryHolidayCountResponse> response = holidayService.getCountByCountry(year, pageable);

    return new RsData<>("200", "나라별 공휴일 개수 조회 성공", PageResponse.of(response));
  }
}
