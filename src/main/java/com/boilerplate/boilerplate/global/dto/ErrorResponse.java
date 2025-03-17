package com.boilerplate.boilerplate.global.dto;

import com.boilerplate.boilerplate.global.exception.BusinessException;
import com.boilerplate.boilerplate.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final String message;
    private final int status;
    private final String code;
    private final LocalDateTime timestamp;
    private final Map<String, Object> details;

    public static ErrorResponse of(BusinessException e) {
        return ErrorResponse.builder()
            .message(e.getMessage())
            .status(e.getErrorCode().getStatus().value())
            .code(e.getErrorCode().name())
            .timestamp(LocalDateTime.now())
            .details(new HashMap<>())
            .build();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .message(errorCode.getMessage())
            .status(errorCode.getStatus().value())
            .code(errorCode.name())
            .timestamp(LocalDateTime.now())
            .details(new HashMap<>())
            .build();
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
}