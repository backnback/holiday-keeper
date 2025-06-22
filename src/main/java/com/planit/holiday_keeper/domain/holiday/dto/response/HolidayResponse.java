package com.planit.holiday_keeper.domain.holiday.dto.response;

import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
import com.planit.holiday_keeper.domain.holiday.enums.HolidayTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Schema(description = "특정 연도 및 국가별 공휴일 목록 조회 응답")
@Builder
public record HolidayResponse(

    @Schema(description = "공휴일 날짜", example = "2025-10-13")
    @NotNull LocalDate date,

    @Schema(description = "공휴일 원본명", example = "Columbus Day")
    String localName,

    @Schema(description = "공휴일 영문명", example = "Columbus Day")
    String name,

    @Schema(description = "국가 코드", example = "US")
    String countryCode,

    @Schema(description = "전세계 공통 여부", example = "false")
    Boolean global,

    @Schema(description = "해당 공휴일 적용 지역 목록")
    List<String> counties,

    @Schema(description = "공식 적용 시작 연도") // API 문서 Integer
    Integer launchYear,

    @Schema(description = "공휴일 유형 목록")
    List<HolidayTypes> types

) {
  public static HolidayResponse of(Holiday holiday) {
    return HolidayResponse.builder()
        .date(holiday.getDate())
        .localName(holiday.getLocalName())
        .name(holiday.getName())
        .countryCode(holiday.getCountry().getCountryCode())
        .global(holiday.isGlobal())
        .counties(new ArrayList<>(holiday.getCounties()))
        .launchYear(holiday.getLaunchYear())
        .types(new ArrayList<>(holiday.getTypes()))
        .build();
  }
}
