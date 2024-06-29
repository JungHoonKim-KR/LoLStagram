package com.example.reactmapping.config.jwt;

import com.example.reactmapping.norm.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;

    public TokenDto login(String userEmail)
    {
        TokenDto tokenDto = new TokenDto(userEmail,jwtUtil.createToken(userEmail, Token.TokenType.ACCESS.name()), jwtUtil.createToken(userEmail, Token.TokenType.REFRESH.name()));

        return tokenDto;
    }

}
