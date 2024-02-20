package com.example.reactmapping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDto {
    private Long id;
    private String summonerName;
    private String summonerTag;

}
