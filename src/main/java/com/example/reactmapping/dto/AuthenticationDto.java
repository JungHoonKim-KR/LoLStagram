package com.example.reactmapping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthenticationDto {
    private String emailId;
    private String code;
}
