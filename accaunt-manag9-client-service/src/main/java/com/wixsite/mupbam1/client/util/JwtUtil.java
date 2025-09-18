package com.wixsite.mupbam1.client.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
            String key = Files.readString(Paths.get("keys/public.pem"))
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
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

    public Claims validateAndGetClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
