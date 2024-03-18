package com.example.reactmapping.norm;

public enum LOL {
    INFO(20);
    private final int gameCount;


    LOL(int gameCount) {
        this.gameCount = gameCount;
    }
    public int getGameCount(){
        return gameCount;
    }
}
