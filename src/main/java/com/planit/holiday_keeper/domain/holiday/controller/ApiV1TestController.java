package com.planit.holiday_keeper.domain.holiday.controller;

import com.planit.holiday_keeper.domain.holiday.service.HolidayService;
import com.planit.holiday_keeper.global.rsData.RsData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Tag(name = "테스트 용 임시 API", description = "테스트 활동 도우미 API")
@Profile("test")
public class ApiV1TestController {

  private final HolidayService holidayService;

  @PatchMapping("/holidays/{count}")
  public RsData<Void> modifyHolidayNames(
      @PathVariable int count,
      @RequestParam String countryCode,
      @RequestParam int year
  ) {
    int updated = holidayService.updateHolidayNames(countryCode, year, count);
    return new RsData<>("204", "%d개 이름 변경 완료".formatted(updated));
  }


  @DeleteMapping("/holidays/{count}")
  public RsData<Void> deleteMultipleHolidays(
      @PathVariable int count,
      @RequestParam String countryCode,
      @RequestParam int year
  ) {
    int deleted = holidayService.deleteHoliday(countryCode, year, count);
    return new RsData<>("204", "%d개 삭제 완료".formatted(deleted));
  }
}
