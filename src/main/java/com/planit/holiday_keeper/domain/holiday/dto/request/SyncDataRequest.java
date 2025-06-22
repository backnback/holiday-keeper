package com.planit.holiday_keeper.domain.holiday.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "특정 연도 및 국가의 데이터 동기화")
public record SyncDataRequest(

    @Schema(description = "연도", example = "2025")
    @NotNull
    Integer year,

    @Schema(description = "국가 코드", example = "KR")
    @NotNull
    String countyCode
) {
}
