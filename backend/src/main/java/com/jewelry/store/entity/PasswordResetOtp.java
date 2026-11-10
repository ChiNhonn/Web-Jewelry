package com.jewelry.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "otp_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 50)
    private String purpose;

    @Column(name = "otp_hash", nullable = false)
    private String otpHash;

    @Column(name = "otp_salt", nullable = false)
    private String otpSalt;

    @Column(name = "expires_at", nullable = false)
    private Long expiresAt;

    @Column(name = "last_sent_at", nullable = false)
    private Long lastSentAt;

    @Column(name = "send_count", nullable = false)
    @Builder.Default
    private Integer sendCount = 1;

    @Column(name = "send_window_start", nullable = false)
    private Long sendWindowStart;

    @Column(name = "failed_attempts", nullable = false)
    @Builder.Default
    private Integer failedAttempts = 0;

    @Column(name = "verified_at")
    private Long verifiedAt;

    @Column(name = "reset_token_hash")
    private String resetTokenHash;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;
}
