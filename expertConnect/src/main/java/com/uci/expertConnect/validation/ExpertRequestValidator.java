package com.uci.expertConnect.validation;

import com.uci.expertConnect.dto.request.CreateExpertProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpertRequestValidator {
    private static final Logger logger = LoggerFactory.getLogger(ExpertRequestValidator.class);

    public void validateCreateExpertRequest(CreateExpertProfileRequest request) {
        logger.info("Validating create expert request");
        
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            logger.error("Email is required");
            throw new IllegalArgumentException("Email is required");
        }
        
        List<String> expertise = request.getExpertise();
        if (expertise == null || expertise.isEmpty()) {
            logger.error("Expertise is required");
            throw new IllegalArgumentException("Expertise is required");
        }
        
        if (request.getHourlyRate() <= 0) {
            logger.error("Hourly rate must be positive");
            throw new IllegalArgumentException("Hourly rate must be positive");
        }
        
        if (request.getBio() == null || request.getBio().isEmpty()) {
            logger.error("Bio is required");
            throw new IllegalArgumentException("Bio is required");
        }
        
        if (request.getAvailability() == null || request.getAvailability().isEmpty()) {
            logger.error("Availability is required");
            throw new IllegalArgumentException("Availability is required");
        }
        
        logger.info("Create expert request validation successful");
    }
} 