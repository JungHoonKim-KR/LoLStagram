package com.example.reactmapping.service.LoL;

import com.example.reactmapping.exception.AppException;
import com.example.reactmapping.exception.ErrorCode;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoLApi {
    private final String RiotTokenHeader = "X-Riot-Token";
    private final String ApiKey = "RGAPI-8ffbfd5a-0b81-41f7-8083-6d26ad910aea";
    private final String BaseUrlAsia = "https://asia.api.riotgames.com";
    private final String BaseUrlKR = "https://kr.api.riotgames.com";
    private String Url;
    private String RiotIdGameName = "riotIdGameName";
    private String RiotIdTagline = "riotIdTagline";
    // 소환사를 등록할 때 puuId를 가져와 DB에 저장
    public String callPuuId(String riotIdGameName, String riotIdTagline) {
        log.info("요청 닉네임: " + riotIdGameName + " 요청 태그: " + riotIdTagline);
        return getApiResponseOneData(BaseUrlAsia, "/riot/account/v1/accounts/by-riot-id/" + riotIdGameName + "/" + riotIdTagline, "puuid", "라이엇 이름 또는 태그가 일치하지 않습니다.");
    }

    public String callSummonerId(String puuId) {
        return getApiResponseOneData(BaseUrlKR, "/lol/summoner/v4/summoners/by-puuid/" + puuId, "id", "소환사 아이디를 찾을 수 없습니다. 라이엇 이름 또는 태그가 일치하지 않습니다.");
    }

    // 최근 대전기록 가져오기
    public List<String> callMatches(String puuId, int startGame, int count) {
        Url = String.format("/lol/match/v5/matches/by-puuid/%s/ids?start=%s&count=%s", puuId, startGame, count);
        return createWebClient(BaseUrlAsia, Url).bodyToMono(List.class).block();
    }

    private static DecimalFormat getDecimalFormat() {
        DecimalFormat df = new DecimalFormat("0.0");
        return df;
    }

    private WebClient.ResponseSpec createWebClient(String baseUrl, String url) {
        WebClient webClient = WebClient.create(baseUrl);
        return webClient.get()
                .uri(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Origin", "https://developer.riotgames.com")
                .header(RiotTokenHeader, ApiKey).retrieve();
    }
    private @Nullable String getApiResponse(String baseUrl, String url, ErrorCode errorCode, String errorMessage) {
        return createWebClient(baseUrl, url)
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    throw new AppException(errorCode, errorMessage);
                })
                .block();
    }

    private @Nullable String getApiResponseOneData(String baseUrl, String url, String id, String message) {
        return createWebClient(baseUrl, url)
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .map(summonerInfo -> summonerInfo.get(id))
                .onErrorResume(e -> {
                    throw new AppException(ErrorCode.NOTFOUND, message);
                })
                .block();
    }
}
