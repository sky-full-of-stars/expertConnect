package com.uci.expertConnect.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Bean
    public String stripeApiKey() {
        return stripeApiKey;
    }

    @Bean
    public String webhookSecret() {
        return webhookSecret;
    }

    @Bean
    public String successUrl() {
        return successUrl;
    }

    @Bean
    public String cancelUrl() {
        return cancelUrl;
    }
} 