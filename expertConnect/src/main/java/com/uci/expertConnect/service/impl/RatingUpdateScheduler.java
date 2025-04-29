package com.uci.expertConnect.service.impl;

import com.uci.expertConnect.model.Expert;
import com.uci.expertConnect.model.ExpertRating;
import com.uci.expertConnect.repository.ExpertRatingRepository;
import com.uci.expertConnect.repository.ExpertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingUpdateScheduler {
    private static final Logger logger = LoggerFactory.getLogger(RatingUpdateScheduler.class);
    
    private final ExpertRepository expertRepository;
    private final ExpertRatingRepository expertRatingRepository;

    @Autowired
    public RatingUpdateScheduler(
            ExpertRepository expertRepository,
            ExpertRatingRepository expertRatingRepository) {
        this.expertRepository = expertRepository;
        this.expertRatingRepository = expertRatingRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void updateExpertRatings() {
        logger.info("Starting daily expert rating update");
        List<Expert> experts = expertRepository.findAll();
        
        for (Expert expert : experts) {
            try {
                Double averageRating = expertRatingRepository.calculateAverageRating(expert.getId());
                Integer totalReviews = expertRatingRepository.countReviews(expert.getId());
                
                ExpertRating rating = expertRatingRepository.findByExpertId(expert.getId());
                if (rating == null) {
                    rating = new ExpertRating();
                    rating.setExpert(expert);
                }
                
                rating.setAverageRating(averageRating != null ? averageRating : 0.0);
                rating.setTotalReviews(totalReviews != null ? totalReviews : 0);
                
                expertRatingRepository.save(rating);
                logger.info("Updated ratings for expert {}: average={}, total={}", 
                    expert.getId(), rating.getAverageRating(), rating.getTotalReviews());
            } catch (Exception e) {
                logger.error("Error updating ratings for expert {}: {}", expert.getId(), e.getMessage());
            }
        }
        logger.info("Completed daily expert rating update");
    }
} 