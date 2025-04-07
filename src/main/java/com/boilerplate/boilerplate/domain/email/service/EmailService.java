package com.boilerplate.boilerplate.domain.email.service;

import com.boilerplate.boilerplate.domain.email.dto.VerifyCodeResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String email) {
        String code = createVerificationCode();
        saveVerificationCode(email, code);
        sendEmail(email, code);
    }

    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n유효 시간: 5분");

        mailSender.send(message);
    }

    private String createVerificationCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    private void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(
            "EMAIL:" + email,
            String.valueOf(code),
            Duration.ofMinutes(5)
        );
    }

    public VerifyCodeResponse verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get("EMAIL:" + email);
        boolean verified = code.equals(storedCode);
        if (verified) {
            redisTemplate.opsForValue().set(
                "VERIFIED:" + email,
                String.valueOf(true),
                Duration.ofMinutes(10)
            );
        }
        return new VerifyCodeResponse(code.equals(storedCode));
    }
}
