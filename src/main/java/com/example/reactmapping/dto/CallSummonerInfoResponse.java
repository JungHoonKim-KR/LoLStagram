package com.example.reactmapping.dto;

import com.example.reactmapping.entity.SummonerInfo;
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
