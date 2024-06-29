package com.example.reactmapping.norm;

import lombok.Getter;

@Getter
public enum LOL {
    INFO(20,"14.11.1");
    private final int gameCount;
    private final String version;

    LOL(int gameCount, String version) {
        this.gameCount = gameCount;
        this.version = version;
    }
}
