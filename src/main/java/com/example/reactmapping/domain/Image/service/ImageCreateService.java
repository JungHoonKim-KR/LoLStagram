package com.example.reactmapping.domain.Image.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.reactmapping.global.etcConfig.S3Config;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageCreateService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Config s3Config;
    private static final String EMPTY_STRING = "";

    public String createImg(MultipartFile file) throws IOException {
        if (isFilenameEmpty(file)) {
            return null;
        }
        String fileExtension = extractFileExtension(file.getOriginalFilename());
        String uuid = generateUUID();

        File localFile = createTemporaryFile(file, uuid, fileExtension);
        uploadFileToS3(uuid, fileExtension, localFile);

        return generateFileUrl(uuid, fileExtension);
    }

    private boolean isFilenameEmpty(MultipartFile file) {
        return Objects.equals(file.getOriginalFilename(), EMPTY_STRING);
    }

    private String extractFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private String generateUUID() {
        return String.valueOf(UUID.randomUUID());
    }

    private File createTemporaryFile(MultipartFile file, String uuid, String fileExtension) throws IOException {
        File localFile = File.createTempFile(uuid, fileExtension);
        try (InputStream inputStream = file.getInputStream()) {
            FileUtils.copyInputStreamToFile(inputStream, localFile);
        }
        return localFile;
    }

    private void uploadFileToS3(String uuid, String fileExtension, File file) {
        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuid + fileExtension, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));

    }

    private String generateFileUrl(String uuid, String fileExtension) {
        return s3Config.amazonS3Client().getUrl(bucket, uuid + fileExtension).toString();
    }

}
