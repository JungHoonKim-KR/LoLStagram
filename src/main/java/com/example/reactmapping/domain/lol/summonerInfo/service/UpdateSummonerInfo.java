package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.CalcMostChampion;
import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.domain.lol.match.service.CompareMatchService;
import com.example.reactmapping.domain.lol.match.service.CreateMatchService;
import com.example.reactmapping.domain.lol.match.service.GetMatchService;
import com.example.reactmapping.domain.lol.match.service.UpdateMatchService;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.service.riotApi.GetSummonerInfoWithApi;
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
    private final CompareMatchService compareMatchService;
    private final CalcMostChampion calcMostChampion;
    private final UpdateMatchService updateMatchService;
    private final SummonerUtil summonerUtil;
    private final GetSummonerInfoWithApi getSummonerInfoWithApi;
    private final CreateMatchService createMatchService;
    private final GetMatchService getMatchService;

    public SummonerInfo getUpdatedSummonerInfo(SummonerInfo summonerInfo){
        updateSummonerInfo(summonerInfo);
        return summonerInfo;
    }
    private void updateSummonerInfo(SummonerInfo summonerInfo) {
        int newGameCount = getNewGameCount(summonerInfo);
        if(newGameCount != LOL.Up_To_Date){
            List<String> matchIds = getMatchService.getMatchIds(summonerInfo.getPuuId(), 0, newGameCount);
            List<Match> newMatchList = getNewMatchList(summonerInfo, matchIds);
            summonerInfo.updateBasicInfo(getSummonerInfoWithApi.getSummonerBasic(summonerInfo.getSummonerId(), summonerInfo.getSummonerTag()));
            updateMatchService.updateMatches(summonerInfo,newGameCount, newMatchList);
            updateMostChampionList(summonerInfo);
            updateRecentRecord(summonerInfo);
        }
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
         summonerInfo.updateRecentRecord(summonerUtil.createRecentRecord(summonerInfo.getMatchList()));
    }
    private void updateMostChampionList(SummonerInfo summonerInfo) {
        List<MostChampion> mostChampionList = calcMostChampion.calcMostChampion(summonerInfo.getMatchList());
        summonerInfo.updateMostChampion(mostChampionList);
    }
}



