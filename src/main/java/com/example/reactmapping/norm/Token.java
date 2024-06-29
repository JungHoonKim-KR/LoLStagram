package com.example.reactmapping.norm;

import lombok.Getter;
public class Token{
    public enum TokenType{
        ACCESS,REFRESH;
    }
    public enum TokenName{
        accessToken,refreshToken;
    }

    @Getter
    public enum TokenTime {
        INFO(10 * 1000 * 60L, 60 * 1000 * 60L);

        private final Long accessExpiredTime;
        private final Long refreshExpiredTime;

        TokenTime(Long accessExpiredTime, Long refreshExpiredTime) {
            this.accessExpiredTime = accessExpiredTime;
            this.refreshExpiredTime = refreshExpiredTime;
        }
    }
}

