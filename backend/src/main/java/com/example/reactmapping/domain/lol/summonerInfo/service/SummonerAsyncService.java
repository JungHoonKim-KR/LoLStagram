package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.repository.SummonerInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummonerAsyncService {
    private final SummonerInfoService summonerInfoService;

    @Async
    public void saveAsync(SummonerInfo summonerInfo){
        summonerInfoService.saveSummonerInfo(summonerInfo);
    }

}
