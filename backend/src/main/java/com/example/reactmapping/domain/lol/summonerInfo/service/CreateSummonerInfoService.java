package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.Image.dto.ImageResourceUrlMaps;
import com.example.reactmapping.domain.Image.service.ImageService;
import com.example.reactmapping.domain.lol.dto.MostChampion;
import com.example.reactmapping.domain.lol.match.riotAPI.GetMatchInfoWithAPI;
import com.example.reactmapping.domain.lol.match.service.CreateMatchService;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.entity.BasicInfo;
import com.example.reactmapping.domain.lol.summonerInfo.entity.RecentRecord;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.riotAPI.GetSummonerInfoWithApi;
import com.example.reactmapping.domain.lol.summonerInfo.util.SummonerUtil;
import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateSummonerInfoService {
    private final CreateMatchService createMatchService;
    private final GetSummonerInfoWithApi getSummonerInfoWithApi;
    private final SummonerUtil summonerUtil;
    private final SummonerInfoService summonerInfoService;
    private final GetMatchInfoWithAPI getMatchInfoWithAPI;
    private final ImageService imageService;

    public SummonerInfoDto getOrCreateSummonerDto(SummonerNameAndTagDto dto) {
        SummonerInfo entity = summonerInfoService
                .findSummonerInfoBySummonerNameAndTag(dto)
                .orElseGet(() -> createSummonerInfo(null, dto.getSummonerName(), dto.getSummonerTag()));

        ImageResourceUrlMaps imageURLMaps = imageService.getImageURLMaps(entity.getMatchList());

        return SummonerInfoDto.entityToDto(entity, imageURLMaps);
    }


    public SummonerInfo createSummonerInfo(String puuId, String summonerName, String summonerTag) {
        if(puuId == null)
            puuId = getSummonerInfoWithApi.getPuuId(summonerName, summonerTag);

        String summonerId = getSummonerInfoWithApi.getSummonerId(puuId);
        BasicInfo summonerBasic = getSummonerInfoWithApi.getSummonerBasic(summonerId, summonerTag);
        List<String> matchIds = getMatchInfoWithAPI.getMatchIdList(puuId, 0, LOL.gameCount);

//        n번 API 호출 : 비효율적
//        List<Match> matchList = new LinkedList<>();

//        log.info("매치 객체 생성"); // 시간 많이 걸림
//        for (String matchId : matchIds) {
//            Match match = createMatchService.createMatch(matchId, summonerName, summonerTag);
//            matchList.add(match);
//        }

        // 병렬 처리
        List<Match> matchList = createMatchService.createMatchParallel(matchIds, summonerName, summonerTag);
        RecentRecord recentRecord = summonerUtil.createRecentRecord(matchList);
        List<MostChampion> mostChampions = summonerUtil.calcMostChampion(matchList);


        // 객체 생성
        SummonerInfo summonerInfo = new SummonerInfo(summonerId, summonerName, summonerTag, puuId, summonerBasic, recentRecord, matchList, mostChampions);
        for(Match match : matchList) {
            match.setSummonerInfo(summonerInfo);
        }
        summonerInfoService.saveAsync(summonerInfo).thenRun(()->log.info("소환사 저장 완료"));
        log.info("소환사 생성 완료");
        return summonerInfo;
    }

}
