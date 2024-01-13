package com.example.reactmapping.controller;

import com.example.reactmapping.dto.AuthenticationDto;
import com.example.reactmapping.dto.JoinDTO;
import com.example.reactmapping.dto.LoginRequestDto;
import com.example.reactmapping.dto.LoginResponseDto;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @Operation(summary = "회원가입", description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Parameter(name = "변수", description = "회원 이메일, 비밀번호, 이름") JoinDTO dto) {
        Member member = authService.join(dto);
        return ResponseEntity.ok().body(member);

    }

    @Operation(summary = "로그인")
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto, HttpServletResponse httpServletResponse) {
        LoginResponseDto responseDto = authService.login(httpServletResponse,dto.getEmailId(),dto.getPassword(),dto.getAuthenticationCode());
        return ResponseEntity.ok().body(responseDto);
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/oauthLogin")
    public ResponseEntity<?> oauthLogin(@RequestBody LoginRequestDto dto, HttpSession session, HttpServletResponse httpServletResponse){
        AuthenticationDto authenticationDto = (AuthenticationDto) session.getAttribute("AuthenticationDto");
        if(authenticationDto.getCode().equals(dto.getAuthenticationCode())){
            LoginResponseDto responseDto = authService.login(httpServletResponse,authenticationDto.getEmailId(),dto.getPassword(),dto.getAuthenticationCode());
            log.info(String.valueOf(responseDto));
            return ResponseEntity.ok().body(responseDto);
        }
        else {
            AppException exception = new AppException(ErrorCode.ACCESS_ERROR,"인증 코드 불일치입니다.");
            return new ResponseEntity<>(exception, HttpStatus.UNAUTHORIZED);
        }
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
