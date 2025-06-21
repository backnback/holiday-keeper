package com.planit.holiday_keeper.domain.holiday.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "가능한 국가 목록 API 응답")
public record AvailableCountriesApiResponse(

    @Schema(description = "국가 코드", example = "KR")
    String countryCode,

    @Schema(description = "국가 이름", example = "South Korea")
    String name
) {}
