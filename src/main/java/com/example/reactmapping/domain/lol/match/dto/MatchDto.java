package com.example.reactmapping.domain.lol.match.dto;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.lol.match.entity.Match;
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

public class MatchDto {
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

    public static List<MatchDto> entityToDto(List<Match> MatchList){
        return MatchList.stream()
                .map(Match -> {
                            MatchDto build = MatchDto.builder()
                                    .matchId(Match.getMatchId())
                                    .kills(Match.getKills())
                                    .deaths(Match.getDeaths())
                                    .assists(Match.getAssists())
                                    .kda(Match.getKda())
                                    .championName(Match.getChampionName())
                                    .mainRune(Match.getMainRune())
                                    .subRune(Match.getSubRune())
                                    .gameType(Match.getGameType())
                                    .result(Match.getResult())
                                    .build();
                            return build.toBuilder().itemList(Match.getItemList()).summonerSpellList(Match.getSummonerSpellList()).build();
                        }
                ).collect(Collectors.toList());
    }

    public static Match dtoToEntity(MatchDto MatchDto){
        return Match.builder()
                .matchId(MatchDto.getMatchId())
                .kills(MatchDto.getKills())
                .deaths(MatchDto.getDeaths())
                .assists(MatchDto.getAssists())
                .kda(MatchDto.getKda())
                .championName(MatchDto.getChampionName())
                .mainRune(MatchDto.getMainRune())
                .subRune(MatchDto.getSubRune())
                .itemList(MatchDto.getItemList())
                .summonerSpellList(MatchDto.getSummonerSpellList())
                .result(MatchDto.getResult())
                .build();
    }
}
