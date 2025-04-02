package com.boilerplate.boilerplate.domain.image;

import com.boilerplate.boilerplate.domain.image.entity.Image;
import com.boilerplate.boilerplate.domain.image.repository.ImageRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class DefaultImageProvider {

    private final ImageRepository imageRepository;

    private Image defaultProfileImage;

    @PostConstruct
    public void init() {
        this.defaultProfileImage = imageRepository.findByUrl("/uploads/image/default.jpg")
            .orElseGet(() -> imageRepository.save(new Image(
                "/uploads/image/default.jpg",
                "default.jpg",
                "image/jpg"
            )));
    }
}
