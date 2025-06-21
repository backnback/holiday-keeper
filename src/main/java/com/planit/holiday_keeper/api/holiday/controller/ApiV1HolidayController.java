package com.planit.holiday_keeper.api.holiday.controller;

import com.planit.holiday_keeper.domain.holiday.service.HolidayService;
import com.planit.holiday_keeper.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
@Tag(name = "테스트 API", description = "테스트")
public class ApiV1HolidayController {

  private final HolidayService holidayService;

  @GetMapping
  @Operation(summary = "Swagger 테스트")
  public RsData<Void> hello() {
    return new RsData<>("200", "테스트 성공");
  }
}
