package com.example.reactmapping.domain.lol;

import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.matchInfo.domain.MatchInfo;
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
    private final DataUtil dataUtil;
    public List<MostChampion> calcMostChampion(List<MatchInfo> matchInfoList) {
        DecimalFormat df = dataUtil.getDecimalFormat();
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

                    String kda = (totalDeaths == 0) ? "PF" : df.format((double) (totalKills + totalAssists) / totalDeaths);

                    double avgOfWin = Double.parseDouble(df.format((double) winCount / count * 100));

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
}
