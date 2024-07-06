package com.example.reactmapping.domain.matchInfo.controller;

import com.example.reactmapping.domain.matchInfo.dto.MatchInfoRequestDto;
import com.example.reactmapping.domain.matchInfo.dto.MatchInfoResultDto;
import com.example.reactmapping.domain.matchInfo.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;
    @PutMapping("/match/update")
    public MatchInfoResultDto matchUpdate(@RequestBody MatchInfoRequestDto matchInfoRequestDto, @PageableDefault(size =10) Pageable pageable){
        return matchService.getMatchList(pageable,matchInfoRequestDto.getType(), matchInfoRequestDto.getSummonerId());
    }
}
