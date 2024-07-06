package com.example.reactmapping.domain.summonerInfo.dto;

import com.example.reactmapping.domain.matchInfo.domain.MatchInfo;
import com.example.reactmapping.domain.summonerInfo.domain.SummonerInfo;
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
