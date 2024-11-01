package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.domain.lol.match.dto.MatchDto;
import com.example.reactmapping.domain.lol.match.dto.MatchResultDto;
import com.example.reactmapping.domain.lol.util.LoLApiUtil;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.databind.JsonNode;
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
public class GetMatchService {
    private final LoLApiUtil loLApiUtil;
    private final MatchService matchService;

    public JsonNode getMatch(String matchId){
        return loLApiUtil.getJsonResponse(LOL.BaseUrlAsia, "/lol/match/v5/matches/" + matchId, "경기를 찾을 수 없습니다.")
                .path("info");
    }
    // 최근 대전기록 가져오기
    public List<String> getMatchIdList(String puuId, int startGame, int count) {
        String Url = String.format("/lol/match/v5/matches/by-puuid/%s/ids?start=%s&count=%s", puuId, startGame, count);
        log.info("get matchIdList");
        return loLApiUtil.createWebClient(LOL.BaseUrlAsia, Url).bodyToMono(List.class).block();
    }
    public MatchResultDto getMatchList(Pageable pageable, String type, String summonerId){
        //pageable 조건문 all 예외 처리
        log.info("callType : {}",type);
        Specification<Match> spec = getMatchSpecification(type, summonerId);

        log.info(String.valueOf(pageable.getPageNumber()));
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "gameStartTimestamp"));
        Page<Match> content = matchService.findAll(spec, pageRequest);
        List<MatchDto> MatchDtos = MatchDto.entityToDto(content.getContent());

        return new MatchResultDto(MatchDtos,content.isLast(),type);
    }

    private Specification<Match> getMatchSpecification(String type, String summonerId) {
        if ("ALL".equals(type)) {
            // type 값이 "ALL"일 때
           return(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("summonerInfo").get("summonerId"), summonerId);
        } else {
            // type 값이 "ALL"이 아닐 때
            return  Specification
                    .<Match>where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameType"), type))
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("summonerInfo").get("summonerId"), summonerId));
        }
    }
}
