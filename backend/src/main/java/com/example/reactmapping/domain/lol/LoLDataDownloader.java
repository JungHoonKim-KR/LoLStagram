package com.example.reactmapping.domain.lol;

import com.example.reactmapping.domain.Image.service.ImageService;
import com.example.reactmapping.global.norm.LOL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Service
@Transactional
public class LoLDataDownloader {
    private static final String DATA_DRAGON_URL = "https://ddragon.leagueoflegends.com/cdn/";
    private static final String CHAMPION = "champion";
    private static final String ITEM = "item";
    private static final String SPELL = "spell";
    private static final String RUNE = "rune";
    private final HttpClient httpClient;
    private final ImageService imageService;

    @Autowired
    public LoLDataDownloader(ImageService imageService) {
        this.imageService = imageService;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Scheduled(fixedRate = 36000)
    public void run() {
        try {
            getVersion();
            downloadChampionImages();
            downloadItems();
            downloadSummonerSpells();
            downloadRunes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 최신 버전 가져오기
     */
    public void getVersion() throws Exception {
        String dataUrl = "https://ddragon.leagueoflegends.com/api/versions.json";
        String response = downloadData(dataUrl);
        String[] versions = response.replace("[", "").replace("]", "").replace("\"", "").split(",");
        if (isNewerVersion(versions[0])) {
            LOL.version = versions[0];
        }
    }

    private boolean isNewerVersion(String latest) {
        String[] currentParts = LOL.version.split("\\.");
        String[] latestParts = latest.split("\\.");

        for (int i = 0; i < Math.min(currentParts.length, latestParts.length); i++) {
            int currentNum = Integer.parseInt(currentParts[i]);
            int latestNum = Integer.parseInt(latestParts[i]);
            if (latestNum > currentNum) return true;
            else if (latestNum < currentNum) return false;
        }
        return latestParts.length > currentParts.length;
    }

    /**
     * 챔피언 이미지 다운로드
     */
    public void downloadChampionImages() throws Exception {
        String dataUrl = DATA_DRAGON_URL + LOL.version + "/data/ko_KR/champion.json";
        JSONObject championsData = new JSONObject(downloadData(dataUrl)).getJSONObject("data");
        List<String> newDataList = new ArrayList<>(championsData.keySet());
        List<String> oldDataList = imageService.findAllName(CHAMPION);

        List<String> newEntries = findNewData(oldDataList, newDataList);
        if (!newEntries.isEmpty()) {
            Map<String, byte[]> imgDataMap = makeImgData(newEntries, entry -> DATA_DRAGON_URL + LOL.version + "/img/" + CHAMPION + "/" + entry + ".png");
            imageService.save(imgDataMap, CHAMPION);
        }
    }

    /**
     * 아이템 이미지 다운로드
     */
    public void downloadItems() throws Exception {
        String dataUrl = DATA_DRAGON_URL + LOL.version + "/data/ko_KR/item.json";
        JSONObject itemsData = new JSONObject(downloadData(dataUrl)).getJSONObject("data");
        List<String> newDataList = new ArrayList<>(itemsData.keySet());
        List<String> oldDataList = imageService.findAllName(ITEM);

        List<String> newEntries = findNewData(oldDataList, newDataList);
        if (!newEntries.isEmpty()) {
            Map<String, byte[]> imgDataMap = makeImgData(newEntries, entry -> DATA_DRAGON_URL + LOL.version + "/img/" + ITEM + "/" + entry + ".png");
            imageService.save(imgDataMap,ITEM);
        }
    }

    /**
     * 소환사 주문 이미지 다운로드
     */
    public void downloadSummonerSpells() throws Exception {
        String dataUrl = DATA_DRAGON_URL + LOL.version + "/data/ko_KR/summoner.json";
        JSONObject spellsData = new JSONObject(downloadData(dataUrl)).getJSONObject("data");
        Map<String, String> spellsMap = new LinkedHashMap<>();
        List<String> newEntries = parseAndGetNewEntries(SPELL, spellsData);

        for (String spellName : spellsData.keySet()) {
            JSONObject spell = spellsData.getJSONObject(spellName);
            spellsMap.put(spell.getString("key"), spellName);
        }

        if (!newEntries.isEmpty()) {
            Map<String, byte[]> imgDataMap = makeImgData(newEntries, entry -> DATA_DRAGON_URL + LOL.version + "/img/" + SPELL + "/" + spellsMap.get(entry) + ".png");
            imageService.save(imgDataMap, SPELL);
        }
    }

    /**
     * 룬 이미지 다운로드
     */
    public void downloadRunes() throws Exception {
        String dataUrl = DATA_DRAGON_URL + LOL.version + "/data/ko_KR/runesReforged.json";
        JSONArray runesData = new JSONArray(downloadData(dataUrl));
        Map<String, JSONObject> runesMap = new LinkedHashMap<>();
        List<String> newDataList = new ArrayList<>();
        List<String> oldDataList = imageService.findAllName(RUNE);

        for (int i = 0; i < runesData.length(); i++) {
            JSONObject runeCategory = runesData.getJSONObject(i);
            processRuneData(runesData.getJSONObject(i),runesMap, newDataList);
            JSONArray slots = runeCategory.getJSONArray("slots");

            for (int j = 0; j < slots.length(); j++) {
                JSONArray runes = slots.getJSONObject(j).getJSONArray("runes");
                for (int k = 0; k < runes.length(); k++) {
                    processRuneData(runes.getJSONObject(k), runesMap, newDataList);
                }
            }
        }
        // parseAndGetNewEntries와 동일
        List<String> newEntries = findNewData(oldDataList, newDataList);
        if (!newEntries.isEmpty()) {
            Map<String, byte[]> imgDataMap = makeImgData(newEntries,
                    entry -> DATA_DRAGON_URL + "img/" + runesMap.get(entry).getString("icon"));
            imageService.save(imgDataMap,RUNE);
        }
    }
    private void processRuneData(JSONObject runeData, Map<String, JSONObject> runeMap, List<String> newDataList){
        String key = String.valueOf(runeData.getInt("id"));
        runeMap.put(key, runeData);
        newDataList.add(key);
    }

    private List<String> parseAndGetNewEntries(String category, JSONObject data) {
        List<String> newDataList;
        if (category.equals(SPELL)) {
            newDataList = new ArrayList<>();
            for (String spellName : data.keySet()) {
                JSONObject spell = data.getJSONObject(spellName);
                newDataList.add(spell.getString("key"));
            }
        } else {
            newDataList = new ArrayList<>(data.keySet());
        }
        List<String> oldDataList = imageService.findAllName(category);
        return findNewData(oldDataList, newDataList);
    }

    private Map<String, byte[]> makeImgData(List<String> newEntries, Function<String, String> imageUrlGenerator) throws Exception {
        // 사용자 정의 스레드 풀 생성
        ExecutorService executor = Executors.newFixedThreadPool(10); // 스레드 수 조정

        Map<String, byte[]> resultMap = new LinkedHashMap<>();

        // CompletableFuture 목록 생성
        List<CompletableFuture<byte[]>> futures = new ArrayList<>();
        for (String entry : newEntries) {
            String imageUrl = imageUrlGenerator.apply(entry);
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    return fetchImgData(imageUrl);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor)); // 사용자 정의 Executor 사용
        }

        // 순서대로 결과 수집
        for (int i = 0; i < newEntries.size(); i++) {
            byte[] imgData = futures.get(i).join(); // 결과 대기
            resultMap.put(newEntries.get(i), imgData); // Map에 삽입
        }

        executor.shutdown(); // Executor 종료
        return resultMap;
    }

    /**
     * 데이터 다운로드 메서드
     */
    private String downloadData(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private List<String> findNewData(List<String> oldData, List<String> newData) {
        // 새로운 데이터에서 기존 데이터에 없는 항목 찾기
        newData.removeAll(oldData); // 차집합 연산
        return newData;
    }

    private byte[] fetchImgData(String imageUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return response.body();
    }
}
