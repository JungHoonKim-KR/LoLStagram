package com.example.reactmapping.domain.lol.summonerInfo.controller;

import com.example.reactmapping.domain.Image.service.ImageService;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.UpdateRequestDto;
import com.example.reactmapping.domain.lol.summonerInfo.service.CreateSummonerInfoService;
import com.example.reactmapping.domain.lol.summonerInfo.service.SummonerInfoService;
import com.example.reactmapping.domain.lol.summonerInfo.service.UpdateSummonerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/summoner")
@Slf4j
public class SummonerController {
    private final UpdateSummonerInfo updateSummonerInfo;
    private final SummonerInfoService summonerInfoService;
    private final ImageService imageService;
    private final CreateSummonerInfoService createSummonerInfoService;

    @PutMapping("/update")
    public SummonerInfoDto update(@RequestBody UpdateRequestDto updateRequestDto) {
        SummonerInfo summonerInfoById = summonerInfoService.findSummonerInfoById(updateRequestDto.getSummonerId());
        SummonerInfo callSummonerInfoResponse = updateSummonerInfo.getUpdatedSummonerInfo(summonerInfoById);

        log.info("소환사 정보 업데이트 완료");
        return SummonerInfoDto.entityToDto(callSummonerInfoResponse, imageService);
    }

    @PostMapping("/search")
    public SummonerInfoDto search(@RequestBody SummonerNameAndTagDto summonerNameAndTagDto){

        Optional<SummonerInfo> summonerInfoOptional  = summonerInfoService.findSummonerInfoBySummonerNameAndTag(summonerNameAndTagDto);
        SummonerInfo summonerInfo = summonerInfoOptional.orElseGet(() -> createSummonerInfoService.createSummonerInfo(null, summonerNameAndTagDto.getSummonerName(), summonerNameAndTagDto.getSummonerTag()));
        log.info("소환사 검색 완료");
        return SummonerInfoDto.entityToDto(summonerInfo, imageService);
    }

}
