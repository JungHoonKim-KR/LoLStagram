package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetSummonerInfoService {
    private final SummonerInfoService summonerInfoService ;
    private final CreateSummonerInfoService createSummonerInfoService;
    public SummonerInfo searchOrCreateSummoner(SummonerNameAndTagDto summonerNameAndTagDto){
        Optional<SummonerInfo> summonerInfoOptional  = summonerInfoService.findSummonerInfoBySummonerNameAndTag(summonerNameAndTagDto);
        return summonerInfoOptional.orElseGet(() -> createSummonerInfoService.createSummonerInfo(summonerNameAndTagDto.getSummonerName(), summonerNameAndTagDto.getSummonerTag()));
    }
}
