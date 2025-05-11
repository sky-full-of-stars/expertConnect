package com.uci.expertConnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindMatchingExpertsRequest {

    @NotBlank(message = "Query is required")
    private String text;
}
