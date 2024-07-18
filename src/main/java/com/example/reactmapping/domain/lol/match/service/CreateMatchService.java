package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateMatchService {
    private final GetMatchService getMatchService;

    public Match createMatch(String matchId, String summonerName, String summonerTag) {
        JsonNode matchInfo = getMatchService.getMatch(matchId);
        Match.MatchBuilder matchBuilder = Match.builder()
                .matchId(matchId)
                .gameStartTimestamp(matchInfo.path("gameStartTimestamp").asLong())
                .gameType(determineGameType(matchInfo));
        JsonNode participants = matchInfo.path("participants");
        for (JsonNode participant : participants) {
            if (isDesiredSummoner(participant, summonerName, summonerTag)) {
                populateMatchDetails(matchBuilder, participant);
                break;
            }
        }
        return matchBuilder.build();
    }

    private String determineGameType(JsonNode matchInfo) {
        String gameMode = matchInfo.path("gameMode").asText().replace("\"", "");
        switch (gameMode) {
            case "CLASSIC":
                return switch (matchInfo.path("queueId").asText()) {
                    case "420" -> LOL.GameType.솔랭.name();
                    case "490" -> LOL.GameType.빠른대전.name();
                    default -> LOL.GameType.자유랭크.name();
                };
            case "URF":
                return LOL.GameType.URF.name();
            case "ARAM":
                return LOL.GameType.무작위총력전.name();
            case "CHERRY":
                return LOL.GameType.아레나.name();
            default:
                return "Unknown";
        }
    }
    private boolean isDesiredSummoner(JsonNode participant, String summonerName, String summonerTag) {
        return participant.path(LOL.RiotIdGameName).asText().equals(summonerName)
                && participant.path(LOL.RiotIdTagline).asText().equals(summonerTag);
    }
    private void populateMatchDetails(Match.MatchBuilder matchBuilder, JsonNode participant) {
        matchBuilder.kills(participant.path("kills").asLong())
                .deaths(participant.path("deaths").asLong())
                .assists(participant.path("assists").asLong())
                .kda(calculateKDA(participant))
                .championName(participant.path("championName").asText())
                .mainRune(extractMainRune(participant))
                .subRune(extractSubRune(participant))
                .result(participant.path("win").asText())
                .itemList(extractItems(participant))
                .summonerSpellList(extractSummonerSpells(participant));
    }
    private String calculateKDA(JsonNode participant) {
        long kills = participant.path("kills").asLong();
        long deaths = participant.path("deaths").asLong();
        long assists = participant.path("assists").asLong();
        DecimalFormat df = DataUtil.getDecimalFormat();
        return (deaths == 0) ? "perfect" : df.format((double) (kills + assists) / deaths);
    }

    private Long extractMainRune(JsonNode participant) {
        return participant.path("perks").path("styles").findValuesAsText("perk").stream()
                .map(Long::valueOf)
                .findFirst()
                .orElse(-1L);
    }

    private Long extractSubRune(JsonNode participant) {
        return participant.path("perks").path("styles").findValuesAsText("style").stream()
                .map(Long::valueOf)
                .findFirst()
                .orElse(-1L);
    }
    private List<Integer> extractItems(JsonNode participant) {
        return IntStream.range(0, 7)
                .mapToObj(i -> "item" + i)
                .filter(participant::has)
                .map(key -> participant.path(key).asInt())
                .collect(Collectors.toList());
    }
    private List<Integer> extractSummonerSpells(JsonNode participant) {
        return IntStream.range(1, 3)
                .mapToObj(i -> "summoner" + i + "Id")
                .filter(participant::has)
                .map(key -> participant.path(key).asInt())
                .collect(Collectors.toList());
    }
}
