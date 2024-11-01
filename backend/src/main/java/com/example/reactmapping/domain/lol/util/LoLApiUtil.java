package com.example.reactmapping.domain.lol.util;

import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.norm.LOL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class LoLApiUtil {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public WebClient.ResponseSpec createWebClient(String baseUrl, String url) {
        WebClient webClient = WebClient.create(baseUrl);
        return webClient.get()
                .uri(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Origin", "https://developer.riotgames.com")
                .header(LOL.RiotTokenHeader,LOL.ApiKey).retrieve();
    }
    public JsonNode getJsonResponse(String baseUrl, String url, String errorMessage) {
        String jsonResponse = createWebClient(baseUrl, url)
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    throw new AppException(ErrorCode.NOTFOUND,"소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.");
                })
                .block();
        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
    }


}
