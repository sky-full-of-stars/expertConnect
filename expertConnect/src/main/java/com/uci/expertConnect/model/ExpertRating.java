package com.uci.expertConnect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "expert_ratings")
public class ExpertRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "expert_id", nullable = false, unique = true)
    private Expert expert;
    
    @Column(nullable = false)
    private Double averageRating;
    
    @Column(nullable = false)
    private Integer totalReviews;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
} 