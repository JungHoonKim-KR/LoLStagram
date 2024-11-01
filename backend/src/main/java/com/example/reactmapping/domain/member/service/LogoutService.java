package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.global.security.jwt.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LogoutService {
    private final TokenRepository tokenRepository;

    public void logout(String accessToken, String refreshToken) {

        //refreshToken 검증 후 삭제
        Optional<String> refreshTokenObject = tokenRepository.findToken(refreshToken,Token.TokenType.REFRESH.name());
        if(refreshTokenObject.isPresent()){
            tokenRepository.delete(refreshToken);
        }
        //accessToken 블랙리스트에 등록
        tokenRepository.registerBlacklist(accessToken);
        log.info("로그아웃");
    }
}
