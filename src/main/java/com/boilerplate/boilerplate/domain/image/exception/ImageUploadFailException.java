package com.boilerplate.boilerplate.domain.image.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class ImageUploadFailException extends BusinessException {

    public ImageUploadFailException() {
        super(ImageError.IMAGE_UPLOAD_FAIL);
    }
}
