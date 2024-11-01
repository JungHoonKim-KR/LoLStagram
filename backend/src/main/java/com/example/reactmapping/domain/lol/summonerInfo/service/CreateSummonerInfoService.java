package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.CalcMostChampion;
import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.service.CreateMatchService;
import com.example.reactmapping.domain.lol.match.service.GetMatchService;
import com.example.reactmapping.domain.lol.summonerInfo.entity.BasicInfo;
import com.example.reactmapping.domain.lol.summonerInfo.entity.RecentRecord;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.service.riotApi.GetSummonerInfoWithApi;
import com.example.reactmapping.domain.lol.summonerInfo.util.SummonerUtil;
import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateSummonerInfoService {
    private final CreateMatchService createMatchService;
    private final GetMatchService getMatchService;
    private final GetSummonerInfoWithApi getSummonerInfoWithApi;
    private final CalcMostChampion calcMostChampion;
    private final SummonerUtil summonerUtil;
    private final SummonerInfoService summonerInfoService;
    public SummonerInfo createSummonerInfo(String puuId, String summonerName, String summonerTag) {
        log.info("소환사 생성 시작");
        if(puuId == null)
            puuId = getSummonerInfoWithApi.getPuuId(summonerName, summonerTag);
        String summonerId = getSummonerInfoWithApi.getSummonerId(puuId);
        BasicInfo summonerBasic = getSummonerInfoWithApi.getSummonerBasic(summonerId, summonerTag);

        List<Match> matchList = new LinkedList<>();
        List<String> matchIds = getMatchService.getMatchIdList(puuId, 0, LOL.gameCount);

        for (String matchId : matchIds) {
            Match match = createMatchService.createMatch(matchId, summonerName, summonerTag);
            matchList.add(match);
        }

        RecentRecord recentRecord = summonerUtil.createRecentRecord(matchList);
        List<MostChampion> mostChampions = calcMostChampion.calcMostChampion(matchList);

        SummonerInfo summonerInfo = new SummonerInfo(summonerId, summonerName, summonerTag, puuId, summonerBasic, recentRecord, matchList, mostChampions);

        for(Match match : matchList) {
            match.setSummonerInfo(summonerInfo);
        }
        summonerInfoService.saveSummonerInfo(summonerInfo);
        log.info("소환사 생성 완료");
        return summonerInfo;
    }

}
