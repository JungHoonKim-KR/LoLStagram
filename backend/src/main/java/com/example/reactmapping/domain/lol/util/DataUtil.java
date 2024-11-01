package com.example.reactmapping.domain.lol.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@RequiredArgsConstructor
@Component
public class DataUtil {

    public static DecimalFormat getDecimalFormat() {
        DecimalFormat df = new DecimalFormat("0.0");
        return df;
    }
    public Long convertRomanToArabic(String roman) {
        switch (roman) {
            case "I":
                return 1L;
            case "II":
                return 2L;
            case "III":
                return 3L;
            case "IV":
                return 4L;
            default:
                throw new IllegalArgumentException("Invalid Roman numeral");
        }
    }
}
