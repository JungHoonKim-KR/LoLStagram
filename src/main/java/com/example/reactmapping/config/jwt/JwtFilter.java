package com.example.reactmapping.config.jwt;

import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.exception.ExceptionManager;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            String userEmail ;
            String accessToken;
            final String authorization = request.getHeader("Authorization");
            log.info("authorization: {}", authorization);
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                log.error("authorization is wrong");
                filterChain.doFilter(request, response);
                return;
            } else {
                accessToken = authorization.split(" ")[1];
                userEmail= (String) session.getAttribute(accessToken);
                log.info("요청자: " + userEmail);
            }

            String oauth2LoginUrl = "/oauthLogin";
            String requestUrl = request.getRequestURI();
            log.info("요청 url: " + requestUrl);
            if (requestUrl.equals(oauth2LoginUrl)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtUtil.isExpired(accessToken, "ACCESS")) {
                log.error("AccessToken 만료");
                log.info("유저 아이디: " + userEmail);
                if (refreshTokenRepository.findById(userEmail, "refreshToken").isPresent()) {
                    log.info("RefreshToken 확인, AccessToken 재발급");
                    String newAccessToken = jwtUtil.createToken(userEmail, "ACCESS");
                    log.info("재발급 AccessToken : " + newAccessToken);
                    session.setAttribute(newAccessToken, userEmail);
                    response.setHeader("ACCESS", newAccessToken);
                    setAuthentication(userEmail, request, response, filterChain);
                } else {
                    throw new AppException(ErrorCode.TOKEN_EXPIRED, "토큰 만료");
                }
            } else {
                log.info("AccessToken 정상");
                userEmail = jwtUtil.getUserEmail(accessToken, "ACCESS");
                setAuthentication(userEmail, request, response, filterChain);
            }

            filterChain.doFilter(request, response);
        } catch (AppException e) {
            handleException(response, e);
        }
    }

    private void handleException(HttpServletResponse response, AppException e) throws IOException {
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(exceptionManager.createErrorResponse(e)));
    }

    public void setAuthentication(String userEmail, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userEmail, null, List.of(new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
