package com.example.reactmapping.dto;

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
    @Nullable
    private String authenticationCode;
}
