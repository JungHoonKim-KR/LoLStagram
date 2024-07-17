package com.example.reactmapping.domain.lol.summonerInfo.util;

import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.domain.lol.summonerInfo.domain.RecentRecord;
import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.global.norm.LOL;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;

@Component
public class SummonerUtil {
    public RecentRecord createRecentRecord(List<Match> matchList) {
        long totalkill = 0, totaldeath = 0, totalassist = 0;
        for (Match match : matchList) {
            totalkill += match.getKills();
            totaldeath += match.getDeaths();
            totalassist += match.getAssists();
        }
        Long win = calWin(matchList);
        double kda = calKda(totalkill, totaldeath, totalassist);
        return new RecentRecord(win, LOL.gameCount - win, kda);

    }
    private double calKda(Long totalkill, Long totaldeath, Long totalassist) {
        DecimalFormat df = DataUtil.getDecimalFormat();
        return Double.parseDouble(df.format(((double) (totalkill + totalassist)) / ((double) totaldeath)));
    }
    private Long calWin(List<Match> MatchList) {
        Long win = 0L;
        for (Match Match : MatchList) {
            if (Match.getResult().equals("true")) {
                win++;
            }
        }
        return win;
    }
}
