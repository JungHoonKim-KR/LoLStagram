package com.example.reactmapping.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
}
