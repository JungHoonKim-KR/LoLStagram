package com.example.reactmapping.domain.lol.matchInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchInfoRequestDto {
    public String summonerId;
    public String type;

}
