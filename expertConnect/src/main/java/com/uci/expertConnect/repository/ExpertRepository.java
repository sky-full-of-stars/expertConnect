package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long> {
    Optional<Expert> findByUserEmail(String email);
    boolean existsByUserEmail(String email);
} 