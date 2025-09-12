package com.wixsite.mupbam1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime createdAt;  // используем LocalDateTime вместо Date

    @Column(nullable = false)
    private boolean isUsed = false;

    private static final long CODE_LIFETIME_MINUTES = 5; // 5 минут

    // Проверка истечения срока жизни кода
    public boolean isExpired() {
        return createdAt.isBefore(LocalDateTime.now().minusMinutes(CODE_LIFETIME_MINUTES));
    }

    // Проверка валидности кода
    public boolean isValid(String providedCode) {
        return !isExpired() && !isUsed && code.equals(providedCode);
    }

    // Пометить код как использованный
    public void markAsUsed() {
        this.isUsed = true;
    }
}
