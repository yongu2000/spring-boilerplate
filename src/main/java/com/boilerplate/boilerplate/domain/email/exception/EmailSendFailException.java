package com.boilerplate.boilerplate.domain.email.exception;

import com.boilerplate.boilerplate.global.exception.BusinessException;

public class EmailSendFailException extends BusinessException {

    public EmailSendFailException() {
        super(EmailError.EMAIL_SEND_FAIL);
    }
}
