package com.example.reactmapping.global.norm;

import lombok.Getter;

public class LOL {

    public static final String version = "14.11.1";
    public static final int gameCount = 20;
    public static final String RiotIdGameName = "riotIdGameName";
    public static final String RiotIdTagline = "riotIdTagline";
    public static final String RiotTokenHeader = "X-Riot-Token";
    public static final String ApiKey = "RGAPI-8ffbfd5a-0b81-41f7-8083-6d26ad910aea";
    public static final String BaseUrlAsia = "https://asia.api.riotgames.com";
    public static final String BaseUrlKR = "https://kr.api.riotgames.com";
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