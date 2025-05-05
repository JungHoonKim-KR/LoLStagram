package com.example.reactmapping.global.exception;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager{
    private final MeterRegistry meterRegistry;

    public ExceptionManager(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> appExceptionHandler(AppException e){
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode().name(), e.getMessage());
        return new ResponseEntity<>(errorResponse,e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException e){
        meterRegistry.counter("app_exception_total", "exception", e.getClass().getSimpleName()).increment();
        return new ResponseEntity<>(new ErrorResponse("RUNTIME_ERROR", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
