package com.planit.holiday_keeper.domain.holiday.repository;

import com.planit.holiday_keeper.domain.holiday.entity.Country;
import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

  @Query("SELECT h FROM Holiday h " +
      "WHERE (:year IS NULL OR h.holidayYear = :year) " +
      "AND (:countryCode IS NULL OR h.country.countryCode = :countryCode) " +
      "AND (:from IS NULL OR h.date >= :from) " +
      "AND (:to IS NULL OR h.date <= :to) " +
      "AND (:type IS NULL OR UPPER(h.types) LIKE UPPER(CONCAT('%', :type, '%'))) " +
      "AND (:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')))")
  Page<Holiday> findWithFilters(
      @Param("year") Integer year, @Param("countryCode") String countryCode,
      @Param("from") LocalDate from, @Param("to") LocalDate to,
      @Param("type") String type, @Param("name") String name,
      Pageable pageable
  );

  @Modifying
  @Query(value = """
        INSERT INTO holidays (
            country_id, date, name, local_name, holiday_year, 
            launch_year, global, counties, types, created_at, modified_at
        )
        VALUES (
            :#{#holiday.country.id}, :#{#holiday.date}, :#{#holiday.name}, 
            :#{#holiday.localName}, :#{#holiday.holidayYear}, :#{#holiday.launchYear},
            :#{#holiday.global}, :#{#holiday.counties}, :#{#holiday.types},
            :syncTime, :syncTime
        )
        ON DUPLICATE KEY UPDATE 
            local_name = VALUES(local_name),
            holiday_year = VALUES(holiday_year),
            launch_year = VALUES(launch_year),
            global = VALUES(global),
            counties = VALUES(counties),
            types = VALUES(types),
            modified_at = :syncTime
        """, nativeQuery = true)
  void upsert(@Param("holiday") Holiday holiday, @Param("syncTime") LocalDateTime syncTime);

  List<Holiday> findByCountryAndHolidayYear(Country country, int holidayYear);

  int deleteByCountryAndHolidayYear(Country country, int year);

  @Query("SELECT c.countryCode, c.name, COUNT(h.id) as holidayCount " +
      "FROM Holiday h JOIN h.country c " +
      "WHERE (:year IS NULL OR h.holidayYear = :year) " +
      "GROUP BY c.id")
  Page<Object[]> countHolidaysByCountry(@Param("year") int year, Pageable pageable);

  @Modifying
  @Query("DELETE FROM Holiday h WHERE h.country = :country AND h.holidayYear = :year AND h.modifiedAt < :syncTime")
  int deleteMissingHolidays(@Param("country") Country country, @Param("year") int year, @Param("syncTime") LocalDateTime syncTime);
}
