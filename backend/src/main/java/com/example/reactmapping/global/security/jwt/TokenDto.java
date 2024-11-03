package com.example.reactmapping.global.security.jwt;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class TokenDto {
    private String userEmail;
    private String accessToken;
    private String refreshToken;
}
