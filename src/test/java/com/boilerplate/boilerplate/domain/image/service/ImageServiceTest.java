package com.boilerplate.boilerplate.domain.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.boilerplate.boilerplate.domain.image.dto.ImageUploadResponse;
import com.boilerplate.boilerplate.domain.image.entity.Image;
import com.boilerplate.boilerplate.domain.image.exception.ImageError;
import com.boilerplate.boilerplate.domain.image.exception.ImageUploadFailException;
import com.boilerplate.boilerplate.domain.image.repository.ImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("이미지 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    public Image uploadImage(MultipartFile file) {
        String url = storageService.store(file);
        Image image = new Image(url, file.getOriginalFilename(), file.getContentType());
        return imageRepository.save(image);
    }

    @Test
    void 이미지_업로드_성공() {
        // given
        MultipartFile mockFile = new MockMultipartFile("image", "pic.jpg", "image/jpg",
            "abc".getBytes());
        String fakeUrl = "/uploads/uuid_pic.jpg";
        given(storageService.store(any(MultipartFile.class))).willReturn(fakeUrl);
        given(imageRepository.save(any(Image.class))).willAnswer(
            invocation -> invocation.getArgument(0));

        // when
        ImageUploadResponse result = imageService.uploadImage(mockFile);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo(fakeUrl);
        then(storageService).should().store(mockFile);
        then(imageRepository).should().save(any(Image.class));
    }

    @Test
    void 이미지_업로드_실패() throws Exception {
        // given
        MockMultipartFile mockFile = new MockMultipartFile("image", "pic.jpg", "image/jpg",
            "fail".getBytes());
        given(storageService.store(any(MultipartFile.class))).willThrow(
            new ImageUploadFailException());

        // when
        assertThatThrownBy(() -> imageService.uploadImage(mockFile))
            .isInstanceOf(ImageUploadFailException.class)
            .hasMessage(
                ImageError.IMAGE_UPLOAD_FAIL.getMessage());
    }

}