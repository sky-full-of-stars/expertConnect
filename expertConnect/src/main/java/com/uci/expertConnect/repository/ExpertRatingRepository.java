package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.ExpertRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertRatingRepository extends JpaRepository<ExpertRating, Long> {
    ExpertRating findByExpertId(Long expertId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.expert.id = :expertId")
    Double calculateAverageRating(@Param("expertId") Long expertId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.expert.id = :expertId")
    Integer countReviews(@Param("expertId") Long expertId);
} 