package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.domain.lol.match.riotAPI.GetMatchInfoWithAPI;
import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateMatchService {
    private final GetMatchInfoWithAPI getMatchInfoWithAPI;
    private final DataUtil dataUtil;
    private final RateLimiter limiter;

    public List<Match> createMatchParallel(List<String> matchIdList, String summonerName, String summonerTag) {
        ExecutorService executor = Executors.newFixedThreadPool(10); // 10~20도 OK

        List<CompletableFuture<Match>> matchList = matchIdList.stream()
                .map(matchId -> CompletableFuture.supplyAsync(() -> {
                    try {
                        log.info("{}", matchId);
                        log.info("Using RateLimiter: name={}, hashcode={}",
                                limiter.getName(),
                                System.identityHashCode(limiter));
                        log.info("RateLimiter config: limitForPeriod={}, refreshPeriod={}, timeout={}",
                                limiter.getRateLimiterConfig().getLimitForPeriod(),
                                limiter.getRateLimiterConfig().getLimitRefreshPeriod(),
                                limiter.getRateLimiterConfig().getTimeoutDuration());
//                         ✅ RateLimiter 안에서 실행
                        return RateLimiter.decorateSupplier(limiter, () ->
                                createMatch(matchId, summonerName, summonerTag)
                        ).get();
//                        limiter.wait();
//                        return createMatch(matchId, summonerName, summonerTag);
                    } catch (Exception e) {
                        log.warn("matchId: {} 처리 실패 - {}", matchId, e.getMessage());
                        throw new AppException(ErrorCode.BAD_REQUEST, "서버 에러");
                    }
                }, executor))
                .toList();
        // CompletableFuture 객체가 matchId의 순서를 가지고 있고 CompletableFuture::join을 통해 해당 순서의 데이터가 응답될 때 까지 기다림으로써 순서를 유지하는 구조
        // 다시 말해 supplyAsync(비동기)로 match 생성을 요청해 놓고 return문에서 데이터를 대기하고 있음
        return matchList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

//    public List<Match> createMatchParallel(List<String>matchIdList, String summonerName, String summonerTag){
//        try{
//
//        List<CompletableFuture<Match>> matchList = matchIdList.stream()
//                .map(matchId -> CompletableFuture.supplyAsync(() -> createMatch(matchId, summonerName, summonerTag)))
//                .toList();
//        return matchList.stream()
//                .map(CompletableFuture :: join)
//                .collect(Collectors.toList());
//        }
//        catch (Exception e){
//            throw new AppException(ErrorCode.BAD_REQUEST, "내부 에러");
//        }
//    }

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
        return participant.path("riotIdGameName").asText().equalsIgnoreCase(summonerName)
                && participant.path("riotIdTagline").asText().equals(summonerTag);
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
