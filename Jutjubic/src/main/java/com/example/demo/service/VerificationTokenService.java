package com.example.demo.service;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.VerificationToken;
import com.example.demo.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    public VerificationToken createForUser(AppUser user) {
        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString().replace("-", ""))
                .user(user)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        return tokenRepository.save(token);
    }

    public VerificationToken getByTokenOrThrow(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Nevažeći aktivacioni token."));
    }
}
