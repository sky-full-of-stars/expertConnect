package com.uci.expertConnect.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpertResponse {
    private Long id;
    private UserResponse user;
    private List<String> expertise;
    private Double hourlyRate;
    private String bio;
    private Map<String, Object> availability;
} 