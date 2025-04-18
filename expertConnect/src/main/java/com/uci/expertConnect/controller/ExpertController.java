package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.CreateExpertProfileRequest;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.service.ExpertProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/expert")
public class ExpertController {

    private final ExpertProfileService expertProfileService;

    @Autowired
    public ExpertController(ExpertProfileService expertProfileService) {
        this.expertProfileService = expertProfileService;
    }

    @PostMapping
    public ResponseEntity<Expert> createExpert(@RequestBody CreateExpertProfileRequest request) {
        Expert savedExpert = expertProfileService.createExpertProfile(request);
        return ResponseEntity.ok(savedExpert);
    }
} 