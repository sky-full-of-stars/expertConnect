package com.uci.expertConnect.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.uci.expertConnect.dto.TimeSlot;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Type(JsonType.class)
    private Map<String, List<TimeSlot>> availability;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String profilePicture;

    @ElementCollection
    @CollectionTable(name = "expert_skills", joinColumns = @JoinColumn(name = "expert_id"))
    @Column(name = "skill")
    private Set<String> skills;

    @OneToMany(mappedBy = "expert")
    @JsonBackReference("expert-meetings")
    private Set<Meeting> meetings;
} 
