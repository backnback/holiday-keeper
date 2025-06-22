package com.planit.holiday_keeper.domain.holiday.entity;

import com.planit.holiday_keeper.domain.holiday.enums.HolidayTypes;
import com.planit.holiday_keeper.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "holidays")
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

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "holiday_counties",
      joinColumns = @JoinColumn(name = "holiday_id")
  )
  @Column(name = "county_code")
  @Builder.Default
  private List<String> counties = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id")
  private Country country;

  @ElementCollection(targetClass = HolidayTypes.class, fetch = FetchType.LAZY)
  @Enumerated(EnumType.STRING)
  @CollectionTable(
      name = "holiday_types",
      joinColumns = @JoinColumn(name = "holiday_id")
  )
  @Column(name = "type")
  @Builder.Default
  private List<HolidayTypes> types = new ArrayList<>();
}
