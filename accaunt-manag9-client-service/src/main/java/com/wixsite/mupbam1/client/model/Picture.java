package com.wixsite.mupbam1.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture {
    private Integer id;          // id — в JSON числовой
    private String ownerKey;
    private String description;  // вместо name
    private String url;
}

