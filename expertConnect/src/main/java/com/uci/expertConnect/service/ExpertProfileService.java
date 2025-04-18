package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.CreateExpertProfileRequest;
import com.uci.expertConnect.model.Expert;

public interface ExpertProfileService {
    Expert createExpertProfile(CreateExpertProfileRequest request);
} 