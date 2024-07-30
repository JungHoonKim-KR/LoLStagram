package com.example.reactmapping.domain.lol.summonerInfo.dto;

import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.dto.MatchDto;
import com.example.reactmapping.domain.lol.summonerInfo.domain.BasicInfo;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SummonerInfoDto {
    private String summonerId;
    @Nullable
    private String leagueId;
    @Nullable

    private String tier;
    @Nullable
    private Long tierRank;
    @Nullable
    private double totalKda;
    private String summonerName;
    private String summonerTag;
    @Nullable
    private Long leaguePoints;
    private String puuId;
    @Nullable
    private Long totalWins;
    @Nullable
    private Long totalLosses;
    @Nullable
    private double totalAvgOfWin;
    private Long recentWins;
    private Long recentLosses;
    private List<MostChampion> mostChampionList;
    private List<MatchDto> matchList;

    // SummonerInfo 엔티티를 SummonerInfoDto로 변환하는 메서드
    public static SummonerInfoDto entityToDto(SummonerInfo summonerInfo) {
        //match를 페이징 처리 하려면 제거
        List<MatchDto> MatchDtoList = MatchDto.entityToDto(summonerInfo.getMatchList());
        BasicInfo basicInfo = Optional.ofNullable(summonerInfo.getBasicInfo()).orElse(new BasicInfo());

        return SummonerInfoDto.builder()
                .summonerId(summonerInfo.getSummonerId())
                .summonerName(summonerInfo.getSummonerName())
                .summonerTag(summonerInfo.getSummonerTag())
                .puuId(summonerInfo.getPuuId())
                .leagueId(Optional.ofNullable(basicInfo.getLeagueId()).orElse("Unknown League")) // 기본값 "Unknown League"
                .tier(Optional.ofNullable(basicInfo.getTier()).orElse("Unranked")) // 기본값 "Unranked"
                .tierRank(Optional.ofNullable(basicInfo.getTierRank()).orElse(0L)) // 기본값 0
                .leaguePoints(Optional.ofNullable(basicInfo.getLeaguePoints()).orElse(0L)) // 기본값 0
                .totalWins(Optional.ofNullable(basicInfo.getTotalWins()).orElse(0L)) // 기본값 0
                .totalLosses(Optional.ofNullable(basicInfo.getTotalLosses()).orElse(0L)) // 기본값 0
                .totalAvgOfWin(Optional.ofNullable(basicInfo.getTotalAvgOfWin()).orElse(0.0)) // 기본값 0.0
                .recentWins(summonerInfo.getRecentRecord().getRecentWins())
                .recentLosses(summonerInfo.getRecentRecord().getRecentLosses())
                .totalKda(summonerInfo.getRecentRecord().getTotalKda())
                .matchList(MatchDtoList) // MatchDto 리스트를 설정
                .mostChampionList(summonerInfo.getMostChampionList())
                .build();
    }
}
