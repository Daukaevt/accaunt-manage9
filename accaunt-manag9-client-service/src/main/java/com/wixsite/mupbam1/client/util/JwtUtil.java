package com.wixsite.mupbam1.client.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class JwtUtil {

    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            log.info("Загружаем public.pem...");
            String key = new String(Files.readAllBytes(Paths.get("keys/public.pem")))
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(spec);
            log.info("Public key успешно загружен для проверки JWT");
        } catch (Exception e) {
            log.error("Ошибка при загрузке public.pem", e);
            throw new RuntimeException("Ошибка при загрузке public.pem", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            log.info("Проверяем JWT: {}", token);
            getClaims(token);
            log.info("JWT валиден");
            return true;
        } catch (Exception e) {
            log.warn("JWT невалиден: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }
}
