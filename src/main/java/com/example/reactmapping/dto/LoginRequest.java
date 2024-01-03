package com.example.reactmapping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequest {
    private String emailId;
    private String password;
}
