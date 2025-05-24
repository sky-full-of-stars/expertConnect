package com.uci.expertConnect.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uci.expertConnect.dto.UserRegistrationRequest;
import com.uci.expertConnect.dto.request.LoginRequest;
import com.uci.expertConnect.dto.response.LoginResponse;
import com.uci.expertConnect.dto.response.UserResponse;
import com.uci.expertConnect.model.User;
import com.uci.expertConnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest request) {
        logger.info("Received register User request");
        User user = userService.registerUser(request);
        logger.info("Response successful for register User request");
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/matching-experts")
    public List<Integer> getMatchingExpertsForUser(@PathVariable String userId) {
        try {
            String fastApiUrl = "http://localhost:8003/get-matching-experts/" + userId;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(fastApiUrl, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object expertsObj = response.getBody().get("matchingExperts");

                if (expertsObj instanceof List<?>) {
//                    @SuppressWarnings("unchecked")
                    return (List<Integer>) expertsObj;
                }
            }
        } catch (Exception e) {
            // Optionally log error
        }

        return List.of(); // Return empty list if error or no data
    }


    @PostMapping("/{userId}/profile-photo")
    public ResponseEntity<UserResponse> updateProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received profile photo upload request for user: {}", userId);
            UserResponse userResponse = userService.updateProfilePhoto(userId, file);
            logger.info("Successfully updated profile photo for user: {}", userId);
            return ResponseEntity.ok(userResponse);
        } catch (IOException e) {
            logger.error("Error updating profile photo for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{userId}/profile-photo")
    public ResponseEntity<Void> deleteProfilePhoto(@PathVariable Long userId) {
        try {
            logger.info("Received profile photo deletion request for user: {}", userId);
            userService.deleteProfilePhoto(userId);
            logger.info("Successfully deleted profile photo for user: {}", userId);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error("Error deleting profile photo for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Received login request for email: {}", request.getEmail());
        try {
            LoginResponse response = userService.login(request);
            logger.info("Login successful for user: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for user {}: {}", request.getEmail(), e.getMessage());
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setMessage("Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}