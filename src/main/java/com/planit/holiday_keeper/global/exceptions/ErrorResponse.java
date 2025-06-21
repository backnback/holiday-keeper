package com.planit.holiday_keeper.global.exceptions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
  private final LocalDateTime timestamp;
  private final int status;
  private final String error;
  private final String message;

  public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(errorCode.getHttpStatus().value())
            .error(errorCode.getHttpStatus().name())
            .message(errorCode.getMessage())
            .build()
        );
  }
}
