package com.example.reactmapping.service;

import com.example.reactmapping.config.jwt.JwtUtil;
import com.example.reactmapping.entity.AccessToken;
import com.example.reactmapping.entity.RefreshToken;
import com.example.reactmapping.repository.BlackListRepository;
import com.example.reactmapping.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogoutService implements LogoutHandler {
    private final BlackListRepository blackListRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Token 추출
        String token = authorization.split(" ")[1];

        String refreshToken = jwtUtil.getTokenType(token, "REFRESH", response);

        //refreshToken 삭제
        Optional<RefreshToken> refreshTokenByRefreshToken = refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken);
        if(refreshTokenByRefreshToken.isPresent()){
            log.info("refreshToken 존재");
            refreshTokenRepository.deleteRefreshTokenByRefreshToken(refreshToken);
            log.info("refreshToken 삭제");

        }

        String accessToken = request.getHeader("ACCESS");
        String userEmail = jwtUtil.getUserEmail(accessToken, "ACCESS");

        Optional<AccessToken> accessTokenByEmailId = blackListRepository.findAccessTokenByEmailId(userEmail);
        AccessToken accessTokenEntity;
        //이 유저가 블랙리스트에 전적에 있다면
        if(accessTokenByEmailId.isPresent()){
            accessTokenEntity = accessTokenByEmailId.get();
            accessTokenEntity.updateToken(token);
        }
        else{
            accessTokenEntity = new AccessToken(userEmail, accessToken);
        }
        //블랙리스트에 accessToken 추가
        blackListRepository.save(accessTokenEntity);
        log.info("로그아웃 " + userEmail);

    }
}
