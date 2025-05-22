package com.uci.expertConnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindMatchingExpertsRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Query is required")
    private String text;
}
