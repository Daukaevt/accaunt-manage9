package com.wixsite.mupbam1.dto;

import lombok.Data;

@Data
public class VerifyRequest {
    private String username;
    private String code;
}
