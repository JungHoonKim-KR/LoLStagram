package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UpdateMatchService {
    private final MatchService matchService;

    public void updateMatches(SummonerInfo summonerInfo, int newMatchCount, List<Match> newMatchList) {
        log.info("getUpdateMatchList");
        // 새로운 매치 정보로 기존 리스트 업데이트
        int updateStartIndex = LOL.gameCount - newMatchCount; // 업데이트 시작할 인덱스
        for (int j = 0; j < newMatchCount; j++) {
            int updateIndex = updateStartIndex + j;
            Match newMatch = newMatchList.get(j);

            // 기존 매치 정보 업데이트 (in-place 업데이트로 리스트 크기 유지)
            log.info("set updated Match");
            log.info("new Match id: " + newMatch.getMatchId());
            // 데이터베이스 업데이트 호출
            log.info("update Matches");
            Match match = summonerInfo.getMatchList().get(updateIndex);
            match.updateMatch(newMatch);
            match.setSummonerInfo(summonerInfo);

        }
    }

}
