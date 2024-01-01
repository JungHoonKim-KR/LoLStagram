package com.example.reactmapping.controller;

import com.example.reactmapping.config.jwt.TokenDto;
import com.example.reactmapping.dto.JoinDTO;
import com.example.reactmapping.dto.LoginRequest;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.service.AuthService;

import com.example.reactmapping.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OAuth2Service oAuth2Service;


    @Operation(summary = "회원가입", description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<Member> join(@RequestBody @Parameter(name = "변수", description = "회원 이메일, 비밀번호, 이름") JoinDTO dto) {
        Member member = authService.join(dto);
        return ResponseEntity.ok().body(member);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginRequest dto, HttpServletResponse httpServletResponse) {
        TokenDto tokenDto = authService.login(httpServletResponse, dto);
        return ResponseEntity.ok().body(tokenDto);
    }
    @ResponseBody
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        return "로그아웃";
    }

    @GetMapping("/oauth2/login/google")
    public String test() {

        return "완료";
    }

}
