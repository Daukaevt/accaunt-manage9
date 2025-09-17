package com.wixsite.mupbam1.service;

import com.wixsite.mupbam1.exceptions.InvalidVerificationCodeException;
import com.wixsite.mupbam1.exceptions.VerificationCodeAlreadyUsedException;
import com.wixsite.mupbam1.exceptions.VerificationCodeExpiredException;
import com.wixsite.mupbam1.model.VerificationCode;
import com.wixsite.mupbam1.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    public void saveCode(String username, String code) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUsername(username.toLowerCase());
        verificationCode.setCode(code);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setUsed(false);

        verificationCodeRepository.save(verificationCode);
    }

    public void verifyCode(String username, String code) {
        VerificationCode vc = verificationCodeRepository
                .findTopByUsernameOrderByCreatedAtDesc(username.toLowerCase())
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid verification code"));

        if (vc.isUsed()) {
            throw new VerificationCodeAlreadyUsedException("Verification code already used");
        }

        if (!vc.getCode().equals(code)) {
            throw new InvalidVerificationCodeException("Invalid verification code");
        }

        if (vc.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new VerificationCodeExpiredException("Verification code expired");
        }

        // Код успешен — помечаем как использованный
        vc.setUsed(true);
        verificationCodeRepository.save(vc);
    }
}
