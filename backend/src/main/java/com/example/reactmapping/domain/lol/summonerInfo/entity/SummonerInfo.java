package com.example.reactmapping.domain.lol.summonerInfo.entity;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.entity.Match;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class SummonerInfo {
    @Id
    @Column(name = "summoner_id", nullable = false, unique = true)
    private String summonerId;
    private String summonerName;
    private String summonerTag;
    private String puuId;
    @Embedded
    private BasicInfo basicInfo;
    @Embedded
    private RecentRecord recentRecord;

    @OneToMany(mappedBy = "summonerInfo", cascade = CascadeType.ALL)
    @JsonManagedReference
    @OrderBy("gameStartTimestamp DESC")
    private List<Match> matchList = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(length = 1000)
    private List<MostChampion> mostChampionList = new ArrayList<>();
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    public SummonerInfo(String puuId) {
        this.puuId = puuId;
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }



    // 연관관계의 주인은 Match 이지만 각 Match의 정보를 종합한 후 summoner에 저장하기 때문에 summoner에서 매핑하는 것이 순환참조를 막을 수 있음
    public void addMatch(Match match) {
        matchList.add(match);
        match.updateSummonerInfo(this);
    }

    @Builder
    public SummonerInfo(String summonerId, String summonerName, String summonerTag, String puuId, BasicInfo basicInfo, RecentRecord recentRecord, List<Match> matchList, List<MostChampion> mostChampionList) {
        this.summonerId = summonerId;
        this.summonerName = summonerName;
        this.summonerTag = summonerTag;
        this.puuId = puuId;
        this.basicInfo = basicInfo;
        this.recentRecord = recentRecord;
        this.matchList = matchList;
        this.mostChampionList = mostChampionList;
    }

    public BasicInfo updateBasicInfo(BasicInfo basicInfo){
        this.basicInfo = basicInfo;
        return this.basicInfo;
    }
    public void updateMatchList(List<Match> updatedMatchList) {
        this.matchList = updatedMatchList;
    }
    public void updateMostChampion(List<MostChampion> mostChampionList) {
        this.mostChampionList = mostChampionList;
    }
    public void updateRecentRecord(RecentRecord recentRecord){
        this.recentRecord = recentRecord;
    }



}
