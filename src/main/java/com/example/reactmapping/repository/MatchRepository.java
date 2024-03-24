package com.example.reactmapping.repository;

import com.example.reactmapping.entity.MatchInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<MatchInfo,String> {
    @Query("select m from MatchInfo m where m.summonerInfo.summonerId =:summonerId order by m.gameStartTimestamp DESC ")
    List<MatchInfo> findAllBySummonerId(@Param("summonerId")String summonerId);
    Page<MatchInfo> findAll(Specification<MatchInfo> spec, Pageable pageable);


    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("update MatchInfo s SET " +
            "s.matchId = :matchId, " +
            "s.gameStartTimestamp = :gameStartTimestamp, " +
            "s.kills = :kills, " +
            "s.deaths = :deaths, " +
            "s.assists = :assists, " +
            "s.kda = :kda, " +
            "s.championName = :championName, " +
            "s.mainRune = :mainRune, " +
            "s.subRune = :subRune, " +
            "s.itemList = :itemList, " +
            "s.summonerSpellList = :summonerSpellList, " +
            "s.result = :result " +
            "WHERE s.matchId = :originMatchId")
    void updateAll(@Param("matchId") String matchId,
                            @Param("gameStartTimestamp") Long gameStartTimestamp,
                            @Param("kills") Long kills,
                            @Param("deaths") Long deaths,
                            @Param("assists") Long assists,
                            @Param("kda") String kda,
                            @Param("championName") String championName,
                            @Param("mainRune") Long mainRune,
                            @Param("subRune") Long subRune,
                            @Param("itemList") List<Integer> itemList,
                            @Param("summonerSpellList") List<Integer> summonerSpellList,
                            @Param("result") String result,
                            @Param("originMatchId") String originMatchId);

}
