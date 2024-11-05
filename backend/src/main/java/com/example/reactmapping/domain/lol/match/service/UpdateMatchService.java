package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.domain.lol.summonerInfo.entity.SummonerInfo;
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
    static class CircularQueue {
        private Match matches[];
        private int cur, front, size;

        public CircularQueue(List<Match> matchList) {
            this.size = LOL.gameCount;
            matches = new Match[size];
            front = 0;
            cur = 0;

            while (cur < matchList.size()) {
                matches[cur] = matchList.get(cur);
                cur++;
            }
            cur %= size;
        }

        public void setFront(int index) {
            this.front = index;
            this.cur = front;
        }

<<<<<<< HEAD
        public void update(Match newMatch) {
            if (matches[cur] == null)
                matches[cur] =  newMatch;
            matches[cur].updateMatch(newMatch);
            matches[cur].updateSummonerInfo(matches[cur].getSummonerInfo());
=======
        public void update(Match newMatch, SummonerInfo summonerInfo) {
            if (matches[cur] == null)
                matches[cur] =  newMatch;
            matches[cur].updateMatch(newMatch);
            matches[cur].updateSummonerInfo(summonerInfo);
>>>>>>> master
            rotate();
        }

        public List<Match> getMatchList() {
            List<Match> matchList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Match match = matches[(i + front) % size];
                if (match != null) {
                    matchList.add(match);
                }
            }
            return matchList;
        }

        public void rotate() {
            cur = (cur + 1) % size;
        }
    }


    public void updateMatches(SummonerInfo summonerInfo, List<Match> newMatchList) {
        CircularQueue queue = new CircularQueue(summonerInfo.getMatchList());
        queue.setFront(LOL.gameCount - newMatchList.size());
        for(Match newMatch : newMatchList) {
<<<<<<< HEAD
            queue.update(newMatch);
=======
            queue.update(newMatch, summonerInfo);
>>>>>>> master
        }
        summonerInfo.updateMatchList(queue.getMatchList());

    }
//        log.info("getUpdateMatchList");
//        for (Match match : summonerInfo.getMatchList()) {
//            log.info(match.getChampionName());
//        }
//        // 새로운 매치 정보로 기존 리스트 업데이트
//        int updateStartIndex = LOL.gameCount - newMatchCount; // 업데이트 시작할 인덱스
//        for (int j = 0; j < newMatchCount; j++) {
//            int updateIndex = updateStartIndex + j;
//            Match newMatch = newMatchList.get(j);
//
//            // 기존 매치 정보 업데이트 (in-place 업데이트로 리스트 크기 유지)
//            log.info("set updated Match");
//            log.info("new Match id: " + newMatch.getMatchId());
//            // 데이터베이스 업데이트 호출
//            log.info("update Matches");
//            Match match = summonerInfo.getMatchList().get(updateIndex);
//            match.updateMatch(newMatch);
//            match.setSummonerInfo(summonerInfo);
//
//        }
//        summonerInfo.getMatchList().sort(Comparator.comparing(Match::getGameStartTimestamp).reversed());
//    }


}
