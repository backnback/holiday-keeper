package com.planit.holiday_keeper.domain.test.controller;

import com.planit.holiday_keeper.domain.test.dto.KMeansResultResponse;
import com.planit.holiday_keeper.domain.test.service.KMeansService;
import com.planit.holiday_keeper.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/k-means")
@RequiredArgsConstructor
@Tag(name = "통계 기반 군집 분석 API", description = "Google Form 설문 조사에 기반한 군집 분석 API")
public class ApiV1KMeansController {

  private final KMeansService kMeansService;

  @GetMapping("/run")
  @Operation(summary = "K-means 군집 분석 실행")
  public RsData<KMeansResultResponse> startClustering(
      @Parameter(description = "나눠야 하는 군집의 수")
      @RequestParam(defaultValue = "2") int k
  ) {
    KMeansResultResponse response = kMeansService.analyzeData(k);
    return new RsData<>("200", "군집 분석 성공 (k = %d)".formatted(k), response);
  }
}
