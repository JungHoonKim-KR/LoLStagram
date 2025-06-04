package com.example.reactmapping.domain.lol.util;

import com.example.reactmapping.global.exception.AppException;
import com.example.reactmapping.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoLApiUtil {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebClient.RequestHeadersSpec<?> createWebClient(String baseUrl, String url) {
        WebClient webClient = WebClient.create(baseUrl);
        return webClient.get()
                .uri(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Origin", "https://developer.riotgames.com")
                .header("X-Riot-Token", "RGAPI-8ffbfd5a-0b81-41f7-8083-6d26ad910aea");
    }

    public JsonNode getJsonResponse(String baseUrl, String url, String errorMessage) {
        try {
            String body = createWebClient(baseUrl, url)
                    .retrieve()
                    // ① statusCode 파라미터는 HttpStatusCode이므로, 바로 isError() 또는 수치 비교
                    .onStatus(
                            status -> status.is4xxClientError(),
                            clientResponse -> clientResponse
                                    .bodyToMono(String.class)
                                    .flatMap(bodyText -> {
                                        // ② 여기서는 ClientResponse 참조 가능
                                        log.warn("API 오류: status={}, body={}",
                                                clientResponse.statusCode(), bodyText);
                                        return Mono.error(new AppException(
                                                ErrorCode.NOTFOUND,
                                                errorMessage + " [HTTP " + clientResponse.statusCode() + "]"
                                        ));
                                    })
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            clientResponse -> clientResponse
                                    .bodyToMono(String.class)
                                    .flatMap(bodyText -> {
                                        // ② 여기서는 ClientResponse 참조 가능
                                        log.warn("API 오류: status={}, body={}",
                                                clientResponse.statusCode(), bodyText);
                                        return Mono.error(new AppException(
                                                ErrorCode.NOTFOUND,
                                                "외부 API에서 에러가 발생했습니다." + " [HTTP " + clientResponse.statusCode() + "]"
                                        ));
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();
            return objectMapper.readTree(body);
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 또는 통신 실패", e);
        }
    }

//        public JsonNode getJsonResponse(String baseUrl, String url, String errorMessage) {
//        return WebClient.create(baseUrl)
//                .get()
//                .uri(url)
//                .header("User-Agent", "...")
//                .header("X-Riot-Token", "...")
//                .exchangeToMono(response -> {
//                    HttpStatusCode status = response.statusCode();
//
//                    return response.bodyToMono(String.class).map(body -> {
//                        log.warn("matchId: {}, statusCode: {}, response: {}",
//                                extractMatchIdFromUrl(url), status.value(), body);
//
//                        if (status.isError()) {
//                            throw new AppException(ErrorCode.NOTFOUND, errorMessage + " [HTTP " + status + "]");
//                        }
//
//                        try {
//                            return objectMapper.readTree(body);
//                        } catch (Exception e) {
//                            throw new RuntimeException("JSON 파싱 오류", e);
//                        }
//                    });
//                })
//                .block();
//    }
//    private String extractMatchIdFromUrl(String url) {
//        // "/lol/match/v5/matches/KR_1234" → "KR_1234"
//        String[] parts = url.split("/");
//        return parts.length > 0 ? parts[parts.length - 1] : "unknown";
//    }


}
