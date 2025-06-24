package com.planit.holiday_keeper.domain.holiday.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Schema(description = "국가별 공휴일 개수 목록 응답")
@Builder
public record CountryHolidayCountResponse(

    @Schema(description = "국가 코드", example = "KR")
    String countryCode,

    @Schema(description = "국가 영문명", example = "South Korea")
    String name,

    @Schema(description = "공휴일 개수", example = "15")
    Long holidayCount

) {
  public static CountryHolidayCountResponse of(String countryCode, String name, Long holidayCount) {
    return CountryHolidayCountResponse.builder()
        .countryCode(countryCode)
        .name(name)
        .holidayCount(holidayCount)
        .build();
  }
}
