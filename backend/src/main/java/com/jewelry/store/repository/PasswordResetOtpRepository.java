package com.jewelry.store.repository;

import com.jewelry.store.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findFirstByEmailAndUsedFalseOrderByIdDesc(String email);
}
