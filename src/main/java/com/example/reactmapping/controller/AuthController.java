package com.example.reactmapping.controller;
import com.example.reactmapping.dto.*;
import com.example.reactmapping.entity.Member;
import com.example.reactmapping.norm.LOL;
import com.example.reactmapping.norm.Token;
import com.example.reactmapping.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestPart("joinDto") @Parameter(name = "변수", description = "회원 이메일, 비밀번호, 이름") JoinDTO dto
            , @RequestPart("image") MultipartFile image) throws IOException {
        dto.setImg(image);
        Member member = authService.join(dto);
        return ResponseEntity.ok().body(member);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
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
        LoginResponseDto responseDto = authService.socialLogin(httpServletResponse, accessToken, pageable, LOL.GameType.솔랭.name());
        log.info("로그인 완료");
        return ResponseEntity.ok().body(responseDto);
    }

    @ResponseBody
    @PostMapping("/logout")
    public void logout() {

    }


}
