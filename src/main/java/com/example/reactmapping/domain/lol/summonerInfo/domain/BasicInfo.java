package com.example.reactmapping.domain.lol.summonerInfo.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BasicInfo {
    private String leagueId;
    private String tier;
    private Long tierRank;
    private Long leaguePoints;
    private Long totalWins;
    private Long totalLosses;
    private double totalAvgOfWin;
}
