package com.example.reactmapping.domain.member.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginRequestDto {
    private String emailId;
    @Nullable
    private String password;
    private String type;
    @Nullable
    private String authenticationCode;
}
