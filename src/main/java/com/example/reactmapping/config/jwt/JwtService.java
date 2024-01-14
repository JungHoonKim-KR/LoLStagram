package com.example.reactmapping.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;

    public TokenDto login(String userEmail)
    {
        TokenDto tokenDto = new TokenDto(userEmail,jwtUtil.createToken(userEmail, "ACCESS"), jwtUtil.createToken(userEmail, "REFRESH"));

        return tokenDto;
    }

}
