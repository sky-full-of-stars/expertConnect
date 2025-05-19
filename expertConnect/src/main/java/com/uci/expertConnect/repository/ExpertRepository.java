package com.uci.expertConnect.repository;

import com.uci.expertConnect.model.Expert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long> {
    Optional<Expert> findByUserEmail(String email);
    boolean existsByUserEmail(String email);

    @Query("SELECT e FROM Expert e " +
           "WHERE :expertise MEMBER OF e.expertise")
    Page<Expert> findByExpertiseOrderByMatchCount(
        @Param("expertise") String expertise,
        Pageable pageable
    );

    // for vector similarity using pgvector
    @Query(value = """
        SELECT id FROM experts 
        ORDER BY bio_embedding <#> cast(:embedding as vector) 
        LIMIT 25
        """, nativeQuery = true)
    List<Integer> findTop50ByEmbedding(@Param("embedding") float[] embedding);
} 