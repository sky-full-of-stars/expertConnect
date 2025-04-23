package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.CreateExpertProfileRequest;
import com.uci.expertConnect.dto.response.ExpertResponse;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.service.ExpertProfileService;
import com.uci.expertConnect.validation.ExpertRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/expert")
public class ExpertController {
    private static final Logger logger = LoggerFactory.getLogger(ExpertController.class);
    private final ExpertProfileService expertProfileService;
    private final ExpertRequestValidator expertRequestValidator;

    @Autowired
    public ExpertController(ExpertProfileService expertProfileService, ExpertRequestValidator expertRequestValidator) {
        this.expertProfileService = expertProfileService;
        this.expertRequestValidator = expertRequestValidator;
    }

    @PostMapping
    public ResponseEntity<Expert> createExpert(@RequestBody CreateExpertProfileRequest request) {
        logger.info("Received create expert request");
        logger.debug("Request details - email: {}, expertise: {}, hourlyRate: {}, bio: {}, availability: {}", 
            request.getEmail(), request.getExpertise(), request.getHourlyRate(), request.getBio(), request.getAvailability());
        
        try {
            expertRequestValidator.validateCreateExpertRequest(request);
            logger.info("Calling service layer to create expert profile");
            Expert savedExpert = expertProfileService.createExpertProfile(request);
            logger.info("Successfully created expert with ID: {}", savedExpert.getId());
            return ResponseEntity.ok(savedExpert);
        } catch (Exception e) {
            logger.error("Error creating expert profile: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{expertId}")
    public ResponseEntity<ExpertResponse> getExpertById(@PathVariable Long expertId) {
        logger.info("Received request to get expert with ID: {}", expertId);
        Expert expert = expertProfileService.getExpertById(expertId);
        ExpertResponse response = expertProfileService.mapExpertToResponse(expert);
        logger.info("Successfully retrieved expert with ID: {}", expertId);
        return ResponseEntity.ok(response);
    }

    //still have not tested the endpoints
    @GetMapping("/search")
    public ResponseEntity<Page<Expert>> findExpertsByExpertise(
            @RequestParam List<String> expertise,
            Pageable pageable) {
        logger.info("Received search request for expertise: {}", expertise);
        Page<Expert> experts = expertProfileService.findExpertsByExpertise(expertise, pageable);
        logger.info("Found {} experts matching the search criteria", experts.getTotalElements());
        return ResponseEntity.ok(experts);
    }
} 