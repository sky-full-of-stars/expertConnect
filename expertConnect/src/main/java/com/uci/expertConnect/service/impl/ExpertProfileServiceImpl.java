package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.dto.CreateExpertProfileRequest;
import com.uci.expertConnect.exception.DuplicateEmailException;
import com.uci.expertConnect.exception.ResourceNotFoundException;
import com.uci.expertConnect.exception.UnauthorizedAccessException;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.model.User;
import com.uci.expertConnect.repository.ExpertRepository;
import com.uci.expertConnect.repository.UserRepository;
import com.uci.expertConnect.service.ExpertProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpertProfileServiceImpl implements ExpertProfileService {

    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;

    @Autowired
    public ExpertProfileServiceImpl(ExpertRepository expertRepository, UserRepository userRepository) {
        this.expertRepository = expertRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Expert createExpertProfile(CreateExpertProfileRequest request) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        // Check if the email in the request matches the authenticated user's email
        if (!currentUserEmail.equals(request.getEmail())) {
            throw new UnauthorizedAccessException("You can only create a profile with your own email address");
        }

        // Check if expert profile already exists for this email
        if (expertRepository.existsByUserEmail(request.getEmail())) {
            throw new DuplicateEmailException("Expert profile already exists for email " + request.getEmail());
        }

        // Get the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        // Create and save the expert profile
        Expert expert = new Expert();
        expert.setUser(user);
        expert.setExpertise(request.getExpertise());
        expert.setHourlyRate(request.getHourlyRate());
        expert.setBio(request.getBio());
        expert.setAvailability(request.getAvailability());
        
        return expertRepository.save(expert);
    }

    @Override
    public Expert getExpertById(Long expertId) {
        return expertRepository.findById(expertId)
                .orElseThrow(() -> new ResourceNotFoundException("Expert not found with id: " + expertId));
    }

    @Override
    public Page<Expert> findExpertsByExpertise(List<String> expertiseList, Pageable pageable) {
        if (expertiseList.isEmpty()) {
            return expertRepository.findAll(pageable);
        }
        
        // For each expertise, find matching experts and combine results
        List<Expert> allExperts = expertiseList.stream()
                .flatMap(expertise -> 
                    expertRepository.findByExpertiseOrderByMatchCount(expertise, pageable).getContent().stream()
                )
                .distinct()
                .collect(Collectors.toList());
                
        return new PageImpl<>(allExperts, pageable, allExperts.size());
    }
} 