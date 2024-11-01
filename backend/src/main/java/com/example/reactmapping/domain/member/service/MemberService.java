package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.member.entity.Member;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void save(Member member) {
        memberRepository.save(member);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.NOTFOUND,"회원을 찾지 못했습니다."));
    }
}
