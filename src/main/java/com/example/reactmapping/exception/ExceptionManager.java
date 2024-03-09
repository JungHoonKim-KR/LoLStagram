package com.example.reactmapping.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionManager {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appExceptionHandler(AppException e){
        Map<String,Object> body = new HashMap<>();
        body.put("errorCode", e.getErrorCode().name());
        body.put("errorMessage", e.getMessage());
        return new ResponseEntity<>(body,e.getErrorCode().getHttpStatus());
    }
}
