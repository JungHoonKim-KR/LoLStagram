package com.example.reactmapping.domain.member.controller;
import com.example.reactmapping.domain.member.dto.LoginInfo;
import com.example.reactmapping.domain.member.dto.LoginRequestDto;
import com.example.reactmapping.domain.member.dto.LoginResponseDto;
import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.domain.member.service.LoginService;
import com.example.reactmapping.global.security.cookie.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final CookieUtil cookieUtil;
    @Operation(summary = "로그인")
    @PostMapping("/normal")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto, HttpServletResponse httpServletResponse) {
        LoginInfo loginInfo = loginService.login(dto.getEmailId(), dto.getPassword());
        httpServletResponse.addCookie(cookieUtil.createCookie(Token.TokenName.refreshToken,loginInfo.getRefreshToken()));
        log.info("로그인 완료");
        return ResponseEntity.ok().body(new LoginResponseDto(loginInfo.getAccessToken(), loginInfo.getUsername(), loginInfo.getSummonerInfoDto(), loginInfo.getMemberDto()));
    }

    @PostMapping("/oauthLogin")
    public ResponseEntity<?> oauthLogin(@CookieValue(name = Token.TokenName.accessToken, defaultValue = "NO") String accessToken,
                                        HttpServletResponse response){
        LoginInfo loginInfo = loginService.socialLogin(accessToken);

        Cookie cookie = new Cookie(Token.TokenName.accessToken, "expiredAccessToken");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        log.info("로그인 완료");
        return ResponseEntity.ok().body(new LoginResponseDto(loginInfo.getAccessToken(), loginInfo.getUsername(), loginInfo.getSummonerInfoDto(), loginInfo.getMemberDto()));
    }
}
