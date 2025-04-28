package com.uci.expertConnect.dto.response;

import lombok.Data;

@Data
public class ExpertRatingResponse {
    private Long expertId;
    private Double averageRating;
    private Integer totalReviews;
} 