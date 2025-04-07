package com.boilerplate.boilerplate.global.exception;

import com.boilerplate.boilerplate.domain.auth.jwt.exception.AuthenticationError;
import com.boilerplate.boilerplate.global.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // BusinessException 하위 모든 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(e);
        errorResponse.addDetail("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatus());
    }

    // DTO 에서 검증 실패할 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(GlobalError.INVALID_INPUT_VALUE);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage) //
            .findFirst()
            .orElse(GlobalError.INVALID_INPUT_VALUE.getMessage());
        errorResponse.addDetail("message", errorMessage);
        log.error("Validation Error: {}, {}", errorResponse.getMessage(), errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Request 입력값 에러
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.of(GlobalError.INVALID_INPUT_VALUE);
        String errorMessage = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .findFirst()
            .orElse(GlobalError.INVALID_INPUT_VALUE.getMessage());
        errorResponse.addDetail("message", errorMessage);
        log.error("Request Valid Error: {}, {}", errorResponse.getMessage(), errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.of(GlobalError.NOT_FOUND);
        errorResponse.addDetail("message", e.getMessage());
        log.error("Not Found = {}", errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponse errorResponse = ErrorResponse.of(AuthenticationError.ACCESS_DENIED);
        errorResponse.addDetail("message", e.getMessage());
        log.error("Access Denied = {}", errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // 처리되지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.of(GlobalError.INTERNAL_SERVER_ERROR);
        errorResponse.addDetail("message", e.getMessage());
        log.error("Unhandled Exception: ", e);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
