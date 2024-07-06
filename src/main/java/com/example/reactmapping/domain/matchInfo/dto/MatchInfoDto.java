package com.example.reactmapping.domain.matchInfo.dto;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.matchInfo.domain.MatchInfo;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class MatchInfoDto {
    private String matchId;
    private Long kills;
    private Long deaths;
    private Long assists;
    private String kda;
    private String championName;
    private Long mainRune;
    private Long subRune;
    private String gameType;
    @Convert(converter = StringListConverter.class)
    private List<Integer> itemList ;
    @Convert(converter = StringListConverter.class)
    private List<Integer> summonerSpellList;
    private String result;

    public static List<MatchInfoDto> entityToDto(List<MatchInfo> matchInfoList){
        return matchInfoList.stream()
                .map(matchInfo -> {
                            MatchInfoDto build = MatchInfoDto.builder()
                                    .matchId(matchInfo.getMatchId())
                                    .kills(matchInfo.getKills())
                                    .deaths(matchInfo.getDeaths())
                                    .assists(matchInfo.getAssists())
                                    .kda(matchInfo.getKda())
                                    .championName(matchInfo.getChampionName())
                                    .mainRune(matchInfo.getMainRune())
                                    .subRune(matchInfo.getSubRune())
                                    .gameType(matchInfo.getGameType())
                                    .result(matchInfo.getResult())
                                    .build();
                            return build.toBuilder().itemList(matchInfo.getItemList()).summonerSpellList(matchInfo.getSummonerSpellList()).build();
                        }
                ).collect(Collectors.toList());
    }

    public static MatchInfo dtoToEntity(MatchInfoDto matchInfoDto){
        return MatchInfo.builder()
                .matchId(matchInfoDto.getMatchId())
                .kills(matchInfoDto.getKills())
                .deaths(matchInfoDto.getDeaths())
                .assists(matchInfoDto.getAssists())
                .kda(matchInfoDto.getKda())
                .championName(matchInfoDto.getChampionName())
                .mainRune(matchInfoDto.getMainRune())
                .subRune(matchInfoDto.getSubRune())
                .itemList(matchInfoDto.getItemList())
                .summonerSpellList(matchInfoDto.getSummonerSpellList())
                .result(matchInfoDto.getResult())
                .build();
    }
}
