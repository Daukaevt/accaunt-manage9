package com.wixsite.mupbam1.controller;

import com.wixsite.mupbam1.dto.AuthRequest;
import com.wixsite.mupbam1.dto.VerifyRequest;
import com.wixsite.mupbam1.model.User;
import com.wixsite.mupbam1.service.AuthService;
import com.wixsite.mupbam1.service.EmailService;
import com.wixsite.mupbam1.service.TokenStoreService;
import com.wixsite.mupbam1.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final TokenStoreService tokenStoreService;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthService authService;
    private final EmailService emailService;

    // 🔹 1. Login — проверяем пользователя, создаём код 2FA и отправляем на email
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        // TODO: проверка username/password через AuthService
    	// Аутентификация пользователя: возвращает пользователя или null
    	User user = authService.authenticate(authRequest);

    	if ( user == null) {
    	    return ResponseEntity.status(401).body(Map.of(
    	            "error", "Invalid username or password"
    	    ));
    	}

    	// Далее: генерация 2FA кода, отправка email и т.д.

        // Генерация 6-значного кода 2FA
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        // Сохраняем код в Redis с TTL 10 минут
        String redisKey = "2fa:" + authRequest.getUsername();
        redisTemplate.opsForValue().set(redisKey, code, 10, TimeUnit.MINUTES);

        // Отправка кода на email
        String email = authService.getEmailByUsername(authRequest.getUsername());
        emailService.sendVerificationCode(email, code);

        return ResponseEntity.ok(Map.of(
                "message", "Please check your email for verification code"
        ));
    }

    // 🔹 2. Verify — проверяем код 2FA, создаём JWT и сохраняем в Redis
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest verifyRequest) {
        boolean codeValid = checkCode(verifyRequest.getUsername(), verifyRequest.getCode());
        if (!codeValid) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Invalid or expired verification code"
            ));
        }

        // Получаем роль пользователя через AuthService
        String role = authService.getRoleByUsername(verifyRequest.getUsername());

        // Генерация JWT
        String token = jwtUtil.generateToken(verifyRequest.getUsername(), role);


        // Сохраняем токен в Redis с TTL 1 час
        tokenStoreService.saveToken(token, 3600_000);
        /*
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(Map.of(
                    "message", "Verification successful"
                ));
                */

        return ResponseEntity.ok(Map.of(
                "message", "Verification successful",
                "token", token
        ));
    }

    // 🔹 Проверка кода 2FA в Redis
    private boolean checkCode(String username, String code) {
        String redisKey = "2fa:" + username;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            return false; // код не найден или истёк
        }

        boolean isValid = storedCode.equals(code);

        if (isValid) {
            redisTemplate.delete(redisKey); // удаляем код после успешной проверки
        }

        return isValid;
    }
}
