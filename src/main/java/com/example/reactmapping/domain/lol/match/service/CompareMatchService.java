package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.domain.Match;
import com.example.reactmapping.global.norm.LOL;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompareMatchService {
    // 최신 경기 리스트 10개 중 마지막 경기를 가져옴
    // 이 마지막 경기가 현재 저장된 리스트의 몇번 인덱스에 포함되는지
    // ex) 4번이면 0~3의 경기 즉 4개의 경기가 갱신이 안됨.
    private final GetMatchService getMatchService;
    private final MatchService matchService;

    public int getCountNewMatch(String puuId, String summonerId) {
        log.info("gameCount start");
        String targetMatchId = getMatchService.getMatchIdList(puuId, LOL.LastIndex, 1).get(0);
        log.info("find match");
        List<Match> matchList = matchService.findAllBySummonerId(summonerId);
        log.info("cal gameCount");
        Integer gameCount = getGameCount(matchList, targetMatchId);
        return Objects.requireNonNullElse(gameCount, LOL.gameCount);
    }
    private static @Nullable Integer getGameCount(List<Match> matchList, String targetMatchId) {
        int result;
        if (!matchList.isEmpty()) {
            result = IntStream.range(0, matchList.size())
                    .filter(i -> targetMatchId.equals(matchList.get(i).getMatchId()))
                    .findFirst()
                    .orElse(-1);  // 찾지 못한 경우 -1 반환
            if (result != -1) {
                log.info(String.valueOf(LOL.gameCount - (result + 1)));
                return LOL.gameCount - (result + 1);
            }
        }
        return null;
    }
}
