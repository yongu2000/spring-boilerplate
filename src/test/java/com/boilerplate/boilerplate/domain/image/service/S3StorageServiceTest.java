package com.boilerplate.boilerplate.domain.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.boilerplate.boilerplate.domain.image.exception.ImageUploadFailException;
import com.boilerplate.boilerplate.domain.image.repository.ImageRepository;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@DisplayName("EmailService 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class S3StorageServiceTest {

    private S3Client s3Client;
    private ImageRepository imageRepository;
    private S3StorageService s3StorageService;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        imageRepository = mock(ImageRepository.class);
        s3StorageService = new S3StorageService(s3Client, imageRepository);
        // reflection 으로 필드 주입 (테스트 편의)
        injectValue(s3StorageService, "bucketName", "test-bucket");
        injectValue(s3StorageService, "urlPrefix", "https://test-bucket.s3.amazonaws.com/");
    }

    @Test
    void 업로드_정상_URL_리턴() {
        // given
        MockMultipartFile file = new MockMultipartFile(
            "image", "cat.png", "image/png", "fake-image-content".getBytes()
        );

        // when
        String resultUrl = s3StorageService.store(file);

        // then
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));

        PutObjectRequest actualRequest = captor.getValue();
        assertThat(actualRequest.bucket()).isEqualTo("test-bucket");
        assertThat(resultUrl).startsWith("https://test-bucket.s3.amazonaws.com/");
    }

    @Test
    void IOException_발생시_ImageUploadFailException() throws IOException {
        // given
        MultipartFile brokenFile = mock(MultipartFile.class);
        when(brokenFile.getOriginalFilename()).thenReturn("fail.png");
        when(brokenFile.getBytes()).thenThrow(new IOException("강제 실패"));

        // when & then
        assertThrows(ImageUploadFailException.class, () -> s3StorageService.store(brokenFile));
    }

    
    private void injectValue(Object target, String fieldName, String value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("필드 주입 실패: " + fieldName, e);
        }
    }

}