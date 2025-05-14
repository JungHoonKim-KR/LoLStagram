package com.example.reactmapping.domain.lol.summonerInfo.controller;

import com.example.reactmapping.domain.lol.LoLDataDownloader;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.UpdateRequestDto;
import com.example.reactmapping.domain.lol.summonerInfo.service.SearchService;
import com.example.reactmapping.domain.lol.summonerInfo.service.SummonerInfoService;
import com.example.reactmapping.domain.lol.summonerInfo.service.UpdateSummonerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/summoner")
@Slf4j
public class SummonerController {
    private final UpdateSummonerInfo updateSummonerInfo;
    private final SummonerInfoService summonerInfoService;
    private final SearchService searchService;
    private final LoLDataDownloader loLDataDownloader;
    @PutMapping("/update")
    public SummonerInfoDto update(@RequestBody UpdateRequestDto updateRequestDto) {
        SummonerInfo summonerInfoById = summonerInfoService.findSummonerInfoById(updateRequestDto.getSummonerId());
        SummonerInfoDto summonerInfoDto = updateSummonerInfo.updateSummonerInfoAndGetDto(summonerInfoById);

        log.info("소환사 정보 업데이트 완료");
        return summonerInfoDto;
    }

    @PostMapping("/search")
    public SummonerInfoDto search(@RequestBody SummonerNameAndTagDto summonerNameAndTagDto){
        SummonerInfoDto summonerInfoDto = searchService.getOrCreateSummonerDto(summonerNameAndTagDto);
        log.info("소환사 검색 완료");
        return summonerInfoDto;
    }

    @GetMapping("/enrollalldata")
    public ResponseEntity<?> enrollAll(){
        loLDataDownloader.run();
        return ResponseEntity.status(200).body("업데이트 완료");
    }

}
