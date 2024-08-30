package com.example.reactmapping.global.norm;

import java.util.List;

public class URL {
    // permit 카테고리로 분류된 경로들
    public static class Permit {
        public static final String ROOT = "/";
        public static final String LOGIN = "/login/**";
        public static final String JOIN = "/join/**";
        public static final String[] PATHS = {
                "/",
                "/login/**",
                "/join/**",
        };
    }
}
