package com.uci.expertConnect.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface S3Service {
    String uploadProfilePhoto(Long userId, MultipartFile file) throws IOException;
    void deleteProfilePhoto(String photoUrl) throws IOException;
    String getProfilePhotoUrl(String photoUrl);
} 