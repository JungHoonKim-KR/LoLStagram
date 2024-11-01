package com.example.reactmapping.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDto {
    private Long id;
    private String summonerName;
    private String summonerTag;

}
