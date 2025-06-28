package com.planit.holiday_keeper.domain.holiday.repository;

import com.planit.holiday_keeper.domain.holiday.entity.Holiday;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomQueryRepositoryImpl implements CustomQueryRepository {

  private final EntityManager entityManager;

  @Override
  public void bulkUpsert(List<Holiday> holidays, LocalDateTime syncTime) {
    String sql = """
        INSERT INTO holidays (
            country_id, date, name, local_name, holiday_year,
            launch_year, global, counties, types, created_at, modified_at
        )
        VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        )
        ON DUPLICATE KEY UPDATE
            local_name = VALUES(local_name),
            holiday_year = VALUES(holiday_year),
            launch_year = VALUES(launch_year),
            global = VALUES(global),
            counties = VALUES(counties),
            types = VALUES(types),
            modified_at = ?
        """;

    entityManager.unwrap(org.hibernate.Session.class).doWork(connection -> {
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        Timestamp timestamp = Timestamp.valueOf(syncTime);
        for (Holiday holiday : holidays) {
          statement.setLong(1, holiday.getCountry().getId());
          statement.setObject(2, holiday.getDate());
          statement.setString(3, holiday.getName());
          statement.setString(4, holiday.getLocalName());
          checkIntNull(statement, 5, holiday.getHolidayYear());
          checkIntNull(statement, 6, holiday.getLaunchYear());
          statement.setBoolean(7, holiday.isGlobal());
          statement.setString(8, holiday.getCounties());
          statement.setString(9, holiday.getTypes());
          statement.setTimestamp(10, timestamp);
          statement.setTimestamp(11, timestamp);
          statement.setTimestamp(12, timestamp);
          statement.addBatch();
        }
        statement.executeBatch();
      }
    });
  }

  private void checkIntNull(PreparedStatement statement, int index, Integer value) throws SQLException {
    if (value != null) {
      statement.setInt(index, value);
    } else {
      statement.setNull(index, java.sql.Types.INTEGER);
    }
  }
}
