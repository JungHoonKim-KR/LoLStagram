package com.example.reactmapping.domain.lol.matchInfo.service;

import com.example.reactmapping.domain.lol.dto.CompareDto;
import com.example.reactmapping.domain.lol.matchInfo.domain.MatchInfo;
import com.example.reactmapping.domain.lol.matchInfo.repository.MatchRepository;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompareMatchService {
    // 최신 경기 리스트 10개 중 마지막 경기를 가져옴
    // 이 마지막 경기가 현재 저장된 리스트의 몇번 인덱스에 포함되는지
    // ex) 4번이면 0~3의 경기 즉 4개의 경기가 갱신이 안됨.
    private final GetMatchInfo matchService;
    private final MatchRepository matchRepository;
    public CompareDto compare(String puuId, String summonerId) {
        int result = -1;
        String targetMatchId = matchService.getMatches(puuId, LOL.gameCount - 1, 1).get(0);
        List<MatchInfo> matchInfo = matchRepository.findAllBySummonerId(summonerId);
        if (!matchInfo.isEmpty()) {
            result = IntStream.range(0, matchInfo.size())
                    .filter(i -> targetMatchId.equals(matchInfo.get(i).getMatchId()))
                    .findFirst()
                    .orElse(0);
            // 업데이트는 개수로 처리하기 때문에 0이 아니라면 인덱스 +1을 해줘야함
            if (result != 0) result++;
        }
        return new CompareDto(result, matchInfo);
    }
}
