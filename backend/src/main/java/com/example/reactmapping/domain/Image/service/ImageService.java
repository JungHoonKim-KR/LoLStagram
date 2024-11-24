package com.example.reactmapping.domain.Image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.reactmapping.domain.Image.entity.Image;
import com.example.reactmapping.domain.Image.repository.ImageJDBCRepository;
import com.example.reactmapping.domain.Image.repository.ImageRepository;
import com.example.reactmapping.global.etcConfig.S3Config;

import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Config s3Config;
    private static final String EMPTY_STRING = "";
    private final ImageRepository imageRepository;
    private final ImageJDBCRepository imageJDBCRepository;
    private final AmazonS3Client s3Client;

    public String getImageURL(String type, String name) {
        return imageRepository.findUrlByTypeAndName(type, name).orElse(null);
    }

    public List<String> findAllName(String type) {
        return imageRepository.findAllName(type);
    }

    public List<Image> findAll(String type) {
        return imageRepository.findAllByType(type);
    }

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
                    String path = category + "/" + key + ".png";

                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
                    s3Client.putObject(bucket, path, inputStream, metadata);

                    // 업로드된 S3 URL 추가
                    urlList.add(s3Client.getUrl(bucket, path).toString());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload image: " + key, e);
                }
            }

            return urlList; // 업로드된 URL 반환
        } catch (Exception e) {
            throw new RuntimeException("Error uploading images to S3", e);
        }
    }


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
