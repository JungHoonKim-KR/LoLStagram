package com.example.reactmapping.norm;

public enum Token {
    INFO(10 * 1000 * 60L, 60 * 1000 * 60L);
    private final Long accessExpiredTime;
    private final Long refreshExpiredTime;

    public Long getAccessTokenTime() {
        return accessExpiredTime;
    }

    public Long getRefreshTokenTime() {
        return refreshExpiredTime;
    }

    Token(Long accessExpiredTime, Long refreshExpiredTime) {
        this.accessExpiredTime = accessExpiredTime;
        this.refreshExpiredTime = refreshExpiredTime;
    }
}
