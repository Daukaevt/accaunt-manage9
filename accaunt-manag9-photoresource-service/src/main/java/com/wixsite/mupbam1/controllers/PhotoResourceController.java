package com.wixsite.mupbam1.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class PhotoResourceController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is public!";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "This is protected resource!";
    }
}
