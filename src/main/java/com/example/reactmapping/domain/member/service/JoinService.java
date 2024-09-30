package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.Image.service.ImageCreateService;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.service.CreateSummonerInfoService;
import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.dto.JoinDTO;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final MemberRepository memberRepository;
    private final CreateSummonerInfoService createSummonerInfoService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AsyncSummonerService asyncSummonerService;
    public Member join(JoinDTO dto, String puuId) throws IOException {
        // 회원 아이디가 이미 존재하는지
        if (memberRepository.findMemberByEmailId(dto.getEmailId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATED, dto.getEmailId() + "는 이미 존재합니다.");
        }
//        SummonerInfo summonerInfo = createSummonerInfoService.createSummonerInfo(null,dto.getSummonerName(), dto.getSummonerTag());

        Member member = createMember(dto, null);
        memberRepository.save(member);
        asyncSummonerService.createSummonerInfo(member, dto.getSummonerName(), dto.getSummonerTag(),puuId);
        return member;
    }

    private Member createMember(JoinDTO dto, SummonerInfo summonerInfo) {
        //        if(dto.getImage() != null) {
//            String imageUrl = imageCreateService.createImage(dto.getImage());
//            member.setProfileImage(imageUrl);
//        }
        return Member.builder()
                .emailId(dto.getEmailId())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .role("ROLE_MEMBER")
                .summonerInfo(summonerInfo)
                .build();
    }

}
