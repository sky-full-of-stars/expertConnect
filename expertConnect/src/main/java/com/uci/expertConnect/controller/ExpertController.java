package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.CreateExpertProfileRequest;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.service.ExpertProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{expertId}")
    public ResponseEntity<Expert> getExpertById(@PathVariable Long expertId) {
        Expert expert = expertProfileService.getExpertById(expertId);
        return ResponseEntity.ok(expert);
    }

    @GetMapping
    public ResponseEntity<Page<Expert>> findExpertsByExpertise(
            @RequestParam List<String> expertise,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "matchCount") String sortBy) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Expert> experts = expertProfileService.findExpertsByExpertise(expertise, pageRequest);
        return ResponseEntity.ok(experts);
    }
} 