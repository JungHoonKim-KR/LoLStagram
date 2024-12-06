package com.example.reactmapping.domain.lol.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MostChampion {
    private Long kills;
    private Long deaths;
    private Long assists;
    private double kda;
    private String championURL;
    private Long win;
    private Long loss;
    private double avgOfWin;
    private Long count;
}
