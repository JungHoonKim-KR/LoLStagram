package com.example.reactmapping.domain.lol.summonerInfo.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BasicInfo {
    @Nullable
    private String leagueId;
    @Nullable
    private String tier;
    @Nullable
    private Long tierRank;
    @Nullable
    private Long leaguePoints;
    @Nullable
    private Long totalWins;
    @Nullable
    private Long totalLosses;
    @Nullable
    private Double totalAvgOfWin;
}

