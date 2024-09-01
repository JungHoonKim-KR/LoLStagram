package com.example.reactmapping.domain.lol.match.controller;

import com.example.reactmapping.domain.lol.match.dto.MatchRequestDto;
import com.example.reactmapping.domain.lol.match.dto.MatchResultDto;
import com.example.reactmapping.domain.lol.match.service.GetMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MatchController {
    private final GetMatchService getMatchService;
    @PutMapping("/match/update")
    public ResponseEntity<MatchResultDto> matchUpdate(@RequestBody MatchRequestDto MatchRequestDto, @PageableDefault(size =10) Pageable pageable){
        log.info("매치 정보 업데이트 완료");
        return ResponseEntity.ok().body(getMatchService.getMatchList(pageable,MatchRequestDto.getType(), MatchRequestDto.getSummonerId()));
    }
}
