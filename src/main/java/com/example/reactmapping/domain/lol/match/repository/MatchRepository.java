package com.example.reactmapping.domain.lol.match.repository;

import com.example.reactmapping.domain.lol.match.entity.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match,String> {
    @Query("select m from Match m where m.summonerInfo.summonerId =:summonerId order by m.gameStartTimestamp DESC ")
    List<Match> findAllBySummonerId(@Param("summonerId")String summonerId);
    Page<Match> findAll(Specification<Match> spec, Pageable pageable);


}
