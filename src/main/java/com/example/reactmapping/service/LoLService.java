package com.example.reactmapping.service;

import com.example.reactmapping.dto.CompareDto;
import com.example.reactmapping.dto.CreateSummonerInfoDto;
import com.example.reactmapping.entity.MatchInfo;
import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import com.example.reactmapping.norm.LOL;
import com.example.reactmapping.object.MostChampion;
import com.example.reactmapping.entity.SummonerInfo;
import com.example.reactmapping.repository.MatchRepository;
import com.example.reactmapping.repository.SummonerInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
@Slf4j
@Service
@RequiredArgsConstructor
public class LoLService {
    private final String RiotTokenHeader = "X-Riot-Token";
    private final String ApiKey = "RGAPI-8ffbfd5a-0b81-41f7-8083-6d26ad910aea";
    private final String BaseUrlAsia = "https://asia.api.riotgames.com";
    private final String BaseUrlKR = "https://kr.api.riotgames.com";
    private String Url;
    private String RiotIdGameName="riotIdGameName";
    private String RiotIdTagline="riotIdTagline";
    private final MatchRepository matchRepository;


    // 소환사를 등록할 때 puuId를 가져와 DB에 저장
    public String callPuuId(String riotIdGameName, String riotIdTagline) {
        log.info("요청 닉네임: "+riotIdGameName+ " 요청 태그: "+riotIdTagline);
        Map block = createWebClient(BaseUrlAsia, "/riot/account/v1/accounts/by-riot-id/" + riotIdGameName + "/" + riotIdTagline)
                .bodyToMono(new ParameterizedTypeReference<Map>() {
                })
                .onErrorResume(e -> {
                    throw new AppException(ErrorCode.NOTFOUND, "라이엇 이름 또는 태그가 일치하지 않습니다.");
                })
                .block();
        return  block.get("puuid").toString();
    }

    public String callSummonerId(String puuId){
        Map block = createWebClient(BaseUrlKR, "/lol/summoner/v4/summoners/by-puuid/" + puuId)
                .bodyToMono(new ParameterizedTypeReference<Map>() {
                })
                .onErrorResume(e -> {
                    throw new AppException(ErrorCode.NOTFOUND, "소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.");
                })
                .block();

        return block.get("id").toString();

    }
    // 최근 대전기록 가져오기
    public List<String> callMatches(String puuId , int startGame, int count) {
        Url = String.format("/lol/match/v5/matches/by-puuid/%s/ids?start=%s&count=%s", puuId, startGame, count);
        return createWebClient(BaseUrlAsia, Url).bodyToMono(List.class).block();
    }
    // 최신 경기 리스트 10개 중 마지막 경기를 가져옴
    // 이 마지막 경기가 현재 저장된 리스트의 몇번 인덱스에 포함되는지
    // ex) 4번이면 0~3의 경기 즉 4개의 경기가 갱신이 안됨.
    public CompareDto compare(String puuId, String summonerId){
        int result =-1;
        String targetMatchId = callMatches(puuId, LOL.INFO.getGameCount()-1, 1).get(0);
        log.info(targetMatchId);
        List<MatchInfo> matchInfo = matchRepository.findAllBySummonerId(summonerId);
        if(!matchInfo.isEmpty()) {
            result = IntStream.range(0, matchInfo.size())
                    .filter(i -> targetMatchId.equals(matchInfo.get(i).getMatchId()))
                    .findFirst()
                    .orElse(0);
            // 업데이트는 개수로 처리하기 때문에 0이 아니라면 인덱스 +1을 해줘야함
            if(result !=0) result++;
        }
        log.info("겹치는 경기"+ result);
        return new CompareDto(result,matchInfo);
    }


    public CreateSummonerInfoDto createSummonerInfo(String riotIdGameName, String riotIdTagline, SummonerInfo summonerInfo, int startGame, int count) throws JsonProcessingException {

        List<MatchInfo> matchList = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper();
        List <String> matches = callMatches(summonerInfo.getPuuId(),startGame,count);
        DecimalFormat df = getDecimalFormat();

        long totalkill=0,totaldeath=0,totalassist=0;
        for (String matchId : matches) {
            String origin = createWebClient(BaseUrlAsia,"/lol/match/v5/matches/" + matchId)
                    .bodyToMono(String.class)
                    .block();
            JsonNode data = mapper.readTree(origin);
            JsonNode info = data.path("info");
            long gameStartTimestamp = info.path("gameStartTimestamp").asLong();

            // CLASSIC, URF, ARAM
            String gameMode = String.valueOf(info.path("gameMode"));
            String gameType = null;
            log.info(gameMode);
            if(gameMode.equals("\"CLASSIC\"")){
                // 솔랭: 420, 빠대: 490, 칼바람: 450
                String queueId = String.valueOf(info.path("queueId"));
                if(queueId.equals("420"))
                    gameType = "솔랭";
                else if(queueId.equals("490"))
                    gameType = "빠른 대전";
                else gameType = "자유 랭크";
            }
            else{
                if(gameMode.equals("\"URF\""))
                    gameType = "URF";
                else if(gameMode.equals("\"ARAM\""))
                    gameType = "무작위 총력전";
                else if(gameMode.equals("\"CHERRY\""))
                    gameType="아레나";
            }
            // 사설 아니면 다 MATCHED_GAME
//            log.info(String.valueOf(info.path("gameType")));
            log.info(gameType);
            JsonNode path = info.path("participants");
            for (JsonNode p : path) {
                //원하는 소환사의 정보를 찾았을 때
                if (p.path(RiotIdGameName).asText().equals(riotIdGameName)
                        && p.path(RiotIdTagline).asText().equals(riotIdTagline)) {
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

                    String kda = (build.getDeaths() == 0 ) ? "perfect" : df.format((double)(build.getKills() + build.getAssists())/build.getDeaths());
                    build= build.toBuilder()
                            .kda(kda)
                            .itemList(itemList).summonerSpellList(summonerSpellList).build();
                    matchList.add(build);

                    totalkill +=kills;
                    totalassist += assists;
                    totaldeath+=deaths;
                }
            }
        }
        Long win = calWin(matchList);
        double totalKda = Double.parseDouble(df.format(((double) (totalkill + totalassist)) / ((double) totaldeath)));
        summonerInfo = summonerInfo.toBuilder().totalKda(totalKda).recentWins(win).recentLosses(LOL.INFO.getGameCount()-win).build();
        return new CreateSummonerInfoDto(summonerInfo,matchList);
    }

