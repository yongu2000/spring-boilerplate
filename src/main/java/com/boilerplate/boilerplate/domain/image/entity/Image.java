package com.boilerplate.boilerplate.domain.image.entity;

import com.boilerplate.boilerplate.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@SQLDelete(sql = "UPDATE image SET deleted_at = CURRENT_TIMESTAMP WHERE image_id = ?")
@SQLRestriction("deleted_at is null")
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", updatable = false)
    private Long id;
    private String url;
    private String originalFileName;
    private String contentType;

    @Builder
    public Image(String url, String originalFileName, String contentType) {
        this.url = url;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
    }
}
