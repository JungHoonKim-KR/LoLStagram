package com.example.reactmapping.domain.lol.summonerInfo.service;

import com.example.reactmapping.domain.Image.dto.ImageResourceUrlMaps;
import com.example.reactmapping.domain.Image.service.ImageService;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SummonerInfoService summonerInfoService;
    private final ImageService imageService;
    private final CreateSummonerInfoService createSummonerInfoService;

    public SummonerInfoDto getOrCreateSummonerDto(SummonerNameAndTagDto dto) {
        SummonerInfo entity = summonerInfoService
                .findSummonerInfoBySummonerNameAndTag(dto)
                .orElseGet(() -> createSummonerInfoService.createSummonerInfo(null, dto.getSummonerName(), dto.getSummonerTag()));

        ImageResourceUrlMaps imageURLMaps = imageService.getImageURLMaps(entity.getMatchList());

        return SummonerInfoDto.entityToDto(entity, imageURLMaps);
    }
}
