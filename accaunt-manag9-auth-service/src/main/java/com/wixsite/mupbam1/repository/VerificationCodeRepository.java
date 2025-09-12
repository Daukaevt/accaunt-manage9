package com.wixsite.mupbam1.repository;

import com.wixsite.mupbam1.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findTopByUsernameOrderByCreatedAtDesc(String username);
}

