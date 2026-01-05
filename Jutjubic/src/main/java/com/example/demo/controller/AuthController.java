package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ApiMessage register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return new ApiMessage("Registracija uspesna. Proveri email i aktiviraj nalog.");
    }

    @GetMapping("/activate")
    public ApiMessage activate(@RequestParam String token) {
        authService.activate(token);
        return new ApiMessage("Nalog je aktiviran. Sada se mozes prijaviti.");
    }

    @PostMapping("/login")
    public ApiMessage login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        // Ako treba sesija/JWT, ovde ide generisanje tokena; za sada samo “OK”.
        return new ApiMessage("Prijava uspesna.");
    }
}
