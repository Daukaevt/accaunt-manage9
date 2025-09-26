package com.wixsite.mupbam1.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenStoreService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "auth:tokens:";

    public void saveToken(String token, long ttlMillis) {
        redisTemplate.opsForValue().set(PREFIX + token, "1", ttlMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenValid(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }

    public void revokeToken(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
