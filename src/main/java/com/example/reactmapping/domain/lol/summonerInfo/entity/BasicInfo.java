package com.example.reactmapping.domain.lol.summonerInfo.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@Builder
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

    // Constructor without @AllArgsConstructor
    public BasicInfo(@Nullable String leagueId, @Nullable String tier, @Nullable Long tierRank, @Nullable Long leaguePoints
            , @Nullable Long totalWins, @Nullable Long totalLosses, @Nullable Double totalAvgOfWin) {
        this.leagueId = leagueId;
        this.tier = tier;
        this.tierRank = tierRank;
        this.leaguePoints = leaguePoints;
        this.totalWins = totalWins;
        this.totalLosses = totalLosses;
        this.totalAvgOfWin = totalAvgOfWin;
    }
}
