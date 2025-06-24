package com.planit.holiday_keeper.global.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // 공통 에러
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
  INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "타입이 올바르지 않습니다."),
  ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

  // Country 관련
  INVALID_COUNTRY_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 국가 코드입니다"),
  COUNTRY_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "국가 코드는 필수 입력값입니다."),

  // Holiday 관련
  INVALID_YEAR_RANGE(HttpStatus.BAD_REQUEST, "연도는 최근 5년 범위 내에서만 가능합니다."),
  YEAR_REQUIRED(HttpStatus.BAD_REQUEST, "연도는 필수 입력값입니다.");


  private final HttpStatus httpStatus;
  private final String message;
}
