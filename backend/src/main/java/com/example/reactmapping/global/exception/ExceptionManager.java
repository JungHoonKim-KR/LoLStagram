package com.example.reactmapping.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager{

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> appExceptionHandler(AppException e){
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode().name(), e.getMessage());
        return new ResponseEntity<>(errorResponse,e.getErrorCode().getHttpStatus());
    }
}
