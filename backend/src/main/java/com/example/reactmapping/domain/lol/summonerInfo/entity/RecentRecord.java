package com.example.reactmapping.domain.lol.summonerInfo.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
@Builder
public class RecentRecord {
    private Long recentWins;
    private Long recentLosses;
    private String totalKda;

    public RecentRecord(Long recentWins, Long recentLosses, String totalKda) {
        this.recentWins = recentWins;
        this.recentLosses = recentLosses;
        this.totalKda = totalKda;
    }
}
