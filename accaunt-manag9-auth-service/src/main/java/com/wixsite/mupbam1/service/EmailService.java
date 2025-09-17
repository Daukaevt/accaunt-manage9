package com.wixsite.mupbam1.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.wixsite.mupbam1.exceptions.TooManyEmailRequestsException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    // Запоминаем время последней отправки для каждого email
    private final Map<String, LocalDateTime> lastSent = new ConcurrentHashMap<>();
    private static final int MIN_INTERVAL_SECONDS = 60; // не чаще одного письма в минуту

    public void sendVerificationCode(String to, String code) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastTime = lastSent.get(to);

        // Проверка частоты отправки
        if (lastTime != null && lastTime.plusSeconds(MIN_INTERVAL_SECONDS).isAfter(now)) {
            logger.warn("Too many email requests for {}", to);
            throw new TooManyEmailRequestsException("Too many requests: please wait before requesting another code.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your verification code");
            message.setText("Your verification code is: " + code + "\nIt is valid for 5 minutes.");

            mailSender.send(message);
            lastSent.put(to, now);
            logger.info("Verification code sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email");
        }
    }
}
