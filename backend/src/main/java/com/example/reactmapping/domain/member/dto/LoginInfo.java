package com.example.reactmapping.domain.member.dto;

import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class LoginInfo {
    public LoginInfo(String accessToken, String refreshToken, String username, SummonerInfoDto summonerInfoDto, MemberDto memberDto) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.summonerInfoDto = summonerInfoDto;
        this.memberDto = memberDto;
    }

    private String accessToken;
    private String refreshToken;
    private String username;
    private SummonerInfoDto summonerInfoDto;
    private MemberDto memberDto;
}
