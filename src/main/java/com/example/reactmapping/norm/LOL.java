package com.example.reactmapping.norm;

import lombok.Getter;

public class LOL {
    public static final String version = "14.11.1";
    public static final int gameCount = 20;
    @Getter
    public enum GameType{
        솔랭("솔랭"),
        빠른대전("빠른 대전"),
        자유랭크("자유 랭크"),
        URF("URF"),
        무작위총력전("무작위 총력전"),
        아레나("아레나");

        private String type;

        GameType(String type){
            this.type = type;
        }

    }


}