package com.planit.holiday_keeper.global.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    log.error("CustomException: {}", e.getErrorCode().getMessage());
    return ErrorResponse.toResponseEntity(e.getErrorCode());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e) {
    log.error("Validation Exception: {}", e.getMessage());
    return ErrorResponse.toResponseEntity(ErrorCode.INVALID_INPUT_VALUE);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Unexpected Exception: {}", e.getMessage(), e);
    return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
  }
}
