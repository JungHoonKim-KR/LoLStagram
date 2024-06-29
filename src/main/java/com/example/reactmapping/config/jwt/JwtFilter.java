package com.example.reactmapping.config.jwt;

import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.exception.ExceptionManager;
import com.example.reactmapping.norm.Auth;
import com.example.reactmapping.norm.Token;
import com.example.reactmapping.repository.RefreshTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
//OncePerRequestFilter 한 번만 동작
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ExceptionManager exceptionManager;
    private static class AuthenticationInfo{
        String userEmail;
        String accessToken;

        public AuthenticationInfo(String userEmail, String accessToken) {
            this.userEmail = userEmail;
            this.accessToken = accessToken;
        }
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String userEmail;
            String accessToken;
            // 요청 세션 얻기
            HttpSession session = request.getSession();
            String requestUrl = request.getRequestURI();
            log.info("요청 url : " + requestUrl);
            // 1 : 인증정보가 있는가
            AuthenticationInfo authenticationInfo = extractAuthorizationInfo(request, session);
            if(authenticationInfo == null){
                log.error("{} is wrong", Auth.KeyWord.Authentication.name());
                filterChain.doFilter(request, response);
                return;
            }else{
                userEmail = authenticationInfo.userEmail;
                accessToken = authenticationInfo.accessToken;
                log.info("요청자 이메일 : " + userEmail);
            }
            // end 1

            // 2 : 엑세스 토큰이 만료됐는가
            isExpiredAccessTokenTime(request, response, accessToken, userEmail, session);
            // end 2
            filterChain.doFilter(request, response);
        } catch (AppException e) {
            handleException(response, e);
        }
    }

    private void isExpiredAccessTokenTime(HttpServletRequest request, HttpServletResponse response, String accessToken, String userEmail, HttpSession session) {
        if (jwtUtil.isExpired(accessToken, Token.TokenType.ACCESS.name())) {
            log.error("{} 만료", Token.TokenName.accessToken.name());
            log.info("유저 아이디 : " + userEmail);
            // 3 : 리프레쉬 토큰이 만료됐는가
            isExpiredRefreshTokenTime(request, response, userEmail, session);
        } else {
            log.info("{} 정상", Token.TokenName.accessToken.name());
            userEmail = jwtUtil.getUserEmail(accessToken, Token.TokenType.ACCESS.name());
            setAuthentication(userEmail, request);
        }
    }

    private void isExpiredRefreshTokenTime(HttpServletRequest request, HttpServletResponse response, String userEmail, HttpSession session) {
        if (refreshTokenRepository.findById(userEmail, Token.TokenType.REFRESH.name()).isPresent()) {
            regenerateAccessToken(request, response, userEmail, session);
        } else {
            throw new AppException(ErrorCode.TOKEN_EXPIRED, "토큰 만료");
        }
    }

    private void regenerateAccessToken(HttpServletRequest request, HttpServletResponse response, String userEmail, HttpSession session) {
        log.info("{} 확인, {} 재발급", Token.TokenName.refreshToken.name(), Token.TokenName.accessToken.name());
        String newAccessToken = jwtUtil.createToken(userEmail, Token.TokenType.ACCESS.name());
        log.info("재발급 {} : " + newAccessToken, Token.TokenName.accessToken.name());

        // 새 토큰으로 업데이트
        session.setAttribute(newAccessToken, userEmail);
        response.setHeader(Token.TokenType.ACCESS.name(), newAccessToken);
        setAuthentication(userEmail, request);
    }

    private AuthenticationInfo extractAuthorizationInfo(HttpServletRequest request, HttpSession session){
        String authorization = request.getHeader(Auth.KeyWord.Authorization.name());
        log.info("{} : {}",Auth.KeyWord.Authorization.name(), authorization);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        } else {
            String accessToken = authorization.split(" ")[1];
            String userEmail= (String) session.getAttribute(accessToken);
            return new AuthenticationInfo(userEmail, accessToken);
        }
    }

    private void handleException(HttpServletResponse response, AppException e) throws IOException {
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(exceptionManager.createErrorResponse(e)));
    }

    public void setAuthentication(String userEmail, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userEmail, null, List.of(new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
