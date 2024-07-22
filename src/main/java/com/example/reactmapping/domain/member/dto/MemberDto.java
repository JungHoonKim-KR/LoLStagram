package com.example.reactmapping.domain.member.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberDto {
    private Long id;
    private String emailId;
    private String username;
    @Nullable
    private String profileImage;
}
