package com.planit.holiday_keeper.domain.holiday.repository;

import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
import com.planit.holiday_keeper.domain.holiday.enums.HolidayTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

  @Query("SELECT h FROM Holiday h " +
      "LEFT JOIN h.types t " +
      "WHERE (:year IS NULL OR h.holidayYear = :year) " +
      "AND (:countryCode IS NULL OR h.country.countryCode = :countryCode) " +
      "AND (:from IS NULL OR h.date >= :from) " +
      "AND (:to IS NULL OR h.date <= :to) " +
      "AND (:type IS NULL OR t = :type) " +
      "AND (:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')))")
  Page<Holiday> findWithFilters(
      @Param("year") Integer year, @Param("countryCode") String countryCode,
      @Param("from") LocalDate from, @Param("to") LocalDate to,
      @Param("type") HolidayTypes type, @Param("name") String name,
      Pageable pageable
  );
}
