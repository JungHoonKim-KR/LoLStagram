package com.example.reactmapping.domain.summonerInfo.controller;

import com.example.reactmapping.domain.member.dto.CallSummonerInfoResponse;
import com.example.reactmapping.domain.member.service.AuthService;
import com.example.reactmapping.domain.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.domain.summonerInfo.dto.SummonerNameAndTagDto;
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
    private final AuthService authService;
    @PutMapping("/update")
    public SummonerInfoDto update(@RequestBody SummonerNameAndTagDto summonerNameAndTagDto) throws JsonProcessingException {
        CallSummonerInfoResponse callSummonerInfoResponse = authService.callSummonerInfo(summonerNameAndTagDto.getSummonerName(), summonerNameAndTagDto.getSummonerTag());
        return SummonerInfoDto.entityToDto(callSummonerInfoResponse.getSummonerInfo());
    }
}
