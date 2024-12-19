package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerNameAndTagDto;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.service.SummonerInfoService;
import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.dto.JoinDTO;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AsyncSummonerService asyncSummonerService;
    private final SummonerInfoService summonerInfoService;

    public Member join(JoinDTO dto, String puuId) {
        // 회원 아이디가 이미 존재하는지
        if (memberRepository.findMemberByEmailId(dto.getEmailId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATED, dto.getEmailId() + "는 이미 존재합니다.");
        }
        Member member = createMember(dto);
        Optional<SummonerInfo> summonerInfoBySummonerNameAndTag = summonerInfoService.findSummonerInfoBySummonerNameAndTag(new SummonerNameAndTagDto(dto.getSummonerName(), dto.getSummonerTag()));
        if(summonerInfoBySummonerNameAndTag.isEmpty()){
            asyncSummonerService.createSummonerInfo(member, dto.getSummonerName(), dto.getSummonerTag(),puuId);
        }
        else {
            member.setSummonerInfo(summonerInfoBySummonerNameAndTag.get());
        }
        memberRepository.save(member);

        return member;
    }

    private Member createMember(JoinDTO dto) {
        return Member.builder()
                .emailId(dto.getEmailId())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .role("ROLE_MEMBER")
                .build();
    }
}
