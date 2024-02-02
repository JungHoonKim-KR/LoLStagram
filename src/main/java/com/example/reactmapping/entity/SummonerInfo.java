package com.example.reactmapping.entity;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.object.MostChampion;
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
    private String summonerName;
    private String summonerTag;
    private Long leaguePoints;
    private String puuId;
    private Long totalWins;
    private Long totalLosses;
    private Long recentWins;
    private Long recentLosses;
    @OneToMany(mappedBy = "SummonerInfo")
    @JsonManagedReference
    @OrderBy("gameStartTimestamp DESC")
    private List<MatchInfo> matchList = new ArrayList<>();
    @Convert(converter = StringListConverter.class)
    private List<MostChampion> mostChampionList= new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    public void addMatchList(MatchInfo matchInfo){
        matchList.add(matchInfo);
        matchInfo.toBuilder().SummonerInfo(this);
    }

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


}
