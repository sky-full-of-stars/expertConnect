package com.uci.expertConnect.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String message;
} 