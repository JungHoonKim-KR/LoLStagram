package com.example.reactmapping.domain.Image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.reactmapping.domain.Image.dto.ImageResourceUrlMaps;
import com.example.reactmapping.domain.Image.entity.Image;
import com.example.reactmapping.domain.Image.repository.ImageJDBCRepository;
import com.example.reactmapping.domain.Image.repository.ImageRepository;
import com.example.reactmapping.domain.lol.match.entity.Match;
import com.example.reactmapping.global.etcConfig.S3Config;

import java.util.concurrent.CompletableFuture;

import com.example.reactmapping.global.norm.LOL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.cloudfront.domain}")
    private String cdnBaseURL;
    private final S3Config s3Config;
    private final ImageRepository imageRepository;
    private final ImageJDBCRepository imageJDBCRepository;
    private final AmazonS3Client s3Client;


    public String getImageURL(String type, String name) {
        return imageRepository.findUrlByTypeAndName(type, name).orElse(null);
    }

    public ImageResourceUrlMaps getImageURLMaps(List<Match>matchList){
        List<String> championKeys = matchList.stream().map(Match::getChampionName).distinct().toList();
        List<String> runeKeys = matchList.stream().flatMap(match -> Stream.of(match.getMainRune(), match.getSubRune())).distinct().toList();
        List<String> itemKeys = matchList.stream().flatMap(match -> match.getItemList().stream()).distinct().toList();
        List<String> spellKeys = matchList.stream().flatMap(match -> match.getSummonerSpellList().stream()).distinct().toList();

        Map<String, String> championURLMap = findUrlsByTypeAndKeys(LOL.ResourceType.CHAMPION.getType(), championKeys);
        Map<String, String> runeURLMap = findUrlsByTypeAndKeys(LOL.ResourceType.RUNE.getType(), runeKeys);
        Map<String, String> itemURLMap = findUrlsByTypeAndKeys(LOL.ResourceType.ITEM.getType(), itemKeys);
        Map<String, String> spellURLMap = findUrlsByTypeAndKeys(LOL.ResourceType.SPELL.getType(), spellKeys);
        return new ImageResourceUrlMaps(championURLMap, runeURLMap, itemURLMap, spellURLMap);
    }

    public List<String> findAllName(String type) {
        return imageRepository.findAllName(type);
    }

    public Map<String, String> findUrlsByTypeAndKeys(String type, List<String>keys){
        List<String[]> urlsByTypeAndKeys = imageRepository.findUrlsByTypeAndKeys(type, keys);
        return urlsByTypeAndKeys.stream().collect(Collectors.toMap(
                obj -> obj[0],
                obj -> obj[1]
        ));
    }
    @Transactional
    public List<String> uploadImageToS3(Map<String, byte[]> imageDataMap, String category) throws Exception {
        // URL 리스트
        List<String> urlList = new ArrayList<>();

        try {
            for (Map.Entry<String, byte[]> entry : imageDataMap.entrySet()) {
                String key = entry.getKey();
                byte[] imageData = entry.getValue();

                try {
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(imageData.length);
                    metadata.setContentType("image/png");
                    metadata.setCacheControl("max-age=86400"); // 1일 동안 캐시
                    String path = category + "/" + key + ".png";

                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
                    s3Client.putObject(bucket, path, inputStream, metadata);

                    String cdnImageURL = String.format("%s/%s", cdnBaseURL, path);
                    urlList.add(cdnImageURL);
                    // 업로드된 S3 URL 추가
//                    urlList.add(s3Client.getUrl(bucket, path).toString());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload image: " + key, e);
                }
            }

            return urlList; // 업로드된 URL 반환
        } catch (Exception e) {
            throw new RuntimeException("Error uploading images to S3", e);
        }
    }

    // 스케쥴링에 의해 실행되는 save 이므로 기존 비니지스 로직과 구분되는 작업 단위 실행을 위함
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Map<String, byte[]> imageDataList, String category) throws Exception {
        List<String> urlList = uploadImageToS3(imageDataList, category);
        List<String> keyList = new ArrayList<>(imageDataList.keySet());
        List<Image> imageList = new ArrayList<>();

        for (int i = 0; i < urlList.size(); i++) {
            imageList.add(new Image(category, keyList.get(i), urlList.get(i)));
        }
        save(imageList);
    }

    public void save(List<Image> imageList) {
        if (imageList.size() > 500) {
            imageJDBCRepository.batchInsert(imageList);
        } else imageRepository.saveAll(imageList);
    }


//    public String createImage(MultipartFile file) throws IOException {
//        if (isFilenameEmpty(file)) {
//            return null;
//        }
//        String fileExtension = extractFileExtension(file.getOriginalFilename());
//        String uuid = generateUUID();
//
//        File localFile = createTemporaryFile(file, uuid, fileExtension);
//        uploadFileToS3(uuid, fileExtension, localFile);
//        return generateFileUrl(uuid, fileExtension);
//    }

//    private boolean isFilenameEmpty(MultipartFile file) {
//        return Objects.equals(file.getOriginalFilename(), EMPTY_STRING);
//    }
//
//    private String extractFileExtension(String filename) {
//        return filename.substring(filename.lastIndexOf("."));
//    }
//
//    private String generateUUID() {
//        return String.valueOf(UUID.randomUUID());
//    }
//
//    private File createTemporaryFile(MultipartFile file, String uuid, String fileExtension) throws IOException {
//        File localFile = File.createTempFile(uuid, fileExtension);
//        try (InputStream inputStream = file.getInputStream()) {
//            FileUtils.copyInputStreamToFile(inputStream, localFile);
//        }
//        return localFile;
//    }
//
//    private void uploadFileToS3(String uuid, String fileExtension, File file) {
//        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuid + fileExtension, file)
//                .withCannedAcl(CannedAccessControlList.PublicRead));
//    }
//
//    private String generateFileUrl(String uuid, String fileExtension) {
//        return s3Config.amazonS3Client().getUrl(bucket, uuid + fileExtension).toString();
//    }


}