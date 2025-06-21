package com.planit.holiday_keeper.domain.holiday.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
