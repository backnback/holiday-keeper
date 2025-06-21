package com.planit.holiday_keeper.domain.holiday.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planit.holiday_keeper.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "countries")
public class Country extends BaseEntity {

  @Column(length = 100)
  private String name;

  @Column(unique = true, length = 10, nullable = false)
  private String countryCode;

  @JsonIgnore
  @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Holiday> holidays = new ArrayList<>();
}
