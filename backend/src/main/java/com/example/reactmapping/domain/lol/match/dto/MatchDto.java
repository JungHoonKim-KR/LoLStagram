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

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
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
    private List<Integer> itemList;
    @Convert(converter = StringListConverter.class)
    private List<Integer> summonerSpellList;
    private String result;


    // 엔티티 리스트를 DTO 리스트로 변환
    public static List<MatchDto> entityToDto(List<Match> matchList) {
        return matchList.stream()
                .map(match -> MatchDto.builder()
                        .matchId(match.getMatchId())
                        .kills(match.getKills())
                        .deaths(match.getDeaths())
                        .assists(match.getAssists())
                        .kda(match.getKda())
                        .championName(match.getChampionName())
                        .mainRune(match.getMainRune())
                        .subRune(match.getSubRune())
                        .gameType(match.getGameType())
                        .itemList(match.getItemList())  // 바로 설정
                        .summonerSpellList(match.getSummonerSpellList()) // 바로 설정
                        .result(match.getResult())
                        .build()
                ).collect(Collectors.toList());
    }

    // DTO를 엔티티로 변환
    public static Match dtoToEntity(MatchDto matchDto) {
        return Match.builder()
                .matchId(matchDto.getMatchId())
                .kills(matchDto.getKills())
                .deaths(matchDto.getDeaths())
                .assists(matchDto.getAssists())
                .kda(matchDto.getKda())
                .championName(matchDto.getChampionName())
                .mainRune(matchDto.getMainRune())
                .subRune(matchDto.getSubRune())
                .gameType(matchDto.getGameType())
                .itemList(matchDto.getItemList())
                .summonerSpellList(matchDto.getSummonerSpellList())
                .result(matchDto.getResult())
                .build();
    }
}
