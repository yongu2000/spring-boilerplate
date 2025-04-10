package com.boilerplate.boilerplate.domain.email.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.boilerplate.boilerplate.domain.email.dto.VerifyCodeResponse;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    private static final String EMAIL = "test@example.com";
    private static final String VERIFICATION_CODE = "123456";


    @Test
    void 인증코드_전송_성공() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(javaMailSender.createMimeMessage()).willReturn(new MimeMessage((jakarta.mail.Session) null));

        // when
        emailService.sendVerificationEmail(EMAIL);

        // then
        then(redisTemplate).should().opsForValue();
        then(javaMailSender).should().send(any(MimeMessage.class));
    }

    @Test
    void 비밀번호_재설정_메일_전송_성공() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(javaMailSender.createMimeMessage()).willReturn(new MimeMessage((jakarta.mail.Session) null));

        // when
        emailService.sendPasswordResetEmail(EMAIL);

        // then
        then(redisTemplate).should().opsForValue();
        then(javaMailSender).should().send(any(MimeMessage.class));
    }

    @Test
    void 인증코드_검증_성공() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("EMAIL:VERIFICATION:" + EMAIL)).willReturn(VERIFICATION_CODE);

        // when
        VerifyCodeResponse response = emailService.verifyCode(EMAIL, VERIFICATION_CODE);

        // then
        assertThat(response.verified()).isTrue();
        then(redisTemplate).should(times(2)).opsForValue();
    }

    @Test
    void 인증코드_검증_실패() {
        // given
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("EMAIL:VERIFICATION:" + EMAIL)).willReturn("999999");

        // when
        VerifyCodeResponse response = emailService.verifyCode("test@example.com", "123456");

        // then
        assertThat(response.verified()).isFalse();
    }
}
