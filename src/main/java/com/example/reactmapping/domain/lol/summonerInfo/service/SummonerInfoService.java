package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.repository.SummonerInfoRepository;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SummonerInfoService {
    private final SummonerInfoRepository summonerInfoRepository;
    public SummonerInfo findSummonerInfoById(String summonerId) {
        log.info(summonerId);
        return summonerInfoRepository.findBySummonerId(summonerId).orElseThrow(() ->new AppException(ErrorCode.NOTFOUND,"존재하지 않는 소환사입니다."));
    }
}
