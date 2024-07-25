package com.example.reactmapping.domain.lol.summonerInfo.domain;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.domain.Match;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
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
    private String summonerName;
    private String summonerTag;
    private String puuId;
    @Embedded
    private BasicInfo basicInfo;
    @Embedded
    private RecentRecord recentRecord;
    @OneToMany(mappedBy = "summonerInfo", fetch = FetchType.LAZY)
    @JsonManagedReference
    @OrderBy("gameStartTimestamp DESC")
    private List<Match> matchList = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
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

    @Override
    public boolean isNew() {
        return createTime == null;
    }


    // 연관관계의 주인은 Match 이지만 각 Match의 정보를 종합한 후 summoner에 저장하기 때문에 summoner에서 매핑하는 것이 순환참조를 막을 수 있음
    public void addMatch(Match match) {
        matchList.add(match);
        match.updateSummonerInfo(this);
    }
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

    public void updateBasicInfo(BasicInfo basicInfo){
        this.basicInfo = basicInfo;
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
    // ID 필드에 대한 getter
    @Override
    public String getId() {
        return summonerId;
    }


}
