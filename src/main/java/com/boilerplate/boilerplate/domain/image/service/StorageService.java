package com.boilerplate.boilerplate.domain.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file);
}
