package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.domain.lol.match.riotAPI.GetMatchInfoWithAPI;
import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateMatchService {
    private final GetMatchInfoWithAPI getMatchInfoWithAPI;
    private final DataUtil dataUtil;

    public Match createMatch(String matchId, String summonerName, String summonerTag) {

        JsonNode matchInfo = getMatchInfoWithAPI.getMatch(matchId);
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
                    case "420" -> LOL.GameType.솔랭.getType();
                    case "490" -> LOL.GameType.빠른대전.getType();
                    default -> LOL.GameType.자유랭크.getType();
                };
            case "URF":
                return LOL.GameType.URF.getType();
            case "ARAM":
                return LOL.GameType.무작위총력전.getType();
            case "CHERRY":
                return LOL.GameType.아레나.getType();
            default:
                return "Unknown";
        }
    }
    private boolean isDesiredSummoner(JsonNode participant, String summonerName, String summonerTag) {
        return participant.path(LOL.RiotIdGameName).asText().equalsIgnoreCase(summonerName)
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
        return dataUtil.calculateKDA(kills,deaths,assists);
    }

    private String extractMainRune(JsonNode participant) {
        return participant.path("perks").path("styles").findValuesAsText("perk").stream()
                .map(String::valueOf)
                .findFirst()
                .orElse(null);
    }

    private String extractSubRune(JsonNode participant) {
        return participant.path("perks").path("styles").findValuesAsText("style").stream()
                .map(String::valueOf)
                .findFirst()
                .orElse(null);
    }
    private List<String> extractItems(JsonNode participant) {
        return IntStream.range(0, 7)
                .mapToObj(i -> "item" + i)
                .filter(participant::has)
                .map(key -> participant.path(key).asText())
                .collect(Collectors.toList());
    }
    private List<String> extractSummonerSpells(JsonNode participant) {
        return IntStream.range(1, 3)
                .mapToObj(i -> "summoner" + i + "Id")
                .filter(participant::has)
                .map(key -> participant.path(key).asText())
                .collect(Collectors.toList());
    }
}
