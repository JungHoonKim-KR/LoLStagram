package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.lol.match.dto.MatchDto;
import com.example.reactmapping.domain.lol.match.service.GetMatchService;
import com.example.reactmapping.domain.lol.summonerInfo.service.SummonerInfoService;
import com.example.reactmapping.global.security.jwt.JwtService;
import com.example.reactmapping.global.security.jwt.JwtUtil;
import com.example.reactmapping.global.security.jwt.TokenDto;
import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.domain.member.dto.*;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.summonerInfo.dto.SummonerInfoDto;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.global.security.cookie.CookieUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final GetMatchService getMatchService;

    public LoginResponseDto login(HttpServletResponse response, String requestEmail, String requestPassword) throws JsonProcessingException {
        Member member = getMemberByEmail(requestEmail);
        verifyPassword(requestPassword, member);
        SummonerInfo summonerInfo = getSummonerInfo(member);
        TokenDto tokenDto = jwtService.generateToken(requestEmail);
        response.addCookie(cookieUtil.createCookie(Token.TokenName.refreshToken,tokenDto.getRefreshToken()));
        return getLoginResponseDto(member, tokenDto.getAccessToken(), summonerInfo);
    }

    public LoginResponseDto socialLogin(String accessToken,Pageable pageable,String type){
        Member member = getMemberByEmail(jwtUtil.getUserEmail(accessToken));
        SummonerInfo summonerInfo = getSummonerInfo(member);
        List<MatchDto> matchList = getMatchService.getMatchList(pageable, type,summonerInfo.getSummonerId()).getMatchDtoList();
        return getLoginResponseDto(member, accessToken, summonerInfo);
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

    private LoginResponseDto getLoginResponseDto(Member member, String accessToken, SummonerInfo summonerInfo) {
        MemberDto memberDto = new MemberDto(member.getId(), member.getEmailId(), member.getUsername(), member.getProfileImage());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .username(member.getUsername())
                .summonerInfoDto(SummonerInfoDto.entityToDto(summonerInfo))
                .memberDto(memberDto)
                .build();
    }

}
