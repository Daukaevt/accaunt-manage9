package com.wixsite.mupbam1.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String role;
    private String email; // <-- добавили
}
