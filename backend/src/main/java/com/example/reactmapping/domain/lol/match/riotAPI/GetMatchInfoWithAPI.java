package com.example.reactmapping.domain.lol.match.riotAPI;

import com.example.reactmapping.domain.lol.util.LoLApiUtil;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class GetMatchInfoWithAPI {
    private final LoLApiUtil loLApiUtil;
    public JsonNode getMatch(String matchId){
        return loLApiUtil.getJsonResponse(LOL.BaseUrlAsia, "/lol/match/v5/matches/" + matchId, "경기를 찾을 수 없습니다.")
                .path("info");
    }
    // 최근 대전기록 가져오기
    public List<String> getMatchIdList(String puuId, int startGame, int count) {
        String Url = String.format("/lol/match/v5/matches/by-puuid/%s/ids?start=%s&count=%s", puuId, startGame, count);
        return loLApiUtil.createWebClient(LOL.BaseUrlAsia, Url).bodyToMono(List.class).block();
    }
}
