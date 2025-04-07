package com.boilerplate.boilerplate.domain.image.dto;

import com.boilerplate.boilerplate.domain.image.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ImageUploadResponse {

    private String imageUrl;

    public static ImageUploadResponse of(Image image) {
        return ImageUploadResponse.builder()
            .imageUrl(image.getUrl())
            .build();
    }
}
