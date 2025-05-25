package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.ExpertAvailabilityResponse;
import com.uci.expertConnect.exception.ResourceNotFoundException;
import com.uci.expertConnect.service.ExpertAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/experts")
@RequiredArgsConstructor
public class ExpertAvailabilityController {

    private final ExpertAvailabilityService expertAvailabilityService;

    @GetMapping("/{expertId}/availability")
    public ResponseEntity<?> getExpertAvailability(@PathVariable String expertId) {
        try {
            ExpertAvailabilityResponse response = expertAvailabilityService.getExpertAvailability(expertId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request");
        }
    }
} 