package com.example.reactmapping.domain.member.repository;

import com.example.reactmapping.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member>findMemberByEmailId(String emailId);
    Optional<Member>findMemberById(Long id);
    // SummonerInfo와 MatchList까지 Fetch Join
    @Query("select m from Member m join fetch m.summonerInfo si join fetch si.matchList where m.emailId = :emailId")
    Optional<Member> findWithSummonerInfoAndMatchList(@Param("emailId") String emailId);
}
