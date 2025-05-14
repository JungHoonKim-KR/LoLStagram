package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.repository.SummonerInfoRepository;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SummonerInfoService {
    private final SummonerInfoRepository summonerInfoRepository;
    @Transactional(readOnly = true)
    public Optional<SummonerInfo> findSummonerInfoBySummonerNameAndTag(SummonerNameAndTagDto summonerNameAndTagDto){
         return summonerInfoRepository.findBySummonerNameAndSummonerTag(summonerNameAndTagDto.getSummonerName(), summonerNameAndTagDto.getSummonerTag());
    }
    @Transactional(readOnly = true)
    public SummonerInfo findSummonerInfoById(String summonerId) {
        log.info(summonerId);
        return summonerInfoRepository.findBySummonerId(summonerId).orElseThrow(() ->new AppException(ErrorCode.NOTFOUND,"존재하지 않는 소환사입니다."));
    }

    public void saveSummonerInfo(SummonerInfo summonerInfo) {
        log.info("소환사 정보 저장");
        summonerInfoRepository.save(summonerInfo);
    }

}
