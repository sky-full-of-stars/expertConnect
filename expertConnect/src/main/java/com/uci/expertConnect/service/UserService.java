package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.UserRegistrationRequest;
import com.uci.expertConnect.dto.request.LoginRequest;
import com.uci.expertConnect.dto.response.LoginResponse;
import com.uci.expertConnect.dto.response.UserResponse;
import com.uci.expertConnect.model.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface UserService {
    User registerUser(UserRegistrationRequest request);
    User findByEmail(String email);
    LoginResponse login(LoginRequest request);
    UserResponse updateProfilePhoto(Long userId, MultipartFile file) throws IOException;
    void deleteProfilePhoto(Long userId) throws IOException;
} 