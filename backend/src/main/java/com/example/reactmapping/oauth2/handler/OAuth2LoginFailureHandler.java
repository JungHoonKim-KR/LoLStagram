package com.example.reactmapping.oauth2.handler;

import com.example.reactmapping.global.norm.URL;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    private final URL url;

    public OAuth2LoginFailureHandler(URL url) {
        this.url = url;
    }
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.info("로그인 실패: {}", exception.getMessage());

        // 실패 메시지 포함 (예: /login?error=access_denied)
        String targetUrl = String.format("%s/login?error=%s", url.getServer(), exception.getMessage());

        // 리다이렉트 수행
        response.sendRedirect(targetUrl);
    }

}
