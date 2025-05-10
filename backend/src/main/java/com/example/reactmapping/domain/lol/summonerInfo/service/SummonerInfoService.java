package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.repository.SummonerInfoRepository;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SummonerInfoService {
    private final SummonerInfoRepository summonerInfoRepository;

    public Optional<SummonerInfo> findSummonerInfoBySummonerNameAndTag(SummonerNameAndTagDto summonerNameAndTagDto){
         return summonerInfoRepository.findBySummonerNameAndSummonerTag(summonerNameAndTagDto.getSummonerName(), summonerNameAndTagDto.getSummonerTag());
    }
    public SummonerInfo findSummonerInfoById(String summonerId) {
        log.info(summonerId);
        return summonerInfoRepository.findBySummonerId(summonerId).orElseThrow(() ->new AppException(ErrorCode.NOTFOUND,"존재하지 않는 소환사입니다."));
    }
    @Transactional
    public void saveSummonerInfo(SummonerInfo summonerInfo) {
        log.info("소환사 정보 저장");
        summonerInfoRepository.save(summonerInfo);
    }
    @Async
    @Transactional
    public CompletableFuture<SummonerInfo> saveAsync(SummonerInfo summonerInfo) {
        saveSummonerInfo(summonerInfo); // void 메서드지만 실제 저장은 이 시점에 완료
        return CompletableFuture.completedFuture(summonerInfo); // 저장된 객체를 반환 반환을 해야 뒤에 thenAccept를 활용할 수 있음
    }
}
