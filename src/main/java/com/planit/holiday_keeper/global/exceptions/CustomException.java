package com.planit.holiday_keeper.global.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
  private final ErrorCode errorCode;
}
