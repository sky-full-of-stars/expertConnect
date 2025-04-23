package com.uci.expertConnect.dto.request;

import com.uci.expertConnect.validation.ValidJsonString;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateExpertProfileRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Expertise is required")
    private List<String> expertise;

    @Positive(message = "Hourly rate must be positive")
    private Double hourlyRate;

    @NotBlank(message = "Bio is required")
    private String bio;

    @NotNull(message = "Availability is required")
    @ValidJsonString(message = "Invalid JSON format for availability")
    private String availability;
} 