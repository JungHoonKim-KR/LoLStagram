package com.example.reactmapping.global.norm;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "origin")
@Getter
public class URL {
    private final String client;
    private final String server;

    public URL(String client, String server) {
        this.client = client;
        this.server = server;
    }


    // permit 카테고리로 분류된 경로들
    public static class Permit {
        public static final String[] PATHS = {
                "/",
                "/login/**",
                "/join/**",
                "/actuator/**",
                "/summoner/enrollalldata"
        };
    }
}
