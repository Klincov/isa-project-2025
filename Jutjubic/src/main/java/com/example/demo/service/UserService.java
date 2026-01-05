package com.example.demo.service;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.VerificationToken;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser registerNewUser(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Lozinke se ne poklapaju.");
        }
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email je već zauzet.");
        }
        if (userRepository.existsByUsernameIgnoreCase(req.getUsername())) {
            throw new IllegalArgumentException("Korisničko ime je već zauzeto.");
        }

        AppUser user = AppUser.builder()
                .email(req.getEmail().trim().toLowerCase())
                .username(req.getUsername().trim())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .address(req.getAddress().trim())
                .enabled(false)
                .locked(false)
                .createdAt(Instant.now())
                .build();

        return userRepository.save(user);
    }

    public void enableUser(AppUser user) {
        user.setEnabled(true);
        userRepository.save(user);
    }
}
