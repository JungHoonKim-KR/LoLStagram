package com.example.reactmapping.domain.lol.match.entity;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "matches")
public class Match{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    @Column(unique = true, nullable = false)
    private String matchId;
    private Long gameStartTimestamp;
    private Long kills;
    private Long deaths;
    private Long assists;
    private String kda;
    private String championName;
    private String mainRune;
    private String subRune;
    private String gameType;
    @Convert(converter = StringListConverter.class)
    private List<String> itemList;
    @Convert(converter = StringListConverter.class)
    private List<String> summonerSpellList;
    private String result;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summoner_id")
    @JsonBackReference
    private SummonerInfo summonerInfo;

    @Builder
    public Match(String matchId, Long gameStartTimestamp, Long kills, Long deaths, Long assists, String kda, String championName, String mainRune, String subRune, String gameType, List<String> itemList, List<String> summonerSpellList, String result, SummonerInfo summonerInfo) {
        this.matchId = matchId;
        this.gameStartTimestamp = gameStartTimestamp;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.kda = kda;
        this.championName = championName;
        this.mainRune = mainRune;
        this.subRune = subRune;
        this.gameType = gameType;
        this.itemList = itemList;
        this.summonerSpellList = summonerSpellList;
        this.result = result;
        this.summonerInfo = summonerInfo;
    }

    public void updateSummonerInfo(SummonerInfo summonerInfo) {
        this.summonerInfo = summonerInfo;
    }

    public void updateMatch(Match newData) {
        if (newData == null) {
            return;  // 새 데이터가 null이면 업데이트를 수행하지 않습니다.
        }
        this.matchId = newData.matchId;
        this.gameStartTimestamp = newData.gameStartTimestamp;
        this.kills = newData.kills;
        this.deaths = newData.deaths;
        this.assists = newData.assists;
        this.kda = newData.kda;
        this.championName = newData.championName;
        this.mainRune = newData.mainRune;
        this.subRune = newData.subRune;
        this.gameType = newData.gameType;
        this.itemList = newData.itemList != null ? new ArrayList<>(newData.itemList) : null;
        this.summonerSpellList = newData.summonerSpellList != null ? new ArrayList<>(newData.summonerSpellList) : null;
        this.result = newData.result;
    }


}
