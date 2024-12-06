package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.norm.Token;
import com.example.reactmapping.global.security.jwt.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;

    public void logout(String accessToken, String refreshToken) {

        //refreshToken 검증 후 삭제
        Optional<String> refreshTokenObject = tokenRepository.findToken(refreshToken, Token.TokenType.REFRESH.name());
        if(refreshTokenObject.isPresent()){
            tokenRepository.delete(refreshToken);
        }
        //accessToken 블랙리스트에 등록
        tokenRepository.registerBlacklist(accessToken);
        log.info("로그아웃");
    }


    public void save(Member member) {
        memberRepository.save(member);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.NOTFOUND,"회원을 찾지 못했습니다."));
    }
}
