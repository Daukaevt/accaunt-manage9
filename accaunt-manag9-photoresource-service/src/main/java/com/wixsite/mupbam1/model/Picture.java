package com.wixsite.mupbam1.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pictures")
@Data
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(name = "owner_key")
    private String ownerKey;

    private String url;
}
