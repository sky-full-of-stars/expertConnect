package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.FindMatchingExpertsRequest;
import com.uci.expertConnect.dto.response.ExpertResponse;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.service.ExpertMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/expert-matching")
public class ExpertMatchingController {

    private final ExpertMatchingService expertMatchingService;

    @Autowired
    public ExpertMatchingController(ExpertMatchingService expertMatchingService) {
        this.expertMatchingService = expertMatchingService;
    }


    // needs to be tested
    @PostMapping("/match")
    public List<Integer> findMatchingExperts(@RequestBody FindMatchingExpertsRequest request) {
        return expertMatchingService.findMatchingExperts(request.getText());
    }
}
