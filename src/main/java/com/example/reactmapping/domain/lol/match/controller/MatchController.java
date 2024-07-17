package com.example.reactmapping.domain.lol.match.controller;

import com.example.reactmapping.domain.lol.match.dto.MatchRequestDto;
import com.example.reactmapping.domain.lol.match.dto.MatchResultDto;
import com.example.reactmapping.domain.lol.match.service.GetMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchController {
    private final GetMatchService getMatchService;
    @PutMapping("/match/update")
    public MatchResultDto matchUpdate(@RequestBody MatchRequestDto MatchRequestDto, @PageableDefault(size =10) Pageable pageable){
        return getMatchService.getMatchList(pageable,MatchRequestDto.getType(), MatchRequestDto.getSummonerId());
    }
}
