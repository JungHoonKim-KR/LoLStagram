package com.example.reactmapping.domain.Image.dto;

import java.util.Map;


public record ImageResourceUrlMaps(
        Map<String, String> championURLMap,
        Map<String, String> runeURLMap,
        Map<String, String> itemURLMap,
        Map<String, String> spellURLMap
) {}