package com.wixsite.mupbam1.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // логин

    @Column(nullable = false)
    private String password;

    private String role;

    @Column(unique = true, nullable = false)
    private String email; // <-- новое поле
}
