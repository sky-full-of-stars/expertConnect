package com.uci.expertConnect.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @OneToOne(mappedBy = "user")
    private Expert expert;
    
    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;
    
    public enum UserRole {
        CLIENT,
        EXPERT
    }
} 