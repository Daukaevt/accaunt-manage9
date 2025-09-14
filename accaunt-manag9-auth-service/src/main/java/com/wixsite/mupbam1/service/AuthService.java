package com.wixsite.mupbam1.service;

import com.wixsite.mupbam1.dto.AuthRequest;
import com.wixsite.mupbam1.exceptions.EmailAlreadyRegisteredException;
import com.wixsite.mupbam1.exceptions.UserAlreadyExistsException;
import com.wixsite.mupbam1.exceptions.UserBlockedException;
import com.wixsite.mupbam1.model.User;
import com.wixsite.mupbam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Для brute-force защиты
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 5;
    private final int BLOCK_MINUTES = 15;

    public boolean authenticate(AuthRequest authRequest) {
        String username = authRequest.getUsername().toLowerCase();

/* Проверяем, заблокирован ли пользователь. Хранится в памяти.
 * 
 * 🔹 Следствия этого подхода:

Работает только на одном инстансе приложения.
Если у тебя несколько серверов (кластер), попытки логина на одном инстансе не будут учитываться на другом.

Данные сбрасываются при перезапуске сервера.
После рестарта все пользователи снова могут делать попытки без ограничения.

Хранение в памяти быстрое, но нестабильное для масштабируемых или долгоживущих сервисов.

💡 Рекомендации для продакшена:

Хранить счётчики и время блокировки в Redis или другой внешней базе с TTL.

Это позволит блокировать пользователей глобально для всех инстансов и сохранять данные при перезапуске.
 * 
 */
        if (blockedUntil.containsKey(username)) {
            LocalDateTime unblockTime = blockedUntil.get(username);
            if (LocalDateTime.now().isBefore(unblockTime)) {
                throw new UserBlockedException("Too many failed login attempts. User is temporarily blocked for " + BLOCK_MINUTES + " minutes.");
            } else {
                blockedUntil.remove(username);
                loginAttempts.remove(username);
            }
        }

        boolean authenticated = userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
                .isPresent();

        if (authenticated) {
            loginAttempts.remove(username);
            blockedUntil.remove(username);
        } else {
            int attempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, attempts);

            if (attempts >= MAX_ATTEMPTS) {
                blockedUntil.put(username, LocalDateTime.now().plusMinutes(BLOCK_MINUTES));
                throw new UserBlockedException("Too many failed login attempts. User is temporarily blocked for " + BLOCK_MINUTES + " minutes.");
            }
        }

        return authenticated;
    }


    public void register(AuthRequest authRequest) {
        String normalizedUsername = authRequest.getUsername().toLowerCase();

        if (userRepository.findByUsername(normalizedUsername).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        if (userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        User user = new User();
        user.setUsername(normalizedUsername); // сохраняем в нижнем регистре
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(authRequest.getRole());
        user.setEmail(authRequest.getEmail());
        userRepository.save(user);
    }

    
    public String getEmailByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase())
                .map(User::getEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
    public String getRoleByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase())
                .map(User::getRole)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }  
}
