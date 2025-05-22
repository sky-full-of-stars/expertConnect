package com.uci.expertConnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CreateExpertProfileRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "Expertise is required")
    @Size(min = 1, message = "At least one expertise is required")
    private List<String> expertise;
    
    @NotNull(message = "Hourly rate is required")
    @Positive(message = "Hourly rate must be positive")
    private Double hourlyRate;
    
    @NotBlank(message = "Bio is required")
    private String bio;
    
    @NotBlank(message = "Availability is required")
    private String availability; // JSON string of time slots
} 