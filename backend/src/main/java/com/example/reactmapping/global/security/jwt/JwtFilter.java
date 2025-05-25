package com.example.reactmapping.global.security.jwt;

import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.exception.ErrorResponse;
import com.example.reactmapping.global.exception.ExceptionManager;
import com.example.reactmapping.global.norm.Auth;
import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.global.norm.URL;
import com.example.reactmapping.global.security.cookie.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//인증 전 관문
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
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
            String accessToken;
            String refreshToken;
            // 요청 세션 얻기
//            HttpSession session = request.getSession();
            String requestUrl = request.getRequestURI();
            log.info("요청 url : " + requestUrl);
            // 1 : 인증정보가 있는가
            AuthenticationInfo authenticationInfo = extractAuthorizationInfo(request);
            if (authenticationInfo == null) {
                if (isPermittedPath(requestUrl)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                log.warn("{} is wrong", Auth.Authentication.name());
                throw new AppException(ErrorCode.ACCESS_ERROR, "권한 없음");
            } else {
                accessToken = authenticationInfo.accessToken;
                refreshToken = authenticationInfo.refreshToken;
            }
            // end 1
            if (isLogout(request, response, filterChain, requestUrl, accessToken)) return;

            // 2 : 엑세스 토큰이 만료됐는가
            isTokenValid(request,accessToken, response, refreshToken);

            filterChain.doFilter(request, response);
        } catch (AppException e) {
            handleException(response, e);
        }
    }

    private static boolean isLogout(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String requestUrl, String accessToken) throws IOException, ServletException {
        if(requestUrl.equals("/logout")){
            Authentication authentication = new UsernamePasswordAuthenticationToken(accessToken, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private void isTokenValid(HttpServletRequest request, String accessToken, HttpServletResponse response, String refreshToken) throws IOException {
        String userEmail = null;

        // 엑세스 토큰이 만료
        if (jwtUtil.isExpired(accessToken)) {
            userEmail = jwtUtil.getUserEmail(accessToken);
            // 리프레쉬가 만료됐는가?
            if (jwtUtil.isExpired(refreshToken)) {
                throw new AppException(ErrorCode.BAD_REQUEST, "잘못된 로그인 요청입니다.");
            }  else if (jwtService.findToken(refreshToken, Token.TokenType.REFRESH.name()).isPresent()) {
                regenerateAccessToken(request, response, userEmail);
            } else {
                throw new AppException(ErrorCode.NOTFOUND, "잘못된 토큰 정보입니다.");
            }

        }else if (jwtService.isBlacklisted(accessToken)) {
            log.warn("{} 로그아웃 처리된 토큰", accessToken);
            throw new AppException(ErrorCode.BAD_REQUEST, "이미 로그아웃된 토큰입니다.");
        }else{
            setAuthentication(userEmail, request);
        }
    }

    private Boolean isPermittedPath(String url) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String path : URL.Permit.PATHS) {
            if (pathMatcher.match(path, url)) {
                log.info("확인~!");
                return true;
            }
        }
        return false;
    }

    private void regenerateAccessToken(HttpServletRequest request, HttpServletResponse response, String userEmail) {
        String newAccessToken = jwtService.regenerateAccessToken(userEmail);
        response.setHeader(Token.TokenType.ACCESS.name(), newAccessToken);
        setAuthentication(userEmail, request);
        log.info("토큰 재발급");
    }

    private AuthenticationInfo extractAuthorizationInfo(HttpServletRequest request) {
        String authorization = request.getHeader(Auth.Authorization.name());
//        log.info("{} : {}", Auth.Authorization.name(), authorization);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        } else {
            String accessToken = authorization.split(" ")[1];
            String refreshToken = cookieUtil.getCookieValue(request, Token.TokenName.refreshToken);
            return new AuthenticationInfo(accessToken, refreshToken);
        }
    }

    private void handleException(HttpServletResponse response, AppException e) throws IOException {
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode().name(), e.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

    public void setAuthentication(String userEmail, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userEmail, null, List.of(new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
