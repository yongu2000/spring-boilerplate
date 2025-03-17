package com.boilerplate.boilerplate.global.exception;

import com.boilerplate.boilerplate.global.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // BusinessException 하위 모든 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    // 처리되지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        ErrorResponse response = ErrorResponse.of(GlobalError.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
