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
  COUNTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "국가를 찾을 수 없습니다"),
  INVALID_COUNTRY_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 국가 코드입니다"),

  // Holiday 관련
  HOLIDAY_NOT_FOUND(HttpStatus.NOT_FOUND, "공휴일을 찾을 수 없습니다"),
  INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜 범위입니다"),

  // HolidayTypes
  INVALID_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않는 타입입니다"),

  // Scheduler
  FAILED_API_CALL(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 호출에 실패했습니다"),
  DATA_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 동기화에 실패했습니다");

  private final HttpStatus httpStatus;
  private final String message;
}
