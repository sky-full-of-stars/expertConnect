package com.uci.expertConnect.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.uci.expertConnect.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {
    private static final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadProfilePhoto(Long userId, MultipartFile file) throws IOException {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String key = String.format("profile-photos/%d/%s%s", userId, UUID.randomUUID(), extension);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(
            bucketName,
            key,
            file.getInputStream(),
            metadata
        );

        s3Client.putObject(putObjectRequest);
        logger.info("Successfully uploaded profile photo for user {} to S3", userId);

        return s3Client.getUrl(bucketName, key).toString();
    }

    @Override
    public void deleteProfilePhoto(String photoUrl) throws IOException {
        if (photoUrl == null || !photoUrl.contains(bucketName)) {
            return;
        }

        String key = photoUrl.substring(photoUrl.indexOf(bucketName) + bucketName.length() + 1);
        try {
            s3Client.deleteObject(bucketName, key);
            logger.info("Successfully deleted profile photo from S3: {}", key);
        } catch (AmazonS3Exception e) {
            logger.error("Error deleting profile photo from S3: {}", e.getMessage());
            throw new IOException("Failed to delete profile photo", e);
        }
    }

    @Override
    public String getProfilePhotoUrl(String photoUrl) {
        return photoUrl != null ? photoUrl : null;
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum limit of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IOException("Invalid file type. Only JPEG, PNG, and GIF images are allowed");
        }
    }
} 