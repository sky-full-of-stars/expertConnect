package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.UserRegistrationRequest;
import com.uci.expertConnect.model.User;
import com.uci.expertConnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        logger.info("Received register User request");
        User user = userService.registerUser(request);
        logger.info("Response successful for register User request");
        return ResponseEntity.ok(user);
    }
} 
