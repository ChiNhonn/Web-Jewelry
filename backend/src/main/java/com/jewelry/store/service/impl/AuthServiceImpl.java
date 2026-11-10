package com.jewelry.store.service.impl;

import com.jewelry.store.dto.request.LoginRequest;
import com.jewelry.store.dto.request.RefreshTokenRequest;
import com.jewelry.store.dto.request.RegisterRequest;
import com.jewelry.store.dto.response.AuthResponse;
import com.jewelry.store.dto.response.UserSummaryResponse;
import com.jewelry.store.entity.PasswordResetOtp;
import com.jewelry.store.entity.RefreshToken;
import com.jewelry.store.entity.User;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.mapper.UserMapper;
import com.jewelry.store.repository.PasswordResetOtpRepository;
import com.jewelry.store.repository.RefreshTokenRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.security.JwtTokenProvider;
import com.jewelry.store.security.UserPrincipal;
import com.jewelry.store.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetOtpRepository otpRepository;
    private final ObjectProvider<JavaMailSender> mailSender;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Địa chỉ email đã được sử dụng bởi một tài khoản khác.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại trong hệ thống.");
        }
        User user = userRepository.save(User.builder()
                .username(request.getUsername().trim().toLowerCase())
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName((request.getLastName().trim() + " " + request.getFirstName().trim()).trim())
                .phone(request.getPhone())
                .role("CUSTOMER")
                .locked(false)
                .build());
        return tokensFor(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));
        if (user.isGuest()) {
            throw new BadRequestException("Tài khoản khách vãng lai không dùng để đăng nhập");
        }
        return tokensFor(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Refresh token không hợp lệ hoặc không tồn tại."));
        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token đã hết hạn hoặc bị thu hồi. Vui lòng đăng nhập lại.");
        }
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return tokensFor(refreshToken.getUser());
    }

    @Override
    @Transactional
    public void logout(String refreshTokenStr) {
        if (refreshTokenStr != null && !refreshTokenStr.isBlank()) {
            refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toUserSummaryResponse(user);
    }

    @Override
    @Transactional
    public String requestPasswordOtp(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy tài khoản với email này"));
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        long now = System.currentTimeMillis();
        otpRepository.save(PasswordResetOtp.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .purpose("RESET_PASSWORD")
                .otpHash(sha(code))
                .otpSalt("sha256")
                .expiresAt(now + 10 * 60 * 1000)
                .lastSentAt(now)
                .sendCount(1)
                .sendWindowStart(now)
                .failedAttempts(0)
                .used(false)
                .build());
        if (sendOtpMail(user.getEmail(), code)) {
            return null;
        }
        log.warn("Chưa gửi được Gmail. Mã OTP cho {} là {}", user.getEmail(), code);
        return code;
    }

    @Override
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("Mật khẩu mới cần ít nhất 6 ký tự");
        }
        PasswordResetOtp saved = otpRepository.findFirstByEmailAndUsedFalseOrderByIdDesc(email.trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Chưa có mã OTP hợp lệ"));
        if (saved.getExpiresAt() < System.currentTimeMillis() || !saved.getOtpHash().equals(sha(otp.trim()))) {
            saved.setFailedAttempts(saved.getFailedAttempts() + 1);
            otpRepository.save(saved);
            throw new BadRequestException("Mã OTP không đúng hoặc đã hết hạn");
        }
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        saved.setUsed(true);
        saved.setVerifiedAt(System.currentTimeMillis());
        otpRepository.save(saved);
    }

    @Override
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("Mật khẩu mới cần ít nhất 6 ký tự");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private AuthResponse tokensFor(User user) {
        UserPrincipal principal = UserPrincipal.create(user);
        RefreshToken refreshToken = refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .revoked(false)
                .createdAt(Instant.now())
                .build());
        return AuthResponse.builder()
                .accessToken(tokenProvider.generateAccessToken(principal))
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpirationMs())
                .user(userMapper.toUserSummaryResponse(user))
                .build();
    }

    private boolean sendOtpMail(String to, String code) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || mailUsername == null || mailUsername.isBlank()) {
            return false;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailUsername);
            message.setTo(to);
            message.setSubject("Mã OTP đặt lại mật khẩu AURA");
            message.setText("Mã OTP của bạn là " + code + ". Mã có hiệu lực trong 10 phút.");
            sender.send(message);
            return true;
        } catch (Exception ex) {
            log.warn("Gửi Gmail thất bại: {}", ex.getMessage());
            return false;
        }
    }

    private String sha(String code) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(code.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
