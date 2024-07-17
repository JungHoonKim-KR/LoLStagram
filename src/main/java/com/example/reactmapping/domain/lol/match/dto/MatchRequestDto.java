package com.example.reactmapping.domain.lol.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequestDto {
    public String summonerId;
    public String type;

}
