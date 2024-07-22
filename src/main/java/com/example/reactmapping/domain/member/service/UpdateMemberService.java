package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.service.GetSummonerInfoService;
import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.domain.member.dto.ProfileUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateMemberService {
    private final MemberService memberService;
    private final GetSummonerInfoService searchSummonerService;
    public void updateProfile(ProfileUpdateDto profileUpdateDto){
        Member member = memberService.findMemberById(profileUpdateDto.getId()).get();
        SummonerInfo summonerInfo = searchSummonerService.searchOrCreateSummoner(new SummonerNameAndTagDto(profileUpdateDto.getSummonerName(), profileUpdateDto.getSummonerTag()));
        member = member.toBuilder()
                .summonerInfo(summonerInfo)
                .build();
        memberService.save(member);
    }
}
