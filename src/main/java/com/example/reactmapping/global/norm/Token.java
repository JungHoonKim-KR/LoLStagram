package com.example.reactmapping.global.norm;

import lombok.Getter;
public class Token{
    public enum TokenType{
        ACCESS,REFRESH;
    }
    public enum TokenTime{
        accessToken(10 * 1000 * 60L),
        refreshToken(60*1000*60L);
        @Getter
        private final long expiredTime;

        TokenTime(long expiredTime){
            this.expiredTime = expiredTime;
        }
    }
    // 어노테이션의 변수로 쓰기 위해서는 constant 문제로 컴파일 전에 상수 정의가 되어 있어야 함
    public static class TokenName{
        public static final String accessToken = "accessToken";
        public static final String refreshToken = "refreshToken";
    }

}

