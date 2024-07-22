package com.example.reactmapping.domain.member.controller;

import com.example.reactmapping.domain.member.dto.ProfileUpdateDto;
import com.example.reactmapping.domain.member.service.LoginService;
import com.example.reactmapping.domain.member.service.UpdateMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UpdateController {
    private final UpdateMemberService updateMemberService;
    @PutMapping("/profile/update")
    public void profileUpdate(@RequestBody ProfileUpdateDto profileUpdateDto) throws IOException {
        updateMemberService.updateProfile(profileUpdateDto);
        log.info("프로필 업데이트 완료");
    }
}
