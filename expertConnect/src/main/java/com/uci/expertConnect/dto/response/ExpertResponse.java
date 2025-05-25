package com.uci.expertConnect.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.uci.expertConnect.dto.TimeSlot;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpertResponse {
    private Long id;
    private UserResponse user;
    private String name;
    private String email;
    private List<String> expertise;
    private Double hourlyRate;
    private String bio;
    private String profilePicture;
    private Set<String> skills;
    private Map<String, List<TimeSlot>> availability;
} 