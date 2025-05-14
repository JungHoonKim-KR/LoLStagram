package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AsyncSaveSummoner {
    private final SummonerInfoService summonerInfoService;

    @Async
    @Transactional
    public CompletableFuture<SummonerInfo> saveAsync(SummonerInfo summonerInfo) {
        summonerInfoService.saveSummonerInfo(summonerInfo);
        return CompletableFuture.completedFuture(summonerInfo); // 저장된 객체를 반환 반환을 해야 뒤에 thenAccept를 활용할 수 있음
    }
}
