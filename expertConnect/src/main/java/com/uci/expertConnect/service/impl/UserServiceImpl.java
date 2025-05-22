package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.dto.UserRegistrationRequest;
import com.uci.expertConnect.dto.request.LoginRequest;
import com.uci.expertConnect.dto.response.LoginResponse;
import com.uci.expertConnect.dto.response.UserResponse;
import com.uci.expertConnect.exception.DuplicateEmailException;
import com.uci.expertConnect.exception.UnauthorizedException;
import com.uci.expertConnect.model.User;
import com.uci.expertConnect.repository.UserRepository;
import com.uci.expertConnect.service.S3Service;
import com.uci.expertConnect.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, S3Service s3Service) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.valueOf(request.getRole().name()));

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.debug("Attempting login for email: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login attempt failed - User not found with email: {}", request.getEmail());
                    return new UnauthorizedException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login attempt failed - Invalid password for user: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setMessage("Login successful");
        
        logger.info("Login successful for user: {}", user.getEmail());
        return response;
    }

    @Transactional
    public UserResponse updateProfilePhoto(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Delete old photo if exists
        if (user.getProfilePhotoUrl() != null) {
            try {
                s3Service.deleteProfilePhoto(user.getProfilePhotoUrl());
            } catch (IOException e) {
                logger.warn("Failed to delete old profile photo for user {}: {}", userId, e.getMessage());
                // Continue even if deletion fails, to allow new photo upload
            }
        }

        // Upload new photo
        String photoUrl = s3Service.uploadProfilePhoto(userId, file);
        logger.info("User [ID: {}] current name before setting photo URL: {}", userId, user.getName());
        user.setProfilePhotoUrl(photoUrl);
        logger.info("User [ID: {}] profilePhotoUrl set to: {}. Name is now: {}", userId, user.getProfilePhotoUrl(), user.getName());

        User savedUser;
        try {
            logger.info("Attempting to save user [ID: {}] with photo URL: {}", userId, user.getProfilePhotoUrl());
            savedUser = userRepository.save(user);
            userRepository.flush();
            logger.info("Successfully saved and flushed user [ID: {}]. Profile photo URL from returned entity: {}", userId, savedUser.getProfilePhotoUrl());
            if (savedUser.getName() == null) {
                logger.warn("User [ID: {}] name is NULL after save!", userId);
            }
        } catch (Exception e) {
            logger.error("ERROR saving/flushing user [ID: {}] after setting photo URL: {}", userId, e.getMessage(), e);
            throw e;
        }
        
        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteProfilePhoto(Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getProfilePhotoUrl() != null) {
            s3Service.deleteProfilePhoto(user.getProfilePhotoUrl());
            user.setProfilePhotoUrl(null);
            userRepository.save(user);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().name());
        userResponse.setProfilePhotoUrl(user.getProfilePhotoUrl());
        return userResponse;
    }
} 