package com.example.reactmapping.domain.member.dto;

import com.example.reactmapping.domain.summonerInfo.domain.SummonerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CallSummonerInfoResponse {
    private SummonerInfo summonerInfo;
    private String summonerId;
}
