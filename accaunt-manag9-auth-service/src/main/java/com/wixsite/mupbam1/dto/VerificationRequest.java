package com.wixsite.mupbam1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerificationRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Verification code is required")
    private String code;
}

