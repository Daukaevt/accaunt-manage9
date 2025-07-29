package com.wixsite.mupbam1.controller;

import com.wixsite.mupbam1.dto.AuthRequest;
import com.wixsite.mupbam1.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        return jwtUtil.generateToken(authRequest.getUsername());
    }

    @GetMapping("/validate")
    public boolean validate(@RequestParam String token) {
        return jwtUtil.validateToken(token);
    }

    @GetMapping("/")
    public String index() {
        return "Hello from auth-service!";
    }
}
