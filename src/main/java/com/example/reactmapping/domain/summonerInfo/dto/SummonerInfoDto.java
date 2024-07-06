package com.example.reactmapping.domain.summonerInfo.dto;

import com.example.reactmapping.domain.matchInfo.dto.MatchInfoDto;
import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.summonerInfo.domain.SummonerInfo;
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
    private List<MatchInfoDto> matchList;

    // SummonerInfo 엔티티를 SummonerInfoDto로 변환하는 메서드
    public static SummonerInfoDto entityToDto(SummonerInfo summonerInfo) {
        //match를 페이징 처리 하려면 제거
        List<MatchInfoDto> matchInfoDtoList = MatchInfoDto.entityToDto(summonerInfo.getMatchList());

        return SummonerInfoDto.builder()
                .summonerId(summonerInfo.getSummonerId())
                .leagueId(summonerInfo.getLeagueId())
                .tier(summonerInfo.getTier())
                .tierRank(summonerInfo.getTierRank())
                .totalKda(summonerInfo.getTotalKda())
                .summonerName(summonerInfo.getSummonerName())
                .summonerTag(summonerInfo.getSummonerTag())
                .leaguePoints(summonerInfo.getLeaguePoints())
                .puuId(summonerInfo.getPuuId())
                .totalWins(summonerInfo.getTotalWins())
                .totalLosses(summonerInfo.getTotalLosses())
                .recentWins(summonerInfo.getRecentWins())
                .recentLosses(summonerInfo.getRecentLosses())
                .totalAvgOfWin(summonerInfo.getTotalAvgOfWin())
                .matchList(matchInfoDtoList) // MatchInfoDto 리스트를 설정
                .mostChampionList(summonerInfo.getMostChampionList())
                .build();
    }
    public static SummonerInfo dtoToEntity(SummonerInfoDto summonerInfoDto){
        return SummonerInfo.builder()
                .summonerId(summonerInfoDto.getSummonerId())
                .leagueId(summonerInfoDto.getLeagueId())
                .tier(summonerInfoDto.getTier())
                .tierRank(summonerInfoDto.getTierRank())
                .totalKda(summonerInfoDto.getTotalKda())
                .summonerName(summonerInfoDto.getSummonerName())
                .summonerTag(summonerInfoDto.getSummonerTag())
                .leaguePoints(summonerInfoDto.getLeaguePoints())
                .puuId(summonerInfoDto.getPuuId())
                .totalWins(summonerInfoDto.getTotalWins())
                .totalLosses(summonerInfoDto.getTotalLosses())
                .totalAvgOfWin(summonerInfoDto.getTotalAvgOfWin())
                .recentWins(summonerInfoDto.getRecentWins())
                .recentLosses(summonerInfoDto.getRecentLosses())
                .build();
    }
}
