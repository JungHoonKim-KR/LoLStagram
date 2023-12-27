package com.example.reactmapping.controller;

import com.example.reactmapping.config.jwt.TokenDto;
import com.example.reactmapping.dto.LoginRequest;
import com.example.reactmapping.dto.MemberJoinRequest;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.service.AuthService;

import com.example.reactmapping.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class AuthController {
    private final AuthService authService;
    private final OAuth2Service oAuth2Service;


    @Operation(summary = "회원가입",description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<Member> join(@RequestBody @Parameter(name = "변수",description = "회원 이메일, 비밀번호, 이름") MemberJoinRequest dto){
        Member member = authService.join(dto);
        return ResponseEntity.ok().body(member);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login( LoginRequest dto, HttpServletResponse httpServletResponse){
        TokenDto tokenDto = authService.login(httpServletResponse, dto);
        return ResponseEntity.ok().body(tokenDto);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        return "로그아웃";
    }

    @GetMapping("/login/oauth2/code/google")
    public String test(){

        return "완료";
    }

}
