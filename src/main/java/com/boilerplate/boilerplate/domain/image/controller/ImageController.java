package com.boilerplate.boilerplate.domain.image.controller;

import com.boilerplate.boilerplate.domain.image.dto.ImageUploadResponse;
import com.boilerplate.boilerplate.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(
        @RequestPart MultipartFile imageFile
    ) {
        return ResponseEntity.ok(imageService.uploadImage(imageFile));
    }
}
