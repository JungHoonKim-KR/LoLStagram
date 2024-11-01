package com.example.reactmapping.domain.lol.dto;

import com.example.reactmapping.domain.lol.match.entity.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class CompareMatchDto {
    private int result;
    private List<Match> MatchList=new ArrayList<>();
}
