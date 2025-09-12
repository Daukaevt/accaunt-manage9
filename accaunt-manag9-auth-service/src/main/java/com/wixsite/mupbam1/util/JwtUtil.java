package com.wixsite.mupbam1.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private PrivateKey privateKey;

    // 1 час
    private final long expiration = 1000 * 60 * 60;

    @PostConstruct
    public void init() {
        try {
            // читаем private.pem (PKCS8)
            String key = Files.readString(Path.of("keys/private.pem"))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);

            logger.info("Loaded RSA private key for JWT signing (Auth Service)");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, SignatureAlgorithm.RS256) // 🔑 RS256 вместо HS256
                .compact();
    }

    // ⚠️ validate и getClaims в Auth-сервисе можно оставить только для отладки
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        // тут тоже нужен publicKey, но для Auth обычно не проверяют свои же токены
        throw new UnsupportedOperationException("Use validate only in client-service");
    }
}
