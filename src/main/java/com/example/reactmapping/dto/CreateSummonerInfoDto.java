package com.example.reactmapping.dto;

import com.example.reactmapping.entity.MatchInfo;
import com.example.reactmapping.entity.SummonerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSummonerInfoDto {
    private SummonerInfo summonerInfo;
    private List<MatchInfo> matchInfo;
}
