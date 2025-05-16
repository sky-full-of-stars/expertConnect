package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.CreateCheckoutSessionRequest;
import com.uci.expertConnect.dto.response.CheckoutSessionResponse;
import com.uci.expertConnect.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    private final PaymentService paymentService;
    
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutSessionResponse> createCheckoutSession(
            @Valid @RequestBody CreateCheckoutSessionRequest request) {
        logger.info("Received request to create checkout session for order ID: {}", request.getOrderId());
        
        CheckoutSessionResponse response = paymentService.createCheckoutSession(request);
        
        if ("error".equals(response.getStatus())) {
            logger.error("Error creating checkout session: {}", response.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
        logger.info("Checkout session created successfully with session ID: {}", response.getSessionId());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        logger.info("Received Stripe webhook");
        
        try {
            String result = paymentService.handleWebhookEvent(payload, sigHeader);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error handling webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }
    }
} 