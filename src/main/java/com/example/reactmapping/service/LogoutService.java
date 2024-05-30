package com.example.reactmapping.service;

import com.example.reactmapping.object.RefreshToken;
import com.example.reactmapping.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogoutService implements LogoutHandler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        //Token 추출
//        String token = authorization.split(" ")[1];
        String emailId=request.getHeader("emailId");

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(emailId, "refreshToken");
        if(refreshToken.isPresent()){
            refreshTokenRepository.delete(emailId,"refreshToken");
        }
        request.getSession().invalidate();
        log.info(emailId+"님 로그아웃");

    }
}
