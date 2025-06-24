package com.planit.holiday_keeper.domain.holiday.repository;

import com.planit.holiday_keeper.domain.holiday.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

  Optional<Country> findByCountryCode(String countryCode);

  @Modifying
  @Query(value = """
        INSERT INTO countries (country_code, name, created_at, modified_at)
        VALUES (:#{#country.countryCode}, :#{#country.name}, NOW(), NOW())
        ON DUPLICATE KEY UPDATE 
            name = VALUES(name),
            modified_at = NOW()
        """, nativeQuery = true)
  void upsert(@Param("country") Country country);
}
