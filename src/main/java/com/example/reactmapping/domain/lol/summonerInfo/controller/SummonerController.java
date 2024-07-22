package com.example.reactmapping.domain.lol.summonerInfo.controller;

import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.UpdateRequestDto;
import com.example.reactmapping.domain.lol.summonerInfo.service.GetSummonerInfoService;
import com.example.reactmapping.domain.lol.summonerInfo.service.SummonerInfoService;
import com.example.reactmapping.domain.lol.summonerInfo.service.UpdateSummonerInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/summoner")
@Slf4j
public class SummonerController {
    private final UpdateSummonerInfo updateSummonerInfo;
    private final SummonerInfoService summonerInfoService;
    private final GetSummonerInfoService summonerService;

    @PutMapping("/update")
    public SummonerInfoDto update(@RequestBody UpdateRequestDto updateRequestDto) throws JsonProcessingException {
        SummonerInfo summonerInfoById = summonerInfoService.findSummonerInfoById(updateRequestDto.getSummonerId());
        SummonerInfo callSummonerInfoResponse = updateSummonerInfo.getUpdatedSummonerInfo(summonerInfoById);
        log.info("소환사 정보 업데이트 완료");
        return SummonerInfoDto.entityToDto(callSummonerInfoResponse);
    }

    @PostMapping("/search")
    public SummonerInfoDto search(@RequestBody SummonerNameAndTagDto summonerNameAndTagDto){
        log.info("소환사 검색 완료");
        return SummonerInfoDto.entityToDto(summonerService.searchOrCreateSummoner(summonerNameAndTagDto));
    }

}
