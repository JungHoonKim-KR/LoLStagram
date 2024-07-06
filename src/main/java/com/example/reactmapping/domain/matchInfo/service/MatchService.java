package com.example.reactmapping.domain.matchInfo.service;
import com.example.reactmapping.domain.matchInfo.domain.MatchInfo;
import com.example.reactmapping.domain.matchInfo.repository.MatchRepository;
import com.example.reactmapping.domain.matchInfo.dto.MatchInfoDto;
import com.example.reactmapping.domain.matchInfo.dto.MatchInfoResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;

    public void matchSaveAll(List<MatchInfo>matchInfoList){
        matchRepository.saveAll(matchInfoList);
    }

    public MatchInfoResultDto getMatchList(Pageable pageable, String type,String summonerId){
        //pageable 조건문 all 예외 처리
        Specification<MatchInfo> spec;

        if ("ALL".equals(type)) {
            // type 값이 "ALL"일 때
            spec = Specification
                    .<MatchInfo>where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("summonerInfo").get("summonerId"), summonerId));
        } else {
            // type 값이 "ALL"이 아닐 때
            spec = Specification
                    .<MatchInfo>where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameType"), type))
                    .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("summonerInfo").get("summonerId"), summonerId));
        }

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "gameStartTimestamp"));
        Page<MatchInfo> content = matchRepository.findAll(spec, pageRequest);
        List<MatchInfoDto> matchInfoDtos = MatchInfoDto.entityToDto(content.getContent());

        return new MatchInfoResultDto(matchInfoDtos,content.isLast(),type);
    }
}
