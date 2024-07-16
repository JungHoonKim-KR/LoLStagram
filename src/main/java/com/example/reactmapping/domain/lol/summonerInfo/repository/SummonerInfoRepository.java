package com.example.reactmapping.domain.lol.summonerInfo.repository;

import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.domain.lol.dto.MostChampion;
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

}
