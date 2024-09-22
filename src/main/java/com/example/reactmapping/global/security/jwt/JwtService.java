package com.example.reactmapping.global.security.jwt;

import com.example.reactmapping.global.norm.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public TokenDto generateToken(String userEmail){
        TokenDto tokenDto = new TokenDto(userEmail,jwtUtil.createToken(userEmail, Token.TokenType.ACCESS.name()), jwtUtil.createToken(userEmail, Token.TokenType.REFRESH.name()));
        tokenRepository.save(tokenDto.getRefreshToken(), Token.TokenType.REFRESH.name());
        return tokenDto;
    }
    public Optional<String> findToken(String token, String type){
         return tokenRepository.findToken(token, type);
    }

    public Boolean isBlacklisted(String accessToken){
        String blackList = tokenRepository.findBlackList(accessToken);
        if(blackList != null){
            return accessToken.equals(blackList);
        }
        return false;
    }

    public String regenerateAccessToken(String userEmail) {
        log.info("{} 확인, {} 재발급", Token.TokenName.refreshToken, Token.TokenName.accessToken);
        String newAccessToken = jwtUtil.createToken(userEmail, Token.TokenType.ACCESS.name());
        log.info("재발급 {} : " + newAccessToken, Token.TokenName.accessToken);
        return newAccessToken;
    }
}
