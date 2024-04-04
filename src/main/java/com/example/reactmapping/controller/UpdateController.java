package com.example.reactmapping.controller;

import com.example.reactmapping.dto.*;
import com.example.reactmapping.service.AuthService;
import com.example.reactmapping.service.MatchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "Authorization")
public class UpdateController {
    private final AuthService authService;
    private final MatchService matchService;
    @PostMapping("/update")
    public SummonerInfoDto update(@RequestBody SummonerNameAndTagDto summonerNameAndTagDto) throws JsonProcessingException {
        CallSummonerInfoResponse callSummonerInfoResponse = authService.callSummonerInfo(summonerNameAndTagDto.getSummonerName(), summonerNameAndTagDto.getSummonerTag());
        return SummonerInfoDto.entityToDto(callSummonerInfoResponse.getSummonerInfo());
    }

    @PostMapping("/profileUpdate")
    public void profileUpdate(@RequestPart("profileDto") ProfileUpdateDto profileUpdateDto
                               , @RequestPart("image")MultipartFile img) throws IOException {
        profileUpdateDto.setImg(img);
        authService.updateProfile(profileUpdateDto);
    }

    @PostMapping("/matchUpdate")
    public MatchInfoResultDto matchUpdate(@RequestBody MatchInfoRequestDto matchInfoRequestDto, @PageableDefault(size =10) Pageable pageable){
        return matchService.getMatchList(pageable,matchInfoRequestDto.getType(), matchInfoRequestDto.getSummonerId());
    }
}

