package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByExpertId(Long expertId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.expert.id = :expertId")
    Double getAverageRatingForExpert(@Param("expertId") Long expertId);
    
    boolean existsByExpertIdAndUserId(Long expertId, Long userId);
} 