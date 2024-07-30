package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.global.security.jwt.RefreshTokenRepository;
import com.example.reactmapping.global.security.cookie.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LogoutService implements LogoutHandler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = cookieUtil.getCookieValue(request,Token.TokenName.refreshToken);
        Optional<String> refreshTokenObject = refreshTokenRepository.findByRefreshToken(refreshToken);
        if(refreshTokenObject.isPresent()){
            refreshTokenRepository.delete(refreshToken);
        }
        request.getSession().invalidate();
        log.info("로그아웃");
    }
}
