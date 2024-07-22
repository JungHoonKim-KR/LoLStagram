package com.example.reactmapping.domain.member.controller;

import com.example.reactmapping.domain.member.dto.ProfileUpdateDto;
import com.example.reactmapping.domain.member.service.LoginService;
import com.example.reactmapping.domain.member.service.UpdateMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UpdateController {
    private final UpdateMemberService updateMemberService;
    @PutMapping("/profile/update")
    public void profileUpdate(@RequestBody ProfileUpdateDto profileUpdateDto) throws IOException {
        updateMemberService.updateProfile(profileUpdateDto);
    }
}
