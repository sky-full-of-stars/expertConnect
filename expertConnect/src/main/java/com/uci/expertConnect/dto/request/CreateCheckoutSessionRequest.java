package com.uci.expertConnect.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCheckoutSessionRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    // If not using an existing order
    private Long expertId;
    
    private Long userId;
    
    @Min(value = 1, message = "Amount must be greater than 0")
    private Long amountInCents; // Amount in cents (e.g., $10.00 = 1000)
    
    private String currency = "USD"; // Default currency
    
    private String description;
} 