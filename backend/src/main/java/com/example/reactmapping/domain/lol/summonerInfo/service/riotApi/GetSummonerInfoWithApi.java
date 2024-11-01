package com.example.reactmapping.domain.lol.summonerInfo.service.riotApi;

import com.example.reactmapping.domain.lol.summonerInfo.entity.BasicInfo;
import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.domain.lol.util.LoLApiUtil;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
@Service
@RequiredArgsConstructor
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
    public BasicInfo getSummonerBasic(String summonerId, String tag) {
        JsonNode jsonResponse =loLApiUtil.getJsonResponse(LOL.BaseUrlKR
                ,"/lol/league/v4/entries/by-summoner/" + summonerId
                ,"소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.");
        if(jsonResponse.isEmpty())
            return new BasicInfo("Unknown League","Unranked",0L,0L,0L,0L,0.0);
        else jsonResponse = jsonResponse.get(0);
        return parseBasicInfo(jsonResponse, tag);
    }

    private BasicInfo parseBasicInfo(JsonNode jsonResponse, String tag) {
        long win = jsonResponse.get("wins").asLong();
        long loss = jsonResponse.get("losses").asLong();
        double winRate = calculateWinRate(win, loss);
        return new BasicInfo(jsonResponse.get("leagueId").asText(),jsonResponse.get("tier").asText(),dataUtil.convertRomanToArabic(jsonResponse.get("rank").asText())
        ,Long.valueOf(jsonResponse.get("leaguePoints").asText()),win,loss,winRate);
    }

    private double calculateWinRate(long wins, long losses) {
        DecimalFormat df = dataUtil.getDecimalFormat();
        if (losses == 0 && wins == 0) return 0.0;
        double rate = ((double) wins / (wins + losses)) * 100;
        return Double.parseDouble(df.format(rate));
    }
}
