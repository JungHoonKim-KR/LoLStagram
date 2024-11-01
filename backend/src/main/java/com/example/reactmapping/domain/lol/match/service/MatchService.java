package com.example.reactmapping.domain.lol.match.service;

import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.domain.lol.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {
    private final MatchRepository matchRepository;

    public Page<Match> findAll(Specification<Match> spec, Pageable pageable) {
        return matchRepository.findAll(spec, pageable);
    }
    public List<Match> findAllBySummonerId(String summonerId) {
           return matchRepository.findAllBySummonerId(summonerId);
    }
}
