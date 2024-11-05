package com.example.reactmapping.global.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum ErrorCode {
    NOTFOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    DUPLICATED(HttpStatus.CONFLICT, "중복된 리소스입니다."),
    // 잘못된 접근
    ACCESS_ERROR(HttpStatus.UNAUTHORIZED, "잘못된 접근입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "로그아웃된 토큰");

    private final HttpStatus httpStatus;
    private final String message;
}
