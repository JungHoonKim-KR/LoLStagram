package com.example.reactmapping.domain.lol.match.dto;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.Image.service.ImageService;
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
    private String championURL;
    private String mainRuneURL;
    private String subRuneURL;
    private String gameType;
    @Convert(converter = StringListConverter.class)
    private List<String> itemURLList;
    @Convert(converter = StringListConverter.class)
    private List<String> summonerSpellURLList;
    private String result;

    // 엔티티 리스트를 DTO 리스트로 변환
    public static List<MatchDto> entityToDto(List<Match> matchList, ImageService imageService) {
        return matchList.stream()
                .map(match -> {

                    return MatchDto.builder()
                            .matchId(match.getMatchId())
                            .kills(match.getKills())
                            .deaths(match.getDeaths())
                            .assists(match.getAssists())
                            .kda(match.getKda())
                            .championURL(imageService.getImageURL("champion", match.getChampionName()))
                            .mainRuneURL(imageService.getImageURL("rune", match.getMainRune()))
                            .subRuneURL(imageService.getImageURL("rune", match.getSubRune()))
                            .gameType(match.getGameType())
                            .itemURLList(match.getItemList().stream().map(item -> imageService.getImageURL("item", item)).toList())  // 바로 설정
                            .summonerSpellURLList(match.getSummonerSpellList().stream().map(spell -> imageService.getImageURL("spell", spell)).toList()) // 바로 설정
                            .result(match.getResult())
                            .build();
                }).collect(Collectors.toList());
    }


}
