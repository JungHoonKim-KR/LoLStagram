package com.example.reactmapping.config.jwt;

import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.repository.RefreshTokenRepository;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userEmail ;
        String accessToken;
        final String authorization = request.getHeader("Authorization");
        log.info("authorization: {}", authorization);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("authorization is wrong");
            // 다음 filter로 넘어가야 다음 filter로 넘어감, permit URL인 경우엔 여기서 걸려도 doFilter를 해야 넘어감
            filterChain.doFilter(request, response);
            return;
        }else{
            accessToken = authorization.split(" ")[1];
            userEmail= (String) session.getAttribute(accessToken);
            log.info("요청자: "+ userEmail);
        }
        // OAuth2 로그인 요청 URL
        String oauth2LoginUrl = "/oauthLogin";
        // 요청 URL 확인
        String requestUrl = request.getRequestURI();
        log.info("요청 url: "+requestUrl);
        // 요청이 OAuth2 로그인 요청이면 필터의 처리를 건너뛰고 다음 필터로 이동
        if (requestUrl.equals(oauth2LoginUrl)) {
            filterChain.doFilter(request, response);
            return;
        }

        //Token 추출

//        userEmail= (String) session.getAttribute(accessToken);

        //AccessToken expired 여부
        if (jwtUtil.isExpired(accessToken, "ACCESS")) {
            log.error("AccessToken 만료");
            log.info("유저 아이디: "+ userEmail);
            //refreshToken이 유효한지
            if (refreshTokenRepository.findById(userEmail,"refreshToken").isPresent()) {
                //refreshToken이 정상적이라면
                    //accessToken 발급
                    log.info("RefreshToken 확인, AccessToken 재발급");
                    //accessToken 재발급하면 db에도 accessToken 업데이트 해야하는데 filter에 service 기능이 들어가야 하는게 마음에 안듬....
                    String newAccessToken = jwtUtil.createToken(userEmail, "ACCESS");
                    log.info("재발급 AccessToken : " + newAccessToken);
                    session.setAttribute(newAccessToken,userEmail);
                    response.setHeader("ACCESS", newAccessToken);
                    setAuthentication(userEmail, request, response, filterChain);
            }else{
                throw new AppException(ErrorCode.ACCESS_ERROR,"재로그인 필요");
            }
        }
        //AccessToken이 정상적이라면
        else {
            log.info("AccessToken 정상");
            userEmail = jwtUtil.getUserEmail(accessToken, "ACCESS");

            //로그인 토큰이라면 권한 부여
            setAuthentication(userEmail, request, response, filterChain);
        }

        filterChain.doFilter(request, response);
    }

    //사용자에게 권한 부여하는 메소드
    public void setAuthentication(String userEmail, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userEmail, null, List.of(new SimpleGrantedAuthority("USER"))); //내가 설정한 권한 기준이 없다면 null 해서 아무나 권한 받게 함
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //인증 서명
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);


    }
}
