package com.example.reactmapping.domain.member.service;

import com.example.reactmapping.domain.member.domain.Member;
import com.example.reactmapping.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> findMemberByEmailId(String emailId) {
        return memberRepository.findMemberByEmailId(emailId);
    }
    public void save(Member member) {
        memberRepository.save(member);
    }
    public Optional<Member> findMemberById(Long id) {
        return memberRepository.findById(id);
    }
}
