package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.ExpertAvailabilityResponse;

public interface ExpertAvailabilityService {
    ExpertAvailabilityResponse getExpertAvailability(String expertId);
} 