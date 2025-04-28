package com.uci.expertConnect.service;

import com.uci.expertConnect.dto.request.CreateReviewRequest;
import com.uci.expertConnect.dto.response.ExpertRatingResponse;
import com.uci.expertConnect.dto.response.ReviewResponse;
import com.uci.expertConnect.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(Long userId, CreateReviewRequest request);
    ReviewResponse getReviewById(Long reviewId);
    Page<ReviewResponse> getReviewsByExpertId(Long expertId, Pageable pageable);
    ExpertRatingResponse getExpertRating(Long expertId);
    void deleteReview(Long reviewId);
} 