package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.util.DataUtil;
import com.example.reactmapping.domain.lol.util.LoLApiUtil;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Slf4j
public class GetSummonerInfo {
    private final DataUtil dataUtil;
    private final LoLApiUtil loLApiUtil;

    public String getPuuId(String riotIdGameName, String riotIdTagline) {
        log.info("요청 닉네임: " + riotIdGameName + " 요청 태그: " + riotIdTagline);
        return loLApiUtil.getApiResponseOneData(LOL.BaseUrlAsia, "/riot/account/v1/accounts/by-riot-id/" + riotIdGameName + "/" + riotIdTagline, "puuid", "라이엇 이름 또는 태그가 일치하지 않습니다.");
    }

    public String getSummonerId(String puuId) {
        return loLApiUtil.getApiResponseOneData(LOL.BaseUrlKR, "/lol/summoner/v4/summoners/by-puuid/" + puuId, "id", "소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.");
    }
    public SummonerInfo callSummonerProfile(String summonerId, String tag) throws JsonProcessingException {
        DecimalFormat df = dataUtil.getDecimalFormat();
        ObjectMapper mapper = new ObjectMapper();
        String block = loLApiUtil.getApiResponse(LOL.BaseUrlKR, "/lol/league/v4/entries/by-summoner/" + summonerId,
                ErrorCode.NOTFOUND, "소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.");

        JsonNode jsonNode = mapper.readTree(block).get(0);
        Map map = mapper.convertValue(jsonNode, Map.class);
        if (map == null) {
            log.info("랭크 정보 없음.");
            return new SummonerInfo();
        }
        Long win = Long.valueOf(map.get("wins").toString());
        Long loss = Long.valueOf(map.get("losses").toString());
        double totalAvgOfWin = Double.parseDouble(df.format((double) win / ((double) win + (double) loss) * 100));
        return SummonerInfo.builder()
                .leagueId(map.get("leagueId").toString())
                .tier(map.get("tier").toString())
                .tierRank(dataUtil.convertRomanToArabic(map.get("rank").toString()))
                // error point
//                .summonerName(map.get("summonerName").toString())
                .summonerTag(tag)
                .leaguePoints(Long.valueOf(map.get("leaguePoints").toString()))
                .matchList(new ArrayList<>())
                .totalWins(win)
                .totalLosses(loss)
                .totalAvgOfWin(totalAvgOfWin)
                .build();
    }
}
