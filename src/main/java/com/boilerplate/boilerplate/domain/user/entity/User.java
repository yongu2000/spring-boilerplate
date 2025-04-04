package com.boilerplate.boilerplate.domain.user.entity;

import com.boilerplate.boilerplate.domain.image.entity.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id", nullable = false)
    private Image profileImage;
    private String bio;
    private String provider;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public User(String email, String username, String password, String name, Role role, String provider) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.provider = provider;
    }

    public void updateProfile(String name, String bio, String email, String username) {
        if (name != null) {
            this.name = name;
        }
        if (bio != null) {
            this.bio = bio;
        }
        if (email != null) {
            this.email = email;
        }
        if (username != null) {
            this.username = username;
        }
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeProfileImage(Image newImage) {
        this.profileImage = newImage;
    }

}
