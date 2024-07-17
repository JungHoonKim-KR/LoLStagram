package com.example.reactmapping.domain.lol.summonerInfo.controller;

import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.UpdateRequestDto;
import com.example.reactmapping.domain.lol.summonerInfo.service.SummonerInfoService;
import com.example.reactmapping.domain.lol.summonerInfo.service.UpdateSummonerInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/summoner")
public class SummonerController {
    private final UpdateSummonerInfo updateSummonerInfo;
    private final SummonerInfoService repositoryService;
    @PutMapping("/update")
    public SummonerInfoDto update(@RequestBody UpdateRequestDto updateRequestDto) throws JsonProcessingException {
        SummonerInfo summonerInfoById = repositoryService.findSummonerInfoById(updateRequestDto.getSummonerId());
        SummonerInfo callSummonerInfoResponse = updateSummonerInfo.getUpdatedSummonerInfo(summonerInfoById);
        return SummonerInfoDto.entityToDto(callSummonerInfoResponse);
    }
}
