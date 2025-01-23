package com.example.reactmapping.domain.lol.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class DataUtil {

    public DecimalFormat getDecimalFormat() {
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
    public String calculateKDA(long kill, long death, long assist) {
        DecimalFormat df = getDecimalFormat();
        return (death == 0) ? "perfect" : df.format((double) (kill + assist) / death);
    }
}
