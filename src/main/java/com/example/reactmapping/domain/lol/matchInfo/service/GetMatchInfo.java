package com.example.reactmapping.domain.lol.matchInfo.service;
import com.example.reactmapping.domain.lol.util.LoLApiUtil;
import com.example.reactmapping.domain.lol.matchInfo.dto.MatchInfoResultDto;
import com.example.reactmapping.domain.lol.matchInfo.repository.MatchRepository;
import com.example.reactmapping.domain.lol.matchInfo.domain.MatchInfo;
import com.example.reactmapping.domain.lol.matchInfo.dto.MatchInfoDto;
import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GetMatchInfo {
    private final MatchRepository matchRepository;
    private final LoLApiUtil loLApiUtil;
    public void matchSaveAll(List<MatchInfo>matchInfoList){
        matchRepository.saveAll(matchInfoList);
    }
    // 최근 대전기록 가져오기
    public List<String> getMatches(String puuId, int startGame, int count) {
        String Url = String.format("/lol/match/v5/matches/by-puuid/%s/ids?start=%s&count=%s", puuId, startGame, count);
        return loLApiUtil.createWebClient(LOL.BaseUrlAsia, Url).bodyToMono(List.class).block();
    }

    public MatchInfoResultDto getMatchList(Pageable pageable, String type, String summonerId){
        //pageable 조건문 all 예외 처리
        log.info("callType : {}",type);
        Specification<MatchInfo> spec = getMatchInfoSpecification(type, summonerId);

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "gameStartTimestamp"));
        Page<MatchInfo> content = matchRepository.findAll(spec, pageRequest);
        List<MatchInfoDto> matchInfoDtos = MatchInfoDto.entityToDto(content.getContent());

        return new MatchInfoResultDto(matchInfoDtos,content.isLast(),type);
    }

    private Specification<MatchInfo> getMatchInfoSpecification(String type, String summonerId) {
        if ("ALL".equals(type)) {
            // type 값이 "ALL"일 때
           return(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("summonerInfo").get("summonerId"), summonerId);
        } else {
            // type 값이 "ALL"이 아닐 때
            return  Specification
                    .<MatchInfo>where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameType"), type))
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("summonerInfo").get("summonerId"), summonerId));
        }
    }
}
