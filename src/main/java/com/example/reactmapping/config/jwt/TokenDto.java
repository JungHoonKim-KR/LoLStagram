package com.example.reactmapping.config.jwt;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class TokenDto {
    private String userEmail;
    private String accessToken;
    private String refreshToken;
}
