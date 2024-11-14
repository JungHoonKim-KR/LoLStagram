package com.example.reactmapping.global.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 부모 클래스의 message 필드 사용
        this.errorCode = errorCode;
    }

    // 커스텀 메시지 필요시
    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage); // 부모 클래스의 message 필드 사용
        this.errorCode = errorCode;
    }
}
