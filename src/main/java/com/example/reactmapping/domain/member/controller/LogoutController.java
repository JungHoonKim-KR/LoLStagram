package com.example.reactmapping.domain.member.controller;

import com.example.reactmapping.domain.member.service.LogoutService;
import com.example.reactmapping.global.norm.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest, @CookieValue(name = Token.TokenName.accessToken) String accessToken,
                                       @CookieValue(name = Token.TokenName.refreshToken) String refreshToken) {

        logoutService.logout(accessToken, refreshToken);
        httpServletRequest.getSession().invalidate();
        return ResponseEntity.ok("logout");
    }
}
