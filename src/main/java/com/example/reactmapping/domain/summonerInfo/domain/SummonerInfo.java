package com.example.reactmapping.domain.summonerInfo.domain;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.matchInfo.domain.MatchInfo;
import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(toBuilder = true)
public class SummonerInfo implements Persistable<String> {
    @Id
    @Column(name = "summoner_id")
    private String summonerId;
    private String leagueId;
    private String tier;
    private Long tierRank;
    private double totalKda;
    private String summonerName;
    private String summonerTag;
    private Long leaguePoints;
    private String puuId;
    private Long totalWins;
    private Long totalLosses;
    private double totalAvgOfWin;
    private Long recentWins;
    private Long recentLosses;
    @OneToMany(mappedBy = "summonerInfo")
    @JsonManagedReference
    @OrderBy("gameStartTimestamp DESC")
    private List<MatchInfo> matchList = new ArrayList<>();
    @Convert(converter = StringListConverter.class)
    private List<MostChampion> mostChampionList = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

//    public void addMatchInfo(MatchInfo matchInfo) {
//        if (matchList == null) {
//            matchList = new ArrayList<>();
//        }
//        matchInfo= matchInfo.toBuilder().summonerInfo(this).build();
//        matchList.add(matchInfo);
//    }


    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
    @Override
    public boolean isNew() {
        return createTime == null;
    }

    // ID 필드에 대한 getter
    @Override
    public String getId() {
        return summonerId;
    }

    public void update(SummonerInfo summonerInfo) {
        this.summonerId = summonerInfo.summonerId;
        this.leagueId = summonerInfo.leagueId;
        this.tier = summonerInfo.tier;
        this.tierRank = summonerInfo.tierRank;
        this.totalKda = summonerInfo.totalKda;
        this.summonerName = summonerInfo.summonerName;
        this.summonerTag = summonerInfo.summonerTag;
        this.leaguePoints = summonerInfo.leaguePoints;
        this.puuId = summonerInfo.puuId;
        this.totalWins = summonerInfo.totalWins;
        this.totalLosses = summonerInfo.totalLosses;
        this.totalAvgOfWin = summonerInfo.totalAvgOfWin;
        this.recentWins = summonerInfo.recentWins;
        this.recentLosses = summonerInfo.recentLosses;
    }


}
