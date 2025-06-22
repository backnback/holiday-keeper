package com.planit.holiday_keeper.domain.holiday.entity;

import com.planit.holiday_keeper.domain.holiday.enums.HolidayTypes;
import com.planit.holiday_keeper.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(
    name = "holidays",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_1",
        columnNames = {"country_id", "date", "name"}
    )
)
public class Holiday extends BaseEntity {

  @Column(nullable = false)
  private LocalDate date;

  @Column(length = 200)
  private String name;

  @Column(length = 200)
  private String localName;

  @Column(length = 10, nullable = false)
  private Integer holidayYear;

  @Column(length = 10)
  private Integer launchYear;

  private boolean global;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id")
  private Country country;

  @Column(length = 100)
  private String types;

  public List<HolidayTypes> getTypes() {
    if (types == null || types.trim().isEmpty()) {
      return List.of();
    }
    List<String> list = Arrays.asList(types.split(","));
    return HolidayTypes.fromStringList(list);
  }

  public void setTypes(List<String> list) {
    if (list == null || list.isEmpty()) {
      types = null;
    } else {
      types = String.join(",", HolidayTypes.validateList(list));
    }
  }

  @Column(length = 500)
  private String counties;

  public List<String> getCounties() {
    if (counties == null || counties.trim().isEmpty()) {
      return List.of();
    }
    return Arrays.asList(counties.split(","));
  }

  public void setCounties(List<String> list) {
    if (list == null || list.isEmpty()) {
      counties = null;
    } else {
      counties = String.join(",", list);
    }
  }
}
