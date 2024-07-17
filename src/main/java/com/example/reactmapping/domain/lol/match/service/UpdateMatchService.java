package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.domain.lol.summonerInfo.domain.SummonerInfo;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMatchService {
    private final MatchService matchService;
// batch update
//    @Transactional
//    public void updateMatchesInBatch(List<Match> matches) {
//        String sql = "UPDATE Match SET gameStartTimestamp = ?, kills = ?, deaths = ?, assists = ?, kda = ?, " +
//                "championName = ?, mainRune = ?, subRune = ?, itemList = ?, summonerSpellList = ?, " +
//                "result = ? WHERE matchId = ?";
//
//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                Match match = matches.get(i);
//                ps.setLong(1, match.getGameStartTimestamp());
//                ps.setLong(2, match.getKills());
//                ps.setLong(3, match.getDeaths());
//                ps.setLong(4, match.getAssists());
//                ps.setString(5, match.getKda());
//                ps.setString(6, match.getChampionName());
//                ps.setLong(7, match.getMainRune());
//                ps.setLong(8, match.getSubRune());
//                ps.setObject(9, match.getItemList());
//                ps.setObject(10, match.getSummonerSpellList());
//                ps.setString(11, match.getResult());
//                ps.setString(12, match.getMatchId());
//            }
//
//            public int getBatchSize() {
//                return matches.size();
//            }
//        });
//    }

    public void updateMatches(SummonerInfo summonerInfo, int newMatchCount, List<Match> newMatchList) {
        List<Match> originMatchList = summonerInfo.getMatchList(); // 기존 매치 리스트
        List<Match> updatedMatchList = new ArrayList<>(originMatchList); // 업데이트를 위한 새 리스트 생성

        // 새로운 매치 정보로 기존 리스트 업데이트
        int updateStartIndex = LOL.gameCount - newMatchCount; // 업데이트 시작할 인덱스
        for (int j = 0; j < newMatchCount; j++) {
            int updateIndex = updateStartIndex + j;
            Match newMatch = newMatchList.get(j);

            // 기존 매치 정보 업데이트 (in-place 업데이트로 리스트 크기 유지)
            updatedMatchList.set(updateIndex, newMatch);

            // 데이터베이스 업데이트 호출
            matchService.updateAll(newMatch.getMatchId(), newMatch.getGameStartTimestamp(), newMatch.getKills(),
                    newMatch.getDeaths(), newMatch.getAssists(), newMatch.getKda(), newMatch.getChampionName(),
                    newMatch.getMainRune(), newMatch.getSubRune(), newMatch.getItemList(),
                    newMatch.getSummonerSpellList(), newMatch.getResult(),
                    originMatchList.get(updateIndex).getMatchId());
        }

        // 업데이트된 리스트로 소환사 정보 업데이트
        summonerInfo.updateMatchList(updatedMatchList);
    }

}
