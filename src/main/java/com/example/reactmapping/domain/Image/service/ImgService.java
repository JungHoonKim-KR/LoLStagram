package com.example.reactmapping.domain.Image.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.reactmapping.global.etcConfig.S3Config;
import com.example.reactmapping.domain.Image.domain.Image;
import com.example.reactmapping.domain.Image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImgService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Config s3Config;
    private final ImageRepository imageRepository;

    public Image createImg(MultipartFile file, String emailId, Long objectId, String objectType) throws IOException {
        if (file.getOriginalFilename() == "")
            return null;
        String fileOriginalName = file.getOriginalFilename();
        String fileExtension = file.getOriginalFilename().substring(fileOriginalName.lastIndexOf("."), fileOriginalName.length());
        String uuid = String.valueOf(UUID.randomUUID());

        InputStream inputStream = file.getInputStream();
        File localFile = File.createTempFile(uuid, fileExtension);

        FileUtils.copyInputStreamToFile(inputStream, localFile);

        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuid + fileExtension, localFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        String fileUrl = s3Config.amazonS3Client().getUrl(bucket, uuid + fileExtension).toString();

        Image image = Image.builder()
                .objectId(objectId)
                .emailId(emailId)
                .filename(uuid)
                .fileOriginName(fileOriginalName)
                .fileUrl(fileUrl)
                .fileExtension(fileExtension)
                .objectType(objectType)
                .build();
        return image;
    }

    public Image updateImg(Image findImg, String emailId, Long objectId, MultipartFile newImg) throws IOException {
        Image img = createImg(newImg, emailId, objectId, findImg.getObjectType());
        imageRepository.save(img);
        return img;
    }
}
