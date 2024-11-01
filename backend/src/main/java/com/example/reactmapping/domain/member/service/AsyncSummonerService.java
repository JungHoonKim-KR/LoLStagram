package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.service.CreateSummonerInfoService;
import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncSummonerService {
    private final MemberRepository memberRepository;
    private final CreateSummonerInfoService createSummonerInfoService;

    @Async
    protected void createSummonerInfo(Member member, String summonerName, String summonerTag,String puuId) {
        SummonerInfo summonerInfo = createSummonerInfoService.createSummonerInfo(puuId,summonerName, summonerTag);
        member.setSummonerInfo(summonerInfo);
        memberRepository.save(member);
    }
}
