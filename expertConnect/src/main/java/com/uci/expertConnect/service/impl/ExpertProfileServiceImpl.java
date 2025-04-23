package com.uci.expertConnect.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uci.expertConnect.dto.request.CreateExpertProfileRequest;
import com.uci.expertConnect.dto.response.ExpertResponse;
import com.uci.expertConnect.dto.response.UserResponse;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.model.User;
import com.uci.expertConnect.exception.UnauthorizedException;
import com.uci.expertConnect.exception.NotFoundException;
import com.uci.expertConnect.repository.ExpertRepository;
import com.uci.expertConnect.repository.UserRepository;
import com.uci.expertConnect.service.ExpertProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ExpertProfileServiceImpl implements ExpertProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ExpertProfileServiceImpl.class);

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public Expert createExpertProfile(CreateExpertProfileRequest request) {
        logger.info("Starting expert profile creation for email: {}", request.getEmail());
        
        // Check if user exists and is authorized
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", request.getEmail());
                    return new EntityNotFoundException("User not found");
                });

        // Check if expert profile already exists
        if (expertRepository.existsByUserEmail(request.getEmail())) {
            logger.warn("Expert profile already exists for this user");
            throw new UnauthorizedException("Expert profile already exists for this user");
        }

        try {
            // Parse the JSON string to a Map
            Map<String, Object> availabilityMap = objectMapper.readValue(request.getAvailability(), Map.class);

            Expert expert = new Expert();
            expert.setUser(user);
            expert.setExpertise(request.getExpertise());
            expert.setHourlyRate(request.getHourlyRate());
            expert.setBio(request.getBio());
            expert.setAvailability(availabilityMap);

            Expert savedExpert = expertRepository.save(expert);
            logger.info("Successfully saved expert profile with ID: {}", savedExpert.getId());
            return savedExpert;
        } catch (JsonProcessingException e) {
            logger.error("Invalid JSON format for availability: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JSON format for availability", e);
        }
    }

    @Override
    public Expert getExpertById(Long expertId) {
        logger.info("Looking up expert with ID: {}", expertId);
        return expertRepository.findById(expertId)
                .orElseThrow(() -> {
                    logger.error("Expert not found with ID: {}", expertId);
                    return new NotFoundException("Expert not found with ID: " + expertId);
                });
    }

    @Override
    public Page<Expert> findExpertsByExpertise(List<String> expertiseList, Pageable pageable) {
        logger.info("Searching for experts with expertise: {}", expertiseList);
        if (expertiseList.isEmpty()) {
            logger.debug("No expertise specified, returning all experts");
            return expertRepository.findAll(pageable);
        }
        
        // For each expertise, find matching experts and combine results
        Page<Expert> experts = expertRepository.findByExpertiseOrderByMatchCount(expertiseList.get(0), pageable);
        logger.info("Found {} experts matching expertise", experts.getTotalElements());
        return experts;
    }

    @Override
    public ExpertResponse mapExpertToResponse(Expert expert) {
        ExpertResponse response = new ExpertResponse();
        response.setId(expert.getId());
        
        // Map user details
        UserResponse userResponse = new UserResponse();
        userResponse.setId(expert.getUser().getId());
        userResponse.setName(expert.getUser().getName());
        userResponse.setEmail(expert.getUser().getEmail());
        userResponse.setRole(expert.getUser().getRole().name());
        response.setUser(userResponse);
        
        response.setExpertise(expert.getExpertise());
        response.setHourlyRate(expert.getHourlyRate());
        response.setBio(expert.getBio());
        response.setAvailability(expert.getAvailability());
        return response;
    }
} 