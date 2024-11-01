package com.example.reactmapping.domain.member.controller;

import com.example.reactmapping.domain.lol.summonerInfo.service.riotApi.GetSummonerInfoWithApi;
import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.dto.JoinDTO;
import com.example.reactmapping.domain.member.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@RestController
@RequiredArgsConstructor
@Slf4j
public class JoinController {
    private final JoinService joinService;
    private final GetSummonerInfoWithApi getSummonerInfoWithApi;
    @Operation(summary = "회원가입", description = "새로운 회원 등록")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDTO dto) throws IOException {
        String puuId = getSummonerInfoWithApi.getPuuId(dto.getSummonerName(), dto.getSummonerTag());
        Member member = joinService.join(dto, puuId);
        log.info("회원가입 완료");
        return ResponseEntity.ok().body(member);
    }
}
