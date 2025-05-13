package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.UserRegistrationRequest;
import com.uci.expertConnect.model.User;
import com.uci.expertConnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request);
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

} 