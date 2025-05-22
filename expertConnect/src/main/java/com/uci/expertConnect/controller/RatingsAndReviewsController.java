package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.CreateReviewRequest;
import com.uci.expertConnect.dto.response.ExpertRatingResponse;
import com.uci.expertConnect.dto.response.ReviewResponse;
import com.uci.expertConnect.service.ReviewService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/experts")
public class RatingsAndReviewsController {
    private static final Logger logger = LoggerFactory.getLogger(RatingsAndReviewsController.class);
    private final ReviewService reviewService;

    @Autowired
    public RatingsAndReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{expertId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long expertId,
            @Valid @RequestBody CreateReviewRequest request) {
        logger.info("Received request to create review for expert {}", expertId);
        request.setExpertId(expertId);
        ReviewResponse response = reviewService.createReview(request);
        logger.info("Successfully created review with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{expertId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getExpertReviews(
            @PathVariable Long expertId,
            Pageable pageable) {
        logger.info("Received request to get reviews for expert {}", expertId);
        Page<ReviewResponse> reviews = reviewService.getReviewsByExpertId(expertId, pageable);
        logger.info("Found {} reviews for expert {}", reviews.getTotalElements(), expertId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{expertId}/rating")
    public ResponseEntity<ExpertRatingResponse> getExpertRating(@PathVariable Long expertId) {
        logger.info("Received request to get rating for expert {}", expertId);
        ExpertRatingResponse response = reviewService.getExpertRating(expertId);
        logger.info("Average rating for expert {} is {}", expertId, response.getAverageRating());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ratings/update")
    public ResponseEntity<Void> triggerRatingUpdate() {
        logger.info("Received request to manually trigger rating update");
        reviewService.triggerRatingUpdate();
        logger.info("Successfully triggered rating update");
        return ResponseEntity.ok().build();
    }
} 