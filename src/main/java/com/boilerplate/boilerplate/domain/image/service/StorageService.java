package com.boilerplate.boilerplate.domain.image.service;

import com.boilerplate.boilerplate.domain.image.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file);
    
    Image downloadAndSaveImage(String imageUrl);
}
