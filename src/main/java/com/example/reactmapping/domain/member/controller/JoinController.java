package com.example.reactmapping.domain.member.controller;

import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.domain.member.dto.JoinDTO;
import com.example.reactmapping.domain.member.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@RequiredArgsConstructor
public class JoinController {
    private final JoinService joinService;
    @Operation(summary = "회원가입", description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestPart("joinDto") @Parameter(name = "변수", description = "회원 이메일, 비밀번호, 이름") JoinDTO dto
            , @RequestPart(name ="image", required = false) MultipartFile image) throws IOException {
        dto.setImage(image);
        Member member = joinService.join(dto);
        return ResponseEntity.ok().body(member);
    }
}
