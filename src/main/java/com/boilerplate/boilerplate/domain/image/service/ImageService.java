package com.boilerplate.boilerplate.domain.image.service;

import com.boilerplate.boilerplate.domain.image.dto.ImageUploadResponse;
import com.boilerplate.boilerplate.domain.image.entity.Image;
import com.boilerplate.boilerplate.domain.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final StorageService storageService;
    private final ImageRepository imageRepository;

    public ImageUploadResponse uploadImage(MultipartFile file) {
        String url = storageService.store(file);
        Image image = new Image(url, file.getOriginalFilename(), file.getContentType());
        imageRepository.save(image);
        return ImageUploadResponse.of(image);
    }

    public Image saveExternalImage(String imageUrl) {
        return storageService.downloadAndSaveImage(imageUrl);
    }
}
