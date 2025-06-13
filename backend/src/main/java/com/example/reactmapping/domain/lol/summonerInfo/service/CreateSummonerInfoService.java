package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.riotAPI.GetMatchInfoWithAPI;
import com.example.reactmapping.domain.lol.match.service.CreateMatchService;
import com.example.reactmapping.domain.lol.summonerInfo.entity.BasicInfo;
import com.example.reactmapping.domain.lol.summonerInfo.entity.RecentRecord;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.riotAPI.GetSummonerInfoWithApi;
import com.example.reactmapping.domain.lol.summonerInfo.util.SummonerUtil;
import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.global.norm.LOL;
import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateSummonerInfoService {
    private final CreateMatchService createMatchService;
    private final GetSummonerInfoWithApi getSummonerInfoWithApi;
    private final SummonerUtil summonerUtil;
    private final GetMatchInfoWithAPI getMatchInfoWithAPI;
    private final SummonerInfoService summonerInfoService;
    private final RateLimiter limiter;
    public SummonerInfo createSummonerInfo(String puuId, String summonerName, String summonerTag) {
        SummonerInfo summonerInfo = buildSummonerInfo(puuId, summonerName, summonerTag);
        summonerInfoService.saveSummonerInfo(summonerInfo);
        log.info("소환사 생성 완료");
        return summonerInfo;
    }

    private SummonerInfo buildSummonerInfo(String optionalPuuId, String summonerName, String summonerTag) {
        String puuId;
        if(optionalPuuId == null)
            puuId = RateLimiter.decorateSupplier(limiter, ()->
                    getSummonerInfoWithApi.getPuuId(summonerName, summonerTag)
            ).get();
        else puuId = optionalPuuId;
        String summonerId = RateLimiter.decorateSupplier(limiter, ()->
                getSummonerInfoWithApi.getSummonerId(puuId))
                .get();

        BasicInfo summonerBasic = RateLimiter.decorateSupplier(limiter, ()->
                getSummonerInfoWithApi.getSummonerBasic(summonerId)
                ).get();

        List<String> matchIds = RateLimiter.decorateSupplier(limiter, ()->
                getMatchInfoWithAPI.getMatchIdList(puuId, 0, LOL.gameCount)
                ).get();

        List<Match> matchList = createMatchService.createMatchParallel(matchIds, summonerName, summonerTag);

        RecentRecord recentRecord = summonerUtil.createRecentRecord(matchList);
        List<MostChampion> mostChampions = summonerUtil.calcMostChampion(matchList);

        SummonerInfo summonerInfo = new SummonerInfo(summonerId, summonerName, summonerTag, puuId, summonerBasic, recentRecord, matchList, mostChampions);
        for(Match match : matchList) {
            match.setSummonerInfo(summonerInfo);
        }

        return summonerInfo;
    }

}
