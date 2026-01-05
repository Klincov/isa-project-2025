package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_user_email", columnNames="email"),
                @UniqueConstraint(name="uk_user_username", columnNames="username")
        })
public class AppUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String username;

    @Column(nullable=false)
    private String passwordHash;

    @Column(nullable=false)
    private String firstName;

    @Column(nullable=false)
    private String lastName;

    @Column(nullable=false)
    private String address;

    @Column(nullable=false)
    private boolean enabled; // tek nakon aktivacije true

    @Column(nullable=false)
    private boolean locked;

    @Column(nullable=false)
    private Instant createdAt;
}