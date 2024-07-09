package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.domain.lol.util.LoLApiUtil;
import com.example.reactmapping.domain.lol.matchInfo.domain.MatchInfo;
import com.example.reactmapping.domain.lol.matchInfo.service.GetMatchInfo;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.CreateSummonerInfoDto;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateSummonerInfo {
    private final DataUtil dataUtil;
    private final GetMatchInfo matchService;
    private final LoLApiUtil loLApiUtil;

    public CreateSummonerInfoDto createSummonerInfo(String riotIdGameName, String riotIdTagline, SummonerInfo summonerInfo, int startGame, int count) throws JsonProcessingException {
        List<MatchInfo> matchList = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<String> matches = matchService.getMatches(summonerInfo.getPuuId(), startGame, count);
        DecimalFormat df = dataUtil.getDecimalFormat();

        long totalkill = 0, totaldeath = 0, totalassist = 0;
        for (String matchId : matches) {
            String origin = loLApiUtil.getApiResponse(LOL.BaseUrlAsia, "/lol/match/v5/matches/" + matchId, ErrorCode.NOTFOUND, "경기를 찾을 수 없습니다.");
            JsonNode data = mapper.readTree(origin);
            JsonNode info = data.path("info");
            long gameStartTimestamp = info.path("gameStartTimestamp").asLong();

            // CLASSIC, URF, ARAM
            String gameMode = String.valueOf(info.path("gameMode"));
            String gameType = null;
            gameType = getGameType(gameMode, info, gameType);
            JsonNode path = info.path("participants");
            for (JsonNode p : path) {
                //원하는 소환사의 정보를 찾았을 때
                if (p.path(LOL.RiotIdGameName).asText().equals(riotIdGameName)
                        && p.path(LOL.RiotIdTagline).asText().equals(riotIdTagline)) {
                    //룬 정보 얻기
                    JsonNode perkPath = p.path("perks").path("styles");
                    Long mainRune = StreamSupport.stream(perkPath.spliterator(), false)
                            .filter(style -> "primaryStyle".equals(style.path("description").asText()))
                            .findFirst()
                            .map(style -> style.path("selections").get(0).path("perk").asLong())
                            .orElse(-1L);

                    Long subRune = StreamSupport.stream(perkPath.spliterator(), false)
                            .filter(style -> "subStyle".equals(style.path("description").asText()))
                            .findFirst()
                            .map(style -> style.path("style").asLong())
                            .orElse(-1L);//

                    List<Integer> itemList = IntStream.range(0, 7)
                            .mapToObj(i -> "item" + i)
                            .filter(p::has)
                            .map(key -> p.path(key).asInt())
                            .collect(Collectors.toList());
                    List<Integer> summonerSpellList = IntStream.range(1, 3)
                            .mapToObj(i -> "summoner" + i + "Id")
                            .filter(p::has)
                            .map(key -> p.path(key).asInt())
                            .collect(Collectors.toList());
                    long kills = p.path("kills").asLong();
                    long deaths = p.path("deaths").asLong();
                    long assists = p.path("assists").asLong();
                    MatchInfo build = MatchInfo.builder()
                            .matchId(matchId)
                            .gameStartTimestamp(gameStartTimestamp)
                            .kills(kills)
                            .deaths(deaths)
                            .assists(assists)
                            .championName(p.path("championName").asText())
                            .mainRune(mainRune)
                            .subRune(subRune)
                            .gameType(gameType)
                            .result(p.path("win").asText())
                            .summonerInfo(summonerInfo)
                            .build();

                    String kda = (build.getDeaths() == 0) ? "perfect" : df.format((double) (build.getKills() + build.getAssists()) / build.getDeaths());
                    build = build.toBuilder()
                            .kda(kda)
                            .itemList(itemList).summonerSpellList(summonerSpellList).build();
                    matchList.add(build);

                    totalkill += kills;
                    totalassist += assists;
                    totaldeath += deaths;
                }
            }
        }
        Long win = calWin(matchList);
        double totalKda = Double.parseDouble(df.format(((double) (totalkill + totalassist)) / ((double) totaldeath)));
        System.out.println(summonerInfo.getSummonerTag());

        summonerInfo = summonerInfo.toBuilder().totalKda(totalKda).recentWins(win).recentLosses(LOL.gameCount - win).build();
        System.out.println(summonerInfo.getSummonerTag());
        return new CreateSummonerInfoDto(summonerInfo, matchList);
    }

    private String getGameType(String gameMode, JsonNode info, String gameType) {
        if (gameMode.equals("\"CLASSIC\"")) {
            // 솔랭: 420, 빠대: 490, 칼바람: 450
            String queueId = String.valueOf(info.path("queueId"));
            if (queueId.equals("420"))
                gameType = LOL.GameType.솔랭.name();

            else if (queueId.equals("490"))
                gameType = LOL.GameType.빠른대전.name();
            else gameType = LOL.GameType.자유랭크.name();
        } else {
            if (gameMode.equals("\"URF\""))
                gameType = LOL.GameType.URF.name();
            else if (gameMode.equals("\"ARAM\""))
                gameType = LOL.GameType.무작위총력전.name();
            else if (gameMode.equals("\"CHERRY\""))
                gameType = LOL.GameType.아레나.name();
        }
        return gameType;
    }
    private Long calWin(List<MatchInfo> matchInfoList) {
        Long win = 0L;
        for (MatchInfo matchInfo : matchInfoList) {
            if (matchInfo.getResult().equals("true")) {
                win++;
            }
        }
        return win;
    }
}
