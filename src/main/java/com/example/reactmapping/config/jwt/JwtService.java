package com.example.reactmapping.config.jwt;

import com.example.reactmapping.norm.Token;
import com.example.reactmapping.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenDto generateToken(String userEmail){
        TokenDto tokenDto = new TokenDto(userEmail,jwtUtil.createToken(userEmail, Token.TokenType.ACCESS.name()), jwtUtil.createToken(userEmail, Token.TokenType.REFRESH.name()));
        refreshTokenRepository.save(tokenDto.getRefreshToken());
        return tokenDto;
    }

    public Optional<String> findRefreshTokenBy(String refreshToken){
         return refreshTokenRepository.findByRefreshToken(refreshToken);
    }
    public String regenerateAccessToken(String userEmail) {
        log.info("{} 확인, {} 재발급", Token.TokenName.refreshToken, Token.TokenName.accessToken);
        String newAccessToken = jwtUtil.createToken(userEmail, Token.TokenType.ACCESS.name());
        log.info("재발급 {} : " + newAccessToken, Token.TokenName.accessToken);
        return newAccessToken;
    }
}
