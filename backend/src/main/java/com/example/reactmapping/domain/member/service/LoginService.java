package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.Image.service.ImageService;
import com.example.reactmapping.domain.lol.summonerInfo.service.SummonerInfoService;
import com.example.reactmapping.global.security.jwt.JwtService;
import com.example.reactmapping.global.security.jwt.JwtUtil;
import com.example.reactmapping.global.security.jwt.TokenDto;
import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.dto.*;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoginService {
    private final MemberRepository memberRepository;
    private final SummonerInfoService summonerInfoRepositoryService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;

    public LoginInfo login(String requestEmail, String requestPassword) {
        Member member = getMemberByEmail(requestEmail);
        verifyPassword(requestPassword, member);
        SummonerInfo summonerInfo = getSummonerInfo(member);
        TokenDto tokenDto = jwtService.generateToken(requestEmail);
        return getLoginInfo(member, tokenDto.getAccessToken(), tokenDto.getRefreshToken(), summonerInfo);
    }

    public LoginInfo socialLogin(String accessToken){
        Member member = getMemberByEmail(jwtUtil.getUserEmail(accessToken));
        SummonerInfo summonerInfo = getSummonerInfo(member);
        return getLoginInfo(member, accessToken, String.valueOf(Optional.empty()), summonerInfo);
    }

    private SummonerInfo getSummonerInfo(Member member) {
        return summonerInfoRepositoryService.findSummonerInfoById(member.getSummonerInfo().getSummonerId());
    }

    private void verifyPassword(String requestPassword, Member member) {
        if (!bCryptPasswordEncoder.matches(requestPassword, member.getPassword()))
            throw new AppException(ErrorCode.ACCESS_ERROR, "비밀번호가 일치하지 않습니다.");
    }


    private Member getMemberByEmail(String requestEmail) {
        Member member;
        Optional<Member> findMember = memberRepository.findMemberByEmailId(requestEmail);
        if (findMember.isPresent()) {
            member = findMember.get();
        } else throw new AppException(ErrorCode.NOTFOUND, requestEmail + "는 존재하지 않습니다.");
        return member;
    }

    private LoginInfo getLoginInfo(Member member, String accessToken, String refreshToken, SummonerInfo summonerInfo) {
        MemberDto memberDto = new MemberDto(member.getId(), member.getEmailId(), member.getUsername());

        return LoginInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(member.getUsername())
                .summonerInfoDto(SummonerInfoDto.entityToDto(summonerInfo, imageService))
                .memberDto(memberDto)
                .build();
    }

}
