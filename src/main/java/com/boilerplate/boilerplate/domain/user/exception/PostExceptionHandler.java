package com.boilerplate.boilerplate.domain.user.exception;

import com.boilerplate.boilerplate.domain.post.controller.PostController;
import com.boilerplate.boilerplate.global.dto.ErrorResponse;
import com.boilerplate.boilerplate.global.exception.GlobalError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PostController.class)
public class PostExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handleNumberFormatException(NumberFormatException ex) {
        ErrorResponse errorResponse = ErrorResponse.of(GlobalError.NOT_FOUND);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
