package com.uci.expertConnect.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "experts")
public class Expert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "expert_expertise",
        joinColumns = @JoinColumn(name = "expert_id")
    )
    @Column(name = "expertise")
    private List<String> expertise;
    
    @Column(name = "hourly_rate", nullable = false)
    private Double hourlyRate;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> availability;

    // Use pgvector to store the bio embedding as a vector
    @Basic
    @Type(JsonType.class)
    @Column(name = "bio_embedding", columnDefinition = "vector(1536)")
    private List<Double> bioEmbedding;
} 
