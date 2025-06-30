package com.planit.holiday_keeper.domain.test.service;

import com.planit.holiday_keeper.domain.test.dto.ClusterDetail;
import com.planit.holiday_keeper.domain.test.dto.KMeansResultResponse;
import com.planit.holiday_keeper.global.exceptions.CustomException;
import com.planit.holiday_keeper.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class KMeansService {

  private final GoogleSheetsService googleSheetsService;


  public KMeansResultResponse analyzeData(int k) {
    String spreadsheetId = "1mCiLRi1f2gCCViIV_7hOburkOFxs0oZAoSsexAosoKU";
    String sheetName = "Sheet1";

    List<List<Object>> sheetData = googleSheetsService.getAllSheetData(spreadsheetId, sheetName);
    if (sheetData.isEmpty() || sheetData.size() == 1) {
      throw new CustomException(ErrorCode.NOT_ENOUGH_DATA);
    }

    List<double[]> processedData = processData(sheetData);

    return executeClustering(processedData, k);
  }


  private List<double[]> processData(List<List<Object>> sheetData) {
    List<List<Object>> rows = sheetData.subList(1, sheetData.size());
    log.info("총 {}개의 응답 데이터를 처리", rows.size());

    List<double[]> processedData = new ArrayList<>();
    int num = 1;
    for (List<Object> row : rows) {
      try {
        if (row.size() < 3) {
          log.warn("{}번째 행의 데이터가 부족하여 건너뜁니다. Row: {}", num, row);
          continue;
        }

        List<Double> data = new ArrayList<>();
        String competency = row.get(1).toString();
        if (competency.equals("기술력")) {
          data.add(1.0);
          data.add(0.0);
        } else if (competency.equals("소통능력")) {
          data.add(0.0);
          data.add(1.0);
        } else {
          log.warn("{}번째 행의 역량 데이터가 올바르지 않아 건너뜁니다: {}", num, competency);
          continue;
        }

        double learningSpeed = Double.parseDouble(row.get(2).toString());
        data.add(learningSpeed);

        processedData.add(data.stream().mapToDouble(d -> d).toArray());
        log.info("{}번째 행 처리 완료: {} -> {}", num, row, data);

      } catch (NumberFormatException e) {
        log.error("{}번째 행 데이터 변환 중 에러 발생: Row: {}", num, row, e);
      }
      num++;
    }

    return processedData;
  }


  private KMeansResultResponse executeClustering(List<double[]> processedData, int k) {
    if (processedData.isEmpty()) {
      throw new CustomException(ErrorCode.NOT_ENOUGH_DATA);
    }

    List<DoublePoint> doublePoints = processedData.stream()
        .map(DoublePoint::new)
        .collect(Collectors.toList());

    KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(k, 1000);
    List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(doublePoints);

    DistanceMeasure distanceMeasure = clusterer.getDistanceMeasure();

    List<ClusterDetail> clusterDetails = new ArrayList<>();
    int clusterId = 1;
    for (CentroidCluster<DoublePoint> cluster : clusters) {
      double[] center = cluster.getCenter().getPoint();
      List<DoublePoint> points = cluster.getPoints();

      double sumOfSquaredDistances = points.stream()
          .mapToDouble(point -> {
              double distance = distanceMeasure.compute(point.getPoint(), center);
              return Math.pow(distance, 2);
          })
          .sum();

      double variance = points.isEmpty() ? 0 : sumOfSquaredDistances / points.size();

      clusterDetails.add(ClusterDetail.builder()
          .clusterId(clusterId++)
          .center(center)
          .size(points.size())
          .variance(variance)
          .points(points.stream().map(DoublePoint::getPoint).toList())
          .build());
    }
    log.info("--- K-means 군집 분석 완료 (k={}) ---", k);
    clusterDetails.forEach(detail -> log.info("[Cluster {}] 중심점: {}, 멤버 수: {}, 분산: {}",
        detail.clusterId(), Arrays.toString(detail.center()), detail.size(), detail.variance()));
    log.info("------------------------------------");

    return KMeansResultResponse.builder()
        .totalPoints(doublePoints.size())
        .k(k)
        .clusters(clusterDetails)
        .build();
  }

}
