package com.example.reactmapping.domain.lol.summonerInfo.service.riotApi;

import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.domain.lol.util.LoLApiUtil;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GetSummonerInfoWithApi {
    private final DataUtil dataUtil;
    private final LoLApiUtil loLApiUtil;

    public String getPuuId(String summonerName,String summonerTage){
       return loLApiUtil.getJsonResponse(LOL.BaseUrlAsia
                , "/riot/account/v1/accounts/by-riot-id/" + summonerName + "/" + summonerTage
                , "라이엇 이름 또는 태그가 일치하지 않습니다.").get("puuid").asText();
    }
    public String getSummonerId(String puuId) {
        return loLApiUtil.getJsonResponse(LOL.BaseUrlKR
                , "/lol/summoner/v4/summoners/by-puuid/" + puuId
                , "소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.").get("id").asText();
    }
    public SummonerInfo getSummonerProfile(String summonerId, String tag) {
        JsonNode jsonResponse =loLApiUtil.getJsonResponse(LOL.BaseUrlKR
                ,"/lol/league/v4/entries/by-summoner/" + summonerId
                ,"소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.");
        return parseSummonerInfo(jsonResponse, tag);
    }

    private SummonerInfo parseSummonerInfo(JsonNode jsonResponse, String tag) {
        long win = jsonResponse.get("wins").asLong();
        long loss = jsonResponse.get("losses").asLong();
        double winRate = calculateWinRate(win, loss);

        return SummonerInfo.builder()
                .leagueId(jsonResponse.get("leagueId").asText())
                .tier(jsonResponse.get("tier").asText())
                .tierRank(dataUtil.convertRomanToArabic(jsonResponse.get("rank").asText()))
                .summonerTag(tag)
                .leaguePoints(Long.valueOf(jsonResponse.get("leaguePoints").asText()))
                .matchList(new ArrayList<>())
                .totalWins(win)
                .totalLosses(loss)
                .totalAvgOfWin(winRate)
                .build();
    }

    private double calculateWinRate(long wins, long losses) {
        DecimalFormat df = dataUtil.getDecimalFormat();
        if (losses == 0 && wins == 0) return 0.0;
        double rate = ((double) wins / (wins + losses)) * 100;
        return Double.parseDouble(df.format(rate));
    }
}
