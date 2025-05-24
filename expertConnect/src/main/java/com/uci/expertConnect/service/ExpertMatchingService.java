package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.request.FindMatchingExpertsRequest;

import java.util.List;

public interface ExpertMatchingService {
    List<Integer> findMatchingExperts(FindMatchingExpertsRequest request);

}
