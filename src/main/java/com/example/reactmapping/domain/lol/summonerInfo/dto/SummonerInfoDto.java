package com.example.reactmapping.domain.lol.summonerInfo.dto;

import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.dto.MatchDto;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SummonerInfoDto {
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
    private List<MostChampion> mostChampionList;
    private List<MatchDto> matchList;

    // SummonerInfo 엔티티를 SummonerInfoDto로 변환하는 메서드
    public static SummonerInfoDto entityToDto(SummonerInfo summonerInfo) {
        //match를 페이징 처리 하려면 제거
        List<MatchDto> MatchDtoList = MatchDto.entityToDto(summonerInfo.getMatchList());

        return SummonerInfoDto.builder()
                .summonerId(summonerInfo.getSummonerId())
                .summonerName(summonerInfo.getSummonerName())
                .summonerTag(summonerInfo.getSummonerTag())
                .puuId(summonerInfo.getPuuId())
                .leagueId(summonerInfo.getBasicInfo().getLeagueId())
                .tier(summonerInfo.getBasicInfo().getTier())
                .tierRank(summonerInfo.getBasicInfo().getTierRank())
                .leaguePoints(summonerInfo.getBasicInfo().getLeaguePoints())
                .totalWins(summonerInfo.getBasicInfo().getTotalWins())
                .totalLosses(summonerInfo.getBasicInfo().getTotalLosses())
                .totalAvgOfWin(summonerInfo.getBasicInfo().getTotalAvgOfWin())
                .recentWins(summonerInfo.getRecentRecord().getRecentWins())
                .recentLosses(summonerInfo.getRecentRecord().getRecentLosses())
                .totalKda(summonerInfo.getRecentRecord().getTotalKda())
                .matchList(MatchDtoList) // MatchDto 리스트를 설정
                .mostChampionList(summonerInfo.getMostChampionList())
                .build();
    }
}
