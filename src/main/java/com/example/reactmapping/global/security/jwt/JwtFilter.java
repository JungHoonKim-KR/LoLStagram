package com.example.reactmapping.global.security.jwt;

import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.exception.ExceptionManager;
import com.example.reactmapping.global.norm.Auth;
import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.global.cookie.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

//인증 전 관문
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final ExceptionManager exceptionManager;
    private final CookieUtil cookieUtil;

    private static class AuthenticationInfo {
        String accessToken;
        String refreshToken;
        public AuthenticationInfo(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String userEmail;
            String accessToken;
            String refreshToken;
            // 요청 세션 얻기
//            HttpSession session = request.getSession();
            String requestUrl = request.getRequestURI();
            log.info("요청 url : " + requestUrl);
            // 1 : 인증정보가 있는가
            AuthenticationInfo authenticationInfo = extractAuthorizationInfo(request);
            if (authenticationInfo == null) {
                log.error("{} is wrong", Auth.KeyWord.Authentication.name());
                filterChain.doFilter(request, response);
                return;
            } else {
                accessToken = authenticationInfo.accessToken;
                refreshToken = authenticationInfo.refreshToken;
                log.info("accessToken : {}" , accessToken);
                log.info("refreshToken : {}" , refreshToken);
            }
            // end 1

            // 2 : 엑세스 토큰이 만료됐는가
            isExpiredAccessTokenTime(request, response, accessToken, refreshToken);
            // end 2
            filterChain.doFilter(request, response);
        } catch (AppException e) {
            handleException(response, e);
        }
    }
    private void isExpiredAccessTokenTime(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken) {
        if (jwtUtil.isExpired(accessToken)) {
            log.error("{} 만료", Token.TokenName.accessToken);
            // 3 : 리프레쉬 토큰이 만료됐는가
            isExpiredRefreshTokenTime(request, response, refreshToken);
        } else {
            log.info("{} 정상", Token.TokenName.accessToken);
            String userEmail = jwtUtil.getUserEmail(accessToken);
            setAuthentication(userEmail, request);
        }
    }

    private void isExpiredRefreshTokenTime(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        if (jwtService.findRefreshTokenBy(refreshToken).isPresent()) {
            regenerateAccessToken(request, response, jwtUtil.getUserEmail(refreshToken));
            log.info("{} 정상", Token.TokenName.refreshToken);
        } else {
            throw new AppException(ErrorCode.TOKEN_EXPIRED, "토큰 만료");
        }
    }

    private void regenerateAccessToken(HttpServletRequest request, HttpServletResponse response, String userEmail) {
        String newAccessToken = jwtService.regenerateAccessToken(userEmail);
        response.setHeader(Token.TokenType.ACCESS.name(), newAccessToken);
        setAuthentication(userEmail, request);
    }

    private AuthenticationInfo extractAuthorizationInfo(HttpServletRequest request) {
        String authorization = request.getHeader(Auth.KeyWord.Authorization.name());
        log.info("{} : {}", Auth.KeyWord.Authorization.name(), authorization);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        } else {
            String accessToken = authorization.split(" ")[1];
            String refreshToken = cookieUtil.getCookieValue(request,Token.TokenName.refreshToken);
            return new AuthenticationInfo(accessToken,refreshToken);
        }
    }

    private void handleException(HttpServletResponse response, AppException e) throws IOException {
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(exceptionManager.createErrorResponse(e)));
    }

    public void setAuthentication(String userEmail, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userEmail, null, List.of(new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
