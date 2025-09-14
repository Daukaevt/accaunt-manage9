package com.wixsite.mupbam1.service;

import com.wixsite.mupbam1.dto.AuthRequest;
import com.wixsite.mupbam1.exceptions.EmailAlreadyRegisteredException;
import com.wixsite.mupbam1.exceptions.UserAlreadyExistsException;
import com.wixsite.mupbam1.model.User;
import com.wixsite.mupbam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void register(AuthRequest authRequest) {
    	String normalizedUsername = authRequest.getUsername().toLowerCase();
    	
        if (userRepository.findByUsername(normalizedUsername).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        if (userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
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

    public boolean authenticate(AuthRequest authRequest) {
    	String normalizedUsername = authRequest.getUsername().toLowerCase();
        return userRepository.findByUsername(normalizedUsername)
                .filter(user -> passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
                .isPresent();
    }   
    
}