    public Long calWin(List<MatchInfo> matchInfoList){
        Long win=0L;
        for (MatchInfo matchInfo :matchInfoList ) {
            if(matchInfo.getResult().equals("true")) {
                win++;
            }
        }
        return win;
    }

    //이번 시즌 랭크정보 가져오기
    public SummonerInfo callSummonerProfile(String summonerId, String tag) throws JsonProcessingException {
        DecimalFormat df = getDecimalFormat();
        ObjectMapper mapper = new ObjectMapper();
        String block = createWebClient(BaseUrlKR, "/lol/league/v4/entries/by-summoner/" + summonerId)
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    throw new AppException(ErrorCode.NOTFOUND, "소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.");
                })
                .block();

        JsonNode jsonNode = mapper.readTree(block).get(0);
        Map map = mapper.convertValue(jsonNode, Map.class);
        if (map == null) {
            log.info("랭크 정보 없음.");
            return new SummonerInfo();
        }
        Long win = Long.valueOf(map.get("wins").toString());
        Long loss = Long.valueOf(map.get("losses").toString());
        double totalAvgOfWin= Double.parseDouble(df.format( (double)win /((double) win + (double) loss) * 100));
        return SummonerInfo.builder()
                .leagueId(map.get("leagueId").toString())
                .tier(map.get("tier").toString())
                .tierRank(convertRomanToArabic(map.get("rank").toString()))
                // error point
//                .summonerName(map.get("summonerName").toString())
                .summonerTag(tag)
                .leaguePoints(Long.valueOf(map.get("leaguePoints").toString()))
                .totalWins(win)
                .totalLosses(loss)
                .totalAvgOfWin(totalAvgOfWin)
                .build();
    }

    public List<MostChampion> calcMostChampion(List<MatchInfo> matchInfoList){
        DecimalFormat df = getDecimalFormat();
        Map<String, List<MatchInfo>> sortedChampionList = matchInfoList.stream().collect(Collectors.groupingBy(MatchInfo::getChampionName));
        List<String> topThreeChampions = sortedChampionList.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, List<MatchInfo>> entry) -> entry.getValue().size()).reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return topThreeChampions.stream()
                .map(champion -> {
                    List<MatchInfo> matchInfos = sortedChampionList.get(champion);
                    long totalKills = matchInfos.stream().mapToLong(MatchInfo::getKills).sum();
                    long totalDeaths = matchInfos.stream().mapToLong(MatchInfo::getDeaths).sum();
                    long totalAssists = matchInfos.stream().mapToLong(MatchInfo::getAssists).sum();
                    long count = sortedChampionList.get(champion).stream().count();
                    long winCount = matchInfos.stream().filter(match -> "true".equals(match.getResult())).count();
                    long lossCount = count - winCount;

                    String kda = (totalDeaths == 0) ? "PF" : df.format((double)(totalKills + totalAssists) / totalDeaths);

                    double avgOfWin = Double.parseDouble(df.format( (double)winCount / count * 100));

                    return MostChampion.builder()
                            .kills(totalKills)
                            .deaths(totalDeaths)
                            .assists(totalAssists)
                            .kda(kda)
                            .championName(champion)
                            .count(count)
                            .win(winCount)
                            .loss(lossCount)
                            .avgOfWin(avgOfWin)
                            .build();
                }).collect(Collectors.toList());
    }

    private static DecimalFormat getDecimalFormat() {
        DecimalFormat df = new DecimalFormat("0.0");
        return df;
    }

    private WebClient.ResponseSpec createWebClient(String baseUrl, String url) {
        WebClient webClient = WebClient.create(baseUrl);
        return webClient.get()
                .uri(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Origin", "https://developer.riotgames.com")
                .header(RiotTokenHeader, ApiKey).retrieve();
    }


    private Long convertRomanToArabic(String roman) {
        switch (roman) {
            case "I":
                return 1L;
            case "II":
                return 2L;
            case "III":
                return 3L;
            case "IV":
                return 4L;
            default:
                throw new IllegalArgumentException("Invalid Roman numeral");
        }
    }



}
