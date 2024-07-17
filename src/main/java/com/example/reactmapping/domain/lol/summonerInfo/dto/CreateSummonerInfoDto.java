package com.example.reactmapping.domain.lol.summonerInfo.dto;

import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSummonerInfoDto {
    private SummonerInfo summonerInfo;
    private List<Match> Match;
}
