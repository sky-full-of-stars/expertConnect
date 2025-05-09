package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.FindMatchingExpertsRequest;
import com.uci.expertConnect.dto.response.ExpertResponse;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.service.ExpertMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/v1/expert-matching")
public class ExpertMatchingController {
    private static final Logger logger = LoggerFactory.getLogger(ExpertMatchingController.class);
    private final ExpertMatchingService expertMatchingService;

    @Autowired
    public ExpertMatchingController(ExpertMatchingService expertMatchingService) {
        this.expertMatchingService = expertMatchingService;
    }

    @PostMapping("/match")
    public List<Integer> findMatchingExperts(@RequestBody FindMatchingExpertsRequest request) {
        logger.debug("Received request to match experts for text: '{}'", request.getText());

        List<Integer> matchingExperts = expertMatchingService.findMatchingExperts(request.getText());

        logger.debug("Matching expert IDs returned: {}", matchingExperts);

        return matchingExperts;
    }
}
