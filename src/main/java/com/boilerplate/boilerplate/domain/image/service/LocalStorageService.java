package com.boilerplate.boilerplate.domain.image.service;

import com.boilerplate.boilerplate.domain.image.exception.ImageUploadFailException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class LocalStorageService implements StorageService {

    private static final String baseDir = "uploads/image/";
    private static final String fileUploadPath = "/uploads/image/";

    @Override
    public String store(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(baseDir, fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return fileUploadPath + fileName;
        } catch (IOException e) {
            throw new ImageUploadFailException();
        }
    }
}
