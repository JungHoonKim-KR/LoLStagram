package com.example.reactmapping.object;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MostChampion {
    private Long kills;
    private Long deaths;
    private Long assists;
    private double kda;
    private String championName;
    private Long win;
    private Long loss;
    private double avgOfWin;
    private Long count;
}
