package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UpdateMatchService {
    private final MatchService matchService;

    public void updateMatches(SummonerInfo summonerInfo, int newMatchCount, List<Match> newMatchList) {
        log.info("getOriginMatchList");
        List<Match> originMatchList = summonerInfo.getMatchList(); // 기존 매치 리스트
        log.info("getUpdateMatchList");

        List<Match> updatedMatchList = new ArrayList<>(originMatchList); // 업데이트를 위한 새 리스트 생성

        // 새로운 매치 정보로 기존 리스트 업데이트
        int updateStartIndex = LOL.gameCount - newMatchCount; // 업데이트 시작할 인덱스
        for (int j = 0; j < newMatchCount; j++) {
            int updateIndex = updateStartIndex + j;
            Match newMatch = newMatchList.get(j);

            // 기존 매치 정보 업데이트 (in-place 업데이트로 리스트 크기 유지)
            log.info("set updated Match");
            updatedMatchList.set(updateIndex, newMatch);
            // 데이터베이스 업데이트 호출
            log.info("update Matches");
            matchService.update(newMatch.getMatchId(), newMatch.getGameStartTimestamp(), newMatch.getKills(),
                    newMatch.getDeaths(), newMatch.getAssists(), newMatch.getKda(), newMatch.getChampionName(),
                    newMatch.getMainRune(), newMatch.getSubRune(), newMatch.getItemList(),
                    newMatch.getSummonerSpellList(), newMatch.getResult(),
                    originMatchList.get(updateIndex).getMatchId());
        }

        // 업데이트된 리스트로 소환사 정보 업데이트
        log.info("mapping updatedMatchList");
        summonerInfo.updateMatchList(updatedMatchList);
    }

}
