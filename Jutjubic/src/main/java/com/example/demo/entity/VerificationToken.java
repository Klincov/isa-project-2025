package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "verification_token", indexes = {
        @Index(name="ix_token_value", columnList="token", unique = true)
})
public class VerificationToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=64)
    private String token;

    @OneToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, unique=true,
            foreignKey = @ForeignKey(name="fk_token_user"))
    private AppUser user;

    @Column(nullable=false)
    private Instant expiresAt;

    @Column
    private Instant confirmedAt;
}