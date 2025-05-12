package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.UserRegistrationRequest;
import com.uci.expertConnect.dto.request.LoginRequest;
import com.uci.expertConnect.dto.response.LoginResponse;
import com.uci.expertConnect.model.User;

public interface UserService {
    User registerUser(UserRegistrationRequest request);
    User findByEmail(String email);
    LoginResponse login(LoginRequest request);
} 