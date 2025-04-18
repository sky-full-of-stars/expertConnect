package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.UserRegistrationRequest;
import com.uci.expertConnect.model.User;

public interface UserService {
    User registerUser(UserRegistrationRequest request);
    User findByEmail(String email);
} 