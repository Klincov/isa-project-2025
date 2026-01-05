package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Size(max = 255)
    private String address;
}
