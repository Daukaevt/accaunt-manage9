package com.wixsite.mupbam1.controller;

import com.wixsite.mupbam1.dto.AuthRequest;
import com.wixsite.mupbam1.dto.VerificationRequest;
import com.wixsite.mupbam1.service.AuthService;
import com.wixsite.mupbam1.service.EmailService;
import com.wixsite.mupbam1.service.VerificationCodeService;
import com.wixsite.mupbam1.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest authRequest) {
        authService.register(authRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        if (authService.authenticate(authRequest)) {
            String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
            verificationCodeService.saveCode(authRequest.getUsername(), verificationCode);
            String email = authService.getEmailByUsername(authRequest.getUsername());
            emailService.sendVerificationCode(email, verificationCode);
            return ResponseEntity.ok("Please check your email for verification code");
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
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
