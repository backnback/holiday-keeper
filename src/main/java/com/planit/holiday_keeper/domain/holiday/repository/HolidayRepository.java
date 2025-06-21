package com.planit.holiday_keeper.domain.holiday.repository;

import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
}
