package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.domain.lol.match.riotAPI.GetMatchInfoWithAPI;
import com.example.reactmapping.domain.lol.match.service.CompareMatchService;
import com.example.reactmapping.domain.lol.match.service.CreateMatchService;
import com.example.reactmapping.domain.lol.match.service.GetMatchService;
import com.example.reactmapping.domain.lol.match.service.UpdateMatchService;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.riotAPI.GetSummonerInfoWithApi;
import com.example.reactmapping.domain.lol.summonerInfo.util.SummonerUtil;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class UpdateSummonerInfo {
    private final GetMatchInfoWithAPI getMatchInfoWithAPI;
    private final CompareMatchService compareMatchService;
    private final UpdateMatchService updateMatchService;
    private final SummonerUtil summonerUtil;
    private final GetSummonerInfoWithApi getSummonerInfoWithApi;
    private final CreateMatchService createMatchService;
    private final GetMatchService getMatchService;
    private final SummonerInfoService summonerInfoService;

    public SummonerInfo getUpdatedSummonerInfo(SummonerInfo summonerInfo){
        int newGameCount = getNewGameCount(summonerInfo);
        if (newGameCount == LOL.Up_To_Date) {
            return summonerInfo;
        }
        updateSummonerInfo(newGameCount, summonerInfo);
        summonerInfoService.saveSummonerInfo(summonerInfo);
        return summonerInfo;
    }
    private void updateSummonerInfo(int newGameCount, SummonerInfo summonerInfo) {
            List<Match> newMatchList = getNewMatchList(summonerInfo, newGameCount);

            summonerInfo.updateBasicInfo(getSummonerInfoWithApi.getSummonerBasic(summonerInfo.getSummonerId(), summonerInfo.getSummonerTag()));
            updateMatchService.updateMatches(summonerInfo, newMatchList);

            updateMostChampionList(summonerInfo);
            updateRecentRecord(summonerInfo);
    }

    private List<Match> getNewMatchList(SummonerInfo summonerInfo, int newGameCount) {
        List<String> matchIds = getMatchInfoWithAPI.getMatchIdList(summonerInfo.getPuuId(), 0, newGameCount);
        List<Match> newMatchList = getNewMatchList(summonerInfo, matchIds);
        return newMatchList;
    }

    private List<Match> getNewMatchList(SummonerInfo summonerInfo, List<String> matchIds) {
        List<Match> newMatchList = new ArrayList<>();
        for(String matchId : matchIds){
            newMatchList.add(createMatchService.createMatch(matchId, summonerInfo.getSummonerName(), summonerInfo.getSummonerTag()));
        }
        return newMatchList;
    }
    private int getNewGameCount(SummonerInfo summonerInfo){
        return compareMatchService.getCountNewMatch(summonerInfo.getPuuId(), summonerInfo.getSummonerId());
    }
    private void updateRecentRecord(SummonerInfo summonerInfo){
        log.info("updateRecentRecord");
         summonerInfo.updateRecentRecord(summonerUtil.createRecentRecord(summonerInfo.getMatchList()));
    }
    private void updateMostChampionList(SummonerInfo summonerInfo) {
        log.info("calMostChamp");
        List<MostChampion> mostChampionList = summonerUtil.calcMostChampion(summonerInfo.getMatchList());
        log.info("mapping mostChamp");
        summonerInfo.updateMostChampion(mostChampionList);
    }
}



