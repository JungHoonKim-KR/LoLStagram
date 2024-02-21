package com.example.reactmapping.controller;

import com.example.reactmapping.dto.CallSummonerInfoResponse;
import com.example.reactmapping.dto.ProfileUpdateDto;
import com.example.reactmapping.dto.SummonerInfoDto;
import com.example.reactmapping.dto.SummonerNameAndTagDto;
import com.example.reactmapping.service.AuthService;
import com.example.reactmapping.service.LoLService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "Authorization")
public class UpdateController {
    private final AuthService authService;
    @PostMapping("/update")
    public SummonerInfoDto update(@RequestBody SummonerNameAndTagDto summonerNameAndTagDto) throws JsonProcessingException {
        CallSummonerInfoResponse callSummonerInfoResponse = authService.callSummonerInfo(summonerNameAndTagDto.getSummonerName(), summonerNameAndTagDto.getSummonerTag());
        return SummonerInfoDto.entityToDto(callSummonerInfoResponse.getSummonerInfo());
    }

    @PostMapping("/profileUpdate")
    public void profileUpdate(@RequestBody ProfileUpdateDto profileUpdateDto) throws JsonProcessingException {
        authService.updateProfile(profileUpdateDto);
    }
}

