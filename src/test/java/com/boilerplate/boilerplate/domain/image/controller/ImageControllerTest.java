package com.boilerplate.boilerplate.domain.image.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.boilerplate.domain.image.dto.ImageUploadResponse;
import com.boilerplate.boilerplate.domain.image.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WebMvcTest(ImageController.class)
@DisplayName("이미지 업로드 Controller")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @MockitoBean
    private ImageService imageService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 이미지_업로드_성공() throws Exception {
        // given
        MockMultipartFile imageFile = new MockMultipartFile(
            "imageFile", "cat.jpg", "image/jpeg", "image-data".getBytes());
        ImageUploadResponse mockResponse = new ImageUploadResponse("/uploads/image/cat.jpg");
        given(imageService.uploadImage(any(MultipartFile.class))).willReturn(mockResponse);

        // when & then
        mockMvc.perform(multipart("/api/image/upload")
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.imageUrl").value("/uploads/image/cat.jpg"))
            .andDo(
                document("image-upload",
                    requestParts(
                        partWithName("imageFile").description("업로드할 이미지 파일")
                    ),
                    responseFields(
                        fieldWithPath("imageUrl").description("저장된 이미지의 접근 URL")
                    )
                ));
    }

}