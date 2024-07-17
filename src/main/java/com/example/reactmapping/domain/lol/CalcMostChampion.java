package com.example.reactmapping.domain.lol;

import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.domain.lol.util.DataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalcMostChampion {
    private static final int TopSize = 3;
    private final String True = "true";
    private final DataUtil dataUtil;

    public List<MostChampion> calcMostChampion(List<Match> MatchList) {
        Map<String, List<Match>> sortedChampionList = MatchList.stream().collect(Collectors.groupingBy(Match::getChampionName));
        List<String> topThreeChampions = getTopThreeChampions(sortedChampionList);

        return topThreeChampions.stream()
                .map(champion -> calculateChampionStats(champion, sortedChampionList)).collect(Collectors.toList());
    }

    private MostChampion calculateChampionStats(String champion, Map<String, List<Match>> sortedChampionList) {
        List<Match> Matchs = sortedChampionList.get(champion);
        long totalKills = Matchs.stream().mapToLong(Match::getKills).sum();
        long totalDeaths = Matchs.stream().mapToLong(Match::getDeaths).sum();
        long totalAssists = Matchs.stream().mapToLong(Match::getAssists).sum();
        long count = sortedChampionList.get(champion).stream().count();
        long winCount = Matchs.stream().filter(match -> True.equals(match.getResult())).count();
        long lossCount = count - winCount;

        String kda = calKda(totalDeaths, totalKills, totalAssists);

        double avgOfWin = calWinRate((double) winCount, count);

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
    }

    private static List<String> getTopThreeChampions(Map<String, List<Match>> sortedChampionList) {
        return sortedChampionList.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, List<Match>> entry) -> entry.getValue().size()).reversed())
                .limit(TopSize)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    private String calKda(long totalDeaths, long totalKills, long totalAssists) {
        DecimalFormat df = dataUtil.getDecimalFormat();
        String kda = (totalDeaths == 0) ? "PF" : df.format((double) (totalKills + totalAssists) / totalDeaths);
        return kda;
    }
    private double calWinRate(double winCount, long count) {
        DecimalFormat df = dataUtil.getDecimalFormat();
        return Double.parseDouble(df.format(winCount / count * 100));
    }
}
