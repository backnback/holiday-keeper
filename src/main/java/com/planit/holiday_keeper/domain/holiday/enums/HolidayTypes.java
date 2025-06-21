package com.planit.holiday_keeper.domain.holiday.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum HolidayTypes {

  PUBLIC("법정 공휴일 - 정부에서 정한 공식 휴일"),
  BANK("은행 휴무일"),
  SCHOOL("학교 휴무일"),
  AUTHORITIES("관공서 휴무일"),
  OPTIONAL("선택적 휴무일"),
  OBSERVANCE("기념일");

  private final String description;

  public static HolidayTypes fromString(String type) {
    if (type == null || type.trim().isEmpty()) return null;

    try {
      return HolidayTypes.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }


  public static List<HolidayTypes> fromStringList(List<String> types) {
    if (types == null || types.isEmpty()) {
      return new ArrayList<>();
    }

    List<HolidayTypes> newTypes = new ArrayList<>();
    for (String type : types) {
      newTypes.add(fromString(type));
    }
    return newTypes;
  }
}
