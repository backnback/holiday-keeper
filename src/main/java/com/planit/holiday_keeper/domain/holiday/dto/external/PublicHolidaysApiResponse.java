package com.planit.holiday_keeper.domain.holiday.dto.external;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.planit.holiday_keeper.domain.holiday.entity.Country;
import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;


@Schema(description = "특정 연도 및 국가별 공휴일 API 응답")
@JsonIgnoreProperties(ignoreUnknown = true)
public record PublicHolidaysApiResponse (

    @Schema(description = "공휴일 날짜", example = "2025-10-13")
    LocalDate date,

    @Schema(description = "공휴일 원본명", example = "Columbus Day")
    String localName,

    @Schema(description = "공휴일 영문명", example = "Columbus Day")
    String name,

    @Schema(description = "국가 코드", example = "US")
    String countryCode,

    //외부 API 문서에서 deprecated 예정 확인 제외
    //boolean fixed;

    @Schema(description = "전세계 공통 여부", example = "false")
    Boolean global,

    @Schema(description = "해당 공휴일 적용 지역 목록")
    List<String> counties,

    @Schema(description = "공식 적용 시작 연도") // API 문서 Integer
    Integer launchYear,

    @Schema(description = "공휴일 유형 목록")
    List<String> types

) {
    public Holiday toEntity(Country country) {
        Holiday holiday = Holiday.builder()
            .date(date)
            .name(name)
            .localName(localName)
            .country(country)
            .holidayYear(date.getYear())
            .global(global != null ? global : false)
            .launchYear(launchYear)
            .build();

        holiday.setCounties(counties);
        holiday.setTypes(types);
        return holiday;
    }
}
