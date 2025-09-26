package com.wixsite.mupbam1.controller;

import com.wixsite.mupbam1.dto.AuthRequest;
import com.wixsite.mupbam1.dto.VerificationRequest;
import com.wixsite.mupbam1.service.AuthService;
import com.wixsite.mupbam1.service.EmailService;
import com.wixsite.mupbam1.service.TokenStoreService;
import com.wixsite.mupbam1.service.VerificationCodeService;
import com.wixsite.mupbam1.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final TokenStoreService tokenStoreService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest authRequest) {
        authService.register(authRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        // 1️⃣ Аутентификация пользователя, возвращает JWT
        String token = authService.authenticate(authRequest);

        if (token != null) {
            // 2️⃣ Сохраняем токен в Redis с TTL (например, 1 час)
            tokenStoreService.saveToken(token, 3600_000); // TTL = 1 час

            // 3️⃣ Генерация verification code
            String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
            verificationCodeService.saveCode(authRequest.getUsername(), verificationCode);

            // 4️⃣ Отправка кода на email
            String email = authService.getEmailByUsername(authRequest.getUsername());
            emailService.sendVerificationCode(email, verificationCode);

            // 5️⃣ Возвращаем токен и сообщение клиенту
            return ResponseEntity.ok(Map.of(
                "token", token,
                "message", "Please check your email for verification code"
            ));
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }


    
    /**
     * Logout: отзывает токен пользователя
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenStoreService.revokeToken(token);
        }

        return ResponseEntity.ok("{\"message\": \"Logout successful\"}");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerificationRequest verifyRequest) {
        // Проверка кода выбрасывает исключения для всех ошибок
        verificationCodeService.verifyCode(verifyRequest.getUsername(), verifyRequest.getCode());

        // Если успешно, выдаем JWT
        String role = authService.getRoleByUsername(verifyRequest.getUsername());
        return ResponseEntity.ok(jwtUtil.generateToken(verifyRequest.getUsername(), role));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String token) {
        return ResponseEntity.ok(jwtUtil.validateToken(token));
    }
}
