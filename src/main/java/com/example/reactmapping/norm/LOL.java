package com.example.reactmapping.norm;

public enum LOL {
    INFO(20,"14.11.1");
    private final int gameCount;
    private final String version;

    LOL(int gameCount, String version) {
        this.gameCount = gameCount;
        this.version = version;
    }
    public int getGameCount(){
        return gameCount;
    }
    public String getVersion(){return version;}
}
