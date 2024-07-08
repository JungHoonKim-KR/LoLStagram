package com.example.reactmapping.domain.lol.util;

import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.example.reactmapping.global.norm.LOL;
import jakarta.annotation.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class LoLApiUtil {

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

    public @Nullable String getApiResponseOneData(String baseUrl, String url, String id, String message) {
        return createWebClient(baseUrl, url)
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .map(summonerInfo -> summonerInfo.get(id))
                .onErrorResume(e -> {
                    throw new AppException(ErrorCode.NOTFOUND, message);
                })
                .block();
    }
    public @Nullable String getApiResponse(String baseUrl, String url, ErrorCode errorCode, String errorMessage) {
        return createWebClient(baseUrl, url)
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    throw new AppException(errorCode, errorMessage);
                })
                .block();
    }

}
