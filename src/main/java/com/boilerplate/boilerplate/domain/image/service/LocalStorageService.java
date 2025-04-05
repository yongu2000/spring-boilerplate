package com.boilerplate.boilerplate.domain.image.service;

import com.boilerplate.boilerplate.domain.image.entity.Image;
import com.boilerplate.boilerplate.domain.image.exception.ImageUploadFailException;
import com.boilerplate.boilerplate.domain.image.repository.ImageRepository;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class LocalStorageService implements StorageService {

    private static final String baseDir = "uploads/image/";
    private static final String fileUploadPath = "/uploads/image/";

    private final ImageRepository imageRepository;

    public LocalStorageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

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

    @Override
    public String downloadImage(String imageUrl) {
        try {
            String fileName = getFileNameFromUrl(imageUrl);
            if (fileName == null || fileName.isBlank()) {
                fileName = UUID.randomUUID() + ".jpg";
            } else {
                fileName = UUID.randomUUID() + "_" + fileName;
            }

            Path savePath = Paths.get(baseDir, fileName);
            Files.createDirectories(savePath.getParent());

            try (InputStream in = new URL(imageUrl).openStream()) {
                Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return fileUploadPath + fileName;

        } catch (IOException e) {
            throw new RuntimeException("이미지 다운로드 실패", e);
        }
    }

    @Override
    public Image downloadAndSaveImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            String contentType = conn.getContentType();
            String originalFileName = getFileNameFromUrl(imageUrl);
            if (originalFileName == null || originalFileName.isBlank()) {
                originalFileName = "unknown.jpg";
            }

            String savedUrl = downloadImage(imageUrl);

            Image image = Image.builder()
                .url(savedUrl)
                .originalFileName(originalFileName)
                .contentType(contentType)
                .build();

            return imageRepository.save(image);

        } catch (IOException e) {
            throw new RuntimeException("이미지 다운로드 및 저장 실패", e);
        }
    }

    private String getFileNameFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();
            return path.substring(path.lastIndexOf("/") + 1);
        } catch (Exception e) {
            return null;
        }
    }
}
