package com.wixsite.mupbam1.service;

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
        verificationCode.setUsername(username);
        verificationCode.setCode(code);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setUsed(false);

        verificationCodeRepository.save(verificationCode);
    }

    public boolean verifyCode(String username, String code) {
        return verificationCodeRepository.findTopByUsernameOrderByCreatedAtDesc(username)
                .filter(vc -> !vc.isUsed())                                 // ещё не использован
                .filter(vc -> vc.getCode().equals(code))                    // совпадает код
                .filter(vc -> vc.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) // не старше 5 мин
                .map(vc -> {
                    vc.setUsed(true);
                    verificationCodeRepository.save(vc);                   // помечаем использованным
                    return true;
                })
                .orElse(false);
    }
}
