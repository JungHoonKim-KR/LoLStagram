package com.example.reactmapping.repository;

import com.example.reactmapping.entity.SummonerInfo;
import com.example.reactmapping.object.MostChampion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SummonerInfoRepository extends JpaRepository<SummonerInfo,String> {

    Optional<SummonerInfo> findBySummonerId(String summonerId);
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE SummonerInfo s SET " +
            "s.leagueId = :leagueId, " +
            "s.tier = :tier, " +
            "s.tierRank = :tierRank, " +
            "s.leaguePoints = :leaguePoints, " +
            "s.totalWins = :totalWins, " +
            "s.totalLosses = :totalLosses, " +
            "s.recentWins = :recentWins, " +
            "s.recentLosses = :recentLosses, " +
            "s.mostChampionList = :mostChampionList " +
            "WHERE s.summonerId = :summonerId")
   void updateAll( @Param("summonerId") String summonerId,
            @Param("leagueId") String leagueId,
                   @Param("tier") String tier,
                   @Param("tierRank") Long tierRank,
                   @Param("leaguePoints") Long leaguePoints,
                   @Param("totalWins") Long totalWins,
                   @Param("totalLosses") Long totalLosses,
                   @Param("recentWins") Long recentWins,
                   @Param("recentLosses") Long recentLosses,
                   @Param("mostChampionList") List<MostChampion> mostChampionList
                  );



}
