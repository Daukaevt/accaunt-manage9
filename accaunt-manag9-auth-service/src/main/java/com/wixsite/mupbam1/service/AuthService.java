package com.wixsite.mupbam1.service;

import com.wixsite.mupbam1.dto.AuthRequest;
import com.wixsite.mupbam1.exceptions.EmailAlreadyRegisteredException;
import com.wixsite.mupbam1.exceptions.UserAlreadyExistsException;
import com.wixsite.mupbam1.exceptions.UserBlockedException;
import com.wixsite.mupbam1.model.User;
import com.wixsite.mupbam1.repository.UserRepository;
import com.wixsite.mupbam1.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final TokenStoreService tokenStoreService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Для brute-force (пока оставляем в памяти, потом перепишем на Redis)
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 5;
    private final int BLOCK_MINUTES = 15;

    /**
     * Аутентификация: проверка пароля → генерация JWT → сохранение в Redis
     */
    public String authenticate(AuthRequest authRequest) {
        String username = authRequest.getUsername().toLowerCase();

        // Проверка блокировки
        if (blockedUntil.containsKey(username)) {
            LocalDateTime unblockTime = blockedUntil.get(username);
            if (LocalDateTime.now().isBefore(unblockTime)) {
                throw new UserBlockedException(
                        "Too many failed login attempts. User is temporarily blocked for "
                                + BLOCK_MINUTES + " minutes."
                );
            } else {
                blockedUntil.remove(username);
                loginAttempts.remove(username);
            }
        }

        // Проверка логина/пароля
        User user = userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(authRequest.getPassword(), u.getPassword()))
                .orElse(null);

        if (user == null) {
            int attempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, attempts);

            if (attempts >= MAX_ATTEMPTS) {
                blockedUntil.put(username, LocalDateTime.now().plusMinutes(BLOCK_MINUTES));
                throw new UserBlockedException(
                        "Too many failed login attempts. User is temporarily blocked for "
                                + BLOCK_MINUTES + " minutes."
                );
            }

            return null; // неверный пароль
        }

        // Успешный логин → обнуляем счётчики
        loginAttempts.remove(username);
        blockedUntil.remove(username);

        // Генерация JWT
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Сохраняем в Redis (TTL = 1 час = 3600000 мс)
        tokenStoreService.saveToken(token, 3600_000);

        return token;
    }

    public void register(AuthRequest authRequest) {
        String normalizedUsername = authRequest.getUsername().toLowerCase();
        normalizedUsername = StringEscapeUtils.escapeHtml(normalizedUsername);

        if (userRepository.findByUsername(normalizedUsername).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        if (userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        User user = new User();
        user.setUsername(normalizedUsername);
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
