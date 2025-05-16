package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.request.CreateCheckoutSessionRequest;
import com.uci.expertConnect.dto.response.CheckoutSessionResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    /**
     * Creates a Stripe Checkout Session for payment
     * 
     * @param request The checkout session request
     * @return CheckoutSessionResponse containing the session ID and URL
     */
    CheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request);
    
    /**
     * Handles Stripe webhook events
     * 
     * @param payload The webhook payload
     * @param sigHeader The Stripe signature header
     * @return String acknowledgment message
     */
    String handleWebhookEvent(String payload, String sigHeader);
} 