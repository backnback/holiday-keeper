package com.planit.holiday_keeper.domain.test.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ClusterDetail(
    int clusterId,
    double[] center,
    int size,
    double variance,
    List<double[]> points
) {

}
