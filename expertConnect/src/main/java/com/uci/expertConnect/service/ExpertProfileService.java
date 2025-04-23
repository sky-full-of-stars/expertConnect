package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.request.CreateExpertProfileRequest;
import com.uci.expertConnect.dto.response.ExpertResponse;
import com.uci.expertConnect.model.Expert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ExpertProfileService {
    Expert createExpertProfile(CreateExpertProfileRequest request);
    Expert getExpertById(Long expertId);
    Page<Expert> findExpertsByExpertise(List<String> expertiseList, Pageable pageable);
    ExpertResponse mapExpertToResponse(Expert expert);
} 