package com.example.reactmapping.controller;
import com.example.reactmapping.config.JasyptConfig;
import com.example.reactmapping.config.JasyptUtil;
import com.example.reactmapping.dto.*;
import com.example.reactmapping.entity.MatchInfo;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.repository.MatchRepository;
import com.example.reactmapping.service.AuthService;
import com.example.reactmapping.service.LoLService;
import com.example.reactmapping.service.LogoutService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final LogoutService logoutService;
    private final JasyptUtil jasyptUtil;
    @Operation(summary = "회원가입", description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestPart("joinDto") @Parameter(name = "변수", description = "회원 이메일, 비밀번호, 이름") JoinDTO dto
                                    , @RequestPart("image")MultipartFile image) throws IOException {
        dto.setImg(image);
        Member member = authService.join(dto);
        return ResponseEntity.ok().body(member);
    }

    @Operation(summary = "로그인")
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(HttpSession httpSession,@RequestBody LoginRequestDto dto, HttpServletResponse httpServletResponse
                                ,@PageableDefault(size = 10,direction = Sort.Direction.DESC) Pageable pageable) throws JsonProcessingException {
        LoginResponseDto responseDto = authService.login(httpSession,httpServletResponse,dto.getEmailId(),dto.getPassword(),dto.getAuthenticationCode(),pageable,dto.getType());
        String encrypt = jasyptUtil.encrypt(dto.getEmailId());
        return ResponseEntity.ok().body(responseDto);
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/oauthLogin")
    public ResponseEntity<?> oauthLogin(HttpSession httpSession,@RequestBody LoginRequestDto dto, HttpSession session, HttpServletResponse httpServletResponse
            ,@PageableDefault(size = 10,direction = Sort.Direction.DESC) Pageable pageable) throws JsonProcessingException {
        AuthenticationDto authenticationDto = (AuthenticationDto) session.getAttribute("AuthenticationDto");
        if(authenticationDto.getCode().equals(dto.getAuthenticationCode())){
            LoginResponseDto responseDto = authService.login(httpSession,httpServletResponse,authenticationDto.getEmailId(),dto.getPassword(),dto.getAuthenticationCode(),pageable,dto.getType());
            return ResponseEntity.ok().body(responseDto);
        }
        else {
            log.info(authenticationDto.getCode());
            log.info(dto.getAuthenticationCode());
            AppException exception = new AppException(ErrorCode.ACCESS_ERROR,"인증 코드 불일치입니다.");
            return new ResponseEntity<>(exception, HttpStatus.UNAUTHORIZED);
        }
    }

    @ResponseBody
    @PostMapping("/logout")
    public void logout() {

    }


}
