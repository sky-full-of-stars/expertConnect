package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.dto.request.CreateReviewRequest;
import com.uci.expertConnect.dto.response.ExpertRatingResponse;
import com.uci.expertConnect.dto.response.ReviewResponse;
import com.uci.expertConnect.exception.NotFoundException;
import com.uci.expertConnect.exception.UnauthorizedException;
import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.model.ExpertRating;
import com.uci.expertConnect.model.Review;
import com.uci.expertConnect.model.User;
import com.uci.expertConnect.repository.ExpertRatingRepository;
import com.uci.expertConnect.repository.ExpertRepository;
import com.uci.expertConnect.repository.ReviewRepository;
import com.uci.expertConnect.repository.UserRepository;
import com.uci.expertConnect.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;
    private final ExpertRatingRepository expertRatingRepository;
    private final RatingUpdateScheduler ratingUpdateScheduler;

    @Autowired
    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            ExpertRepository expertRepository,
            UserRepository userRepository,
            ExpertRatingRepository expertRatingRepository,
            RatingUpdateScheduler ratingUpdateScheduler) {
        this.reviewRepository = reviewRepository;
        this.expertRepository = expertRepository;
        this.userRepository = userRepository;
        this.expertRatingRepository = expertRatingRepository;
        this.ratingUpdateScheduler = ratingUpdateScheduler;
    }

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        Long userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        Expert expert = expertRepository.findById(request.getExpertId())
                .orElseThrow(() -> new NotFoundException("Expert not found with ID: " + request.getExpertId()));

        // Check if user has already reviewed this expert
        if (reviewRepository.existsByExpertIdAndUserId(expert.getId(), userId)) {
            throw new UnauthorizedException("You have already reviewed this expert");
        }

        Review review = new Review();
        review.setExpert(expert);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Override
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found with ID: " + reviewId));
        return mapToResponse(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsByExpertId(Long expertId, Pageable pageable) {
        return reviewRepository.findByExpertId(expertId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ExpertRatingResponse getExpertRating(Long expertId) {
        ExpertRating rating = expertRatingRepository.findByExpertId(expertId);
        ExpertRatingResponse response = new ExpertRatingResponse();
        response.setExpertId(expertId);
        response.setAverageRating(rating != null ? rating.getAverageRating() : 0.0);
        response.setTotalReviews(rating != null ? rating.getTotalReviews() : 0);
        return response;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new NotFoundException("Review not found with ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional
    public void triggerRatingUpdate() {
        ratingUpdateScheduler.updateExpertRatings();
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setExpertId(review.getExpert().getId());
        response.setExpertName(review.getExpert().getUser().getName());
        response.setUserId(review.getUser().getId());
        response.setUserName(review.getUser().getName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }
} 