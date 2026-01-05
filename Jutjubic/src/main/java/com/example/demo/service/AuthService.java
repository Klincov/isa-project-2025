package com.example.demo.service;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.VerificationToken;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${app.base-url}")
    private String baseUrl; // npr http://localhost:8080

    public void register(RegisterRequest req) {
        AppUser user = userService.registerNewUser(req);

        VerificationToken token = tokenService.createForUser(user);
        String link = baseUrl + "/api/auth/activate?token=" + token.getToken();

        emailService.sendActivationEmail(user.getEmail(), link);
    }

    public void activate(String tokenValue) {
        VerificationToken token = tokenService.getByTokenOrThrow(tokenValue);

        if (token.getConfirmedAt() != null) {
            throw new IllegalArgumentException("Token je već iskorišćen.");
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token je istekao.");
        }

        token.setConfirmedAt(Instant.now());
        //tokenRepository.save(token);

        AppUser user = token.getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }
}
