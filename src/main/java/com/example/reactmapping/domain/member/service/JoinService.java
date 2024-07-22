package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.Image.service.ImageCreateService;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.service.CreateSummonerInfoService;
import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.domain.member.dto.JoinDTO;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final MemberService memberService;
    private final CreateSummonerInfoService createSummonerInfoService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageCreateService imageCreateService;
    public Member join(JoinDTO dto) throws IOException {
        // 회원 아이디가 이미 존재하는지
        if (memberService.findMemberByEmailId(dto.getEmailId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATED, dto.getEmailId() + "는 이미 존재합니다.");
        }
        SummonerInfo summonerInfo = createSummonerInfoService.createSummonerInfo(dto.getSummonerName(), dto.getSummonerTag());

        Member member = createMember(dto, summonerInfo);

        if(dto.getImage() != null) {
            String imageUrl = imageCreateService.createImage(dto.getImage());
            member.setProfileImage(imageUrl);
        }
        memberService.save(member);
        return member;
    }

    private Member createMember(JoinDTO dto, SummonerInfo summonerInfo) {
        return Member.builder()
                .emailId(dto.getEmailId())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .role("ROLE_MEMBER")
                .riotIdGameName(dto.getSummonerName())
                .riotIdTagline(dto.getSummonerTag())
                .summonerInfo(summonerInfo)
                .build();
    }
}
