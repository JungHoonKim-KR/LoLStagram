package com.example.reactmapping.domain.lol.summonerInfo.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecentRecord {
    private Long recentWins;
    private Long recentLosses;
    private double totalKda;
}
