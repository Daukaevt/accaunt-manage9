package com.wixsite.mupbam1.client.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.wixsite.mupbam1.client.dto.AuthRequest;
import com.wixsite.mupbam1.client.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest authRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(), authRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(authRequest.getUsername());

            response.put("username", authRequest.getUsername());
            response.put("token", token);
            return response;

        } catch (AuthenticationException e) {
            response.put("error", "Неверный логин или пароль");
            return response;
        }
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        SecurityContextHolder.clearContext();
        return Map.of("message", "Вы вышли из системы");
    }
}
