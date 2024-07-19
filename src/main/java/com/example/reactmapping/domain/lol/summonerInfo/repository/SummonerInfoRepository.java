package com.example.reactmapping.domain.lol.summonerInfo.repository;

import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SummonerInfoRepository extends JpaRepository<SummonerInfo,String> {
    Optional<SummonerInfo> findBySummonerId(String summonerId);
    Optional<SummonerInfo> findBySummonerNameAndSummonerTag(String summonerName, String summonerTag);

}
