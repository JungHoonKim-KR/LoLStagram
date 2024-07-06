package com.example.reactmapping.domain.member.dto;
import com.example.reactmapping.domain.matchInfo.dto.MatchInfoDto;
import com.example.reactmapping.domain.summonerInfo.dto.SummonerInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String username;
    private SummonerInfoDto summonerInfoDto;
    private MemberDto memberDto;
    private List<MatchInfoDto> matchInfoDtoList;
    //승률, 전적 추가
}
