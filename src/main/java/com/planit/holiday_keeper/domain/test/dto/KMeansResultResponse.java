package com.planit.holiday_keeper.domain.test.dto;

import lombok.Builder;

import java.util.List;


@Builder
public record KMeansResultResponse(
  int totalPoints,
  int k,
  List<ClusterDetail> clusters
) {
}
