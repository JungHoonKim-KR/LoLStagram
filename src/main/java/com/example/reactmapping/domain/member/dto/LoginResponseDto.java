package com.example.reactmapping.domain.member.dto;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String username;
    private SummonerInfoDto summonerInfoDto;
    private MemberDto memberDto;
    //승률, 전적 추가
}
