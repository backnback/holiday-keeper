package com.planit.holiday_keeper.domain.holiday.repository;

import com.planit.holiday_keeper.domain.holiday.entity.Holiday;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomQueryRepository {
  void bulkUpsert(List<Holiday> holidays, LocalDateTime syncTime);
}
