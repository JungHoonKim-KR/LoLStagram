package com.example.reactmapping.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberDto {
    private Long id;
    private String username;
    @Nullable
    private String profileImg;
}
