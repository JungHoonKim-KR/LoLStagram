package com.example.reactmapping.domain.lol.match.dto;

import com.example.reactmapping.StringListConverter;
import com.example.reactmapping.domain.Image.service.ImageService;
import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.global.norm.LOL;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<String> championKeys = matchList.stream().map(Match::getChampionName).distinct().toList();
        List<String> runeKeys = matchList.stream().flatMap(match -> Stream.of(match.getMainRune(), match.getSubRune())).distinct().toList();
        List<String> itemKeys = matchList.stream().flatMap(match -> match.getItemList().stream()).distinct().toList();
        List<String> spellKeys = matchList.stream().flatMap(match -> match.getSummonerSpellList().stream()).distinct().toList();

        Map<String, String> championURLMap = imageService.findUrlsByTypeAndKeys(LOL.ResourceType.CHAMPION.getType(), championKeys);
        Map<String, String> runeURLMap = imageService.findUrlsByTypeAndKeys(LOL.ResourceType.RUNE.getType(), runeKeys);
        Map<String, String> itemURLMap = imageService.findUrlsByTypeAndKeys(LOL.ResourceType.ITEM.getType(), itemKeys);
        Map<String, String> spellURLMap = imageService.findUrlsByTypeAndKeys(LOL.ResourceType.SPELL.getType(), spellKeys);

        return matchList.stream()
                .map(match -> {
                    return MatchDto.builder()
                            .matchId(match.getMatchId())
                            .kills(match.getKills())
                            .deaths(match.getDeaths())
                            .assists(match.getAssists())
                            .kda(match.getKda())
                            .championURL(championURLMap.get(match.getChampionName()))
                            .mainRuneURL(runeURLMap.get(match.getMainRune()))
                            .subRuneURL(runeURLMap.get(match.getSubRune()))
                            .gameType(match.getGameType())
                            .itemURLList(match.getItemList().stream().map(itemURLMap::get).toList())  // 바로 설정
                            .summonerSpellURLList(match.getSummonerSpellList().stream().map(spellURLMap::get).toList()) // 바로 설정
                            .result(match.getResult())
                            .build();
                }).collect(Collectors.toList());
    }


}
