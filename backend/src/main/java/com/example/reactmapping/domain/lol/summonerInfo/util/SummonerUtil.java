package com.example.reactmapping.domain.lol.summonerInfo.util;

import com.example.reactmapping.domain.Image.service.ImageService;
import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.domain.lol.summonerInfo.entity.RecentRecord;
import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SummonerUtil {

    private static final int TopSize = 3;
    private final String True = "true";
    private final DataUtil dataUtil;
    private final ImageService imageService;

    public List<MostChampion> calcMostChampion(List<Match> MatchList) {
        Map<String, List<Match>> sortedChampionList = MatchList.stream().collect(Collectors.groupingBy(Match::getChampionName));
        List<String> topThreeChampionNames = getTopThreeChampionNames(sortedChampionList);
        Map<String, String> urlsByTypeAndKeys = imageService.findUrlsByTypeAndKeys(LOL.ResourceType.CHAMPION.getType(),topThreeChampionNames);
        return topThreeChampionNames.stream()
                .map(champion -> calculateChampionStats(champion, urlsByTypeAndKeys.get(champion), sortedChampionList)).collect(Collectors.toList());
    }

    private MostChampion calculateChampionStats(String champion, String championURL, Map<String, List<Match>> sortedChampionList) {
        List<Match> Matchs = sortedChampionList.get(champion);
        long totalKills = Matchs.stream().mapToLong(Match::getKills).sum();
        long totalDeaths = Matchs.stream().mapToLong(Match::getDeaths).sum();
        long totalAssists = Matchs.stream().mapToLong(Match::getAssists).sum();
        long count = sortedChampionList.get(champion).stream().count();
        long winCount = Matchs.stream().filter(match -> True.equals(match.getResult())).count();
        long lossCount = count - winCount;
        String kda = dataUtil.calculateKDA(totalKills, totalDeaths, totalAssists);
        double avgOfWin = calWinRate((double) winCount, count);

        return MostChampion.builder()
                .kills(totalKills)
                .deaths(totalDeaths)
                .assists(totalAssists)
                .kda(kda)
                .championURL(championURL)
                .count(count)
                .win(winCount)
                .loss(lossCount)
                .avgOfWin(avgOfWin)
                .build();
    }
    public RecentRecord createRecentRecord(List<Match> matchList) {
        long totalkill = 0, totaldeath = 0, totalassist = 0;
        for (Match match : matchList) {
            log.info("{}, {}", match.getMatchId(), match.getKills());
            totalkill += match.getKills();
            totaldeath += match.getDeaths();
            totalassist += match.getAssists();
        }
        Long win = calWin(matchList);
        String kda = dataUtil.calculateKDA(totalkill, totaldeath, totalassist);

        return new RecentRecord(win, LOL.gameCount - win, kda);
    }

    private static List<String> getTopThreeChampionNames(Map<String, List<Match>> sortedChampionList) {
        return sortedChampionList.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, List<Match>> entry) -> entry.getValue().size()).reversed())
                .limit(TopSize)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calWinRate(double winCount, long count) {
        DecimalFormat df = dataUtil.getDecimalFormat();
        return Double.parseDouble(df.format(winCount / count * 100));
    }
    private Long calWin(List<Match> MatchList) {
        Long win = 0L;
        for (Match Match : MatchList) {
            if (Match.getResult().equals(True)) {
                win++;
            }
        }
        return win;
    }
}
