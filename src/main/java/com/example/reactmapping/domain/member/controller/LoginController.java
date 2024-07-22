package com.example.reactmapping.domain.member.controller;
import com.example.reactmapping.domain.member.dto.LoginRequestDto;
import com.example.reactmapping.domain.member.dto.LoginResponseDto;
import com.example.reactmapping.global.norm.LOL;
import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.domain.member.service.LoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService authService;
    @Operation(summary = "로그인")
    @PostMapping("/normal")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto, HttpServletResponse httpServletResponse
            , @PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable) throws JsonProcessingException {
        LoginResponseDto responseDto = authService.login(httpServletResponse, dto.getEmailId(), dto.getPassword()
                , pageable, dto.getType());
        log.info("로그인 완료");
        return ResponseEntity.ok().body(responseDto);
    }
    @PostMapping("/oauthLogin")
    public ResponseEntity<?> oauthLogin(@CookieValue(name = Token.TokenName.accessToken, defaultValue = "NO") String accessToken, HttpServletResponse httpServletResponse
            , @PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable) throws JsonProcessingException {
        LoginResponseDto responseDto = authService.socialLogin(accessToken, pageable, LOL.GameType.솔랭.name());
        log.info("로그인 완료");
        return ResponseEntity.ok().body(responseDto);
    }
}
