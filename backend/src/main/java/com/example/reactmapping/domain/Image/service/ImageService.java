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

    public String getImageURL(String type, String id) {
        String filePath = type + "/" + id + ".png";
        return s3Config.amazonS3Client().getUrl(bucket, filePath).toString();
    }

    public List<String> findAllName(String type) {
        return imageRepository.findAllName(type);
    }

    public List<Image> findAll(String type) {
        return imageRepository.findAllByType(type);
    }

    public List<String> uploadImageToS3(Map<String, byte[]> imageDataMap, String category) throws Exception {
        List<String> urlList = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        imageDataMap.forEach((key, imageData) ->
                //비동기 처리
                futures.add(CompletableFuture.runAsync(() -> {
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(imageData.length);
                    metadata.setContentType("image/png");
                    String path = category + "/" + key + ".png";
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
                    s3Client.putObject(bucket, path, inputStream, metadata);
                    urlList.add(s3Client.getUrl(bucket, path).toString());
                })));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return urlList;
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
