package com.example.reactmapping.domain.member.repository;

import com.example.reactmapping.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member>findMemberByEmailId(String emailId);
    Optional<Member>findMemberById(Long id);
}
