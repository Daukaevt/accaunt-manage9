package com.wixsite.mupbam1.client.controllers;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.wixsite.mupbam1.client.model.Picture;
import com.wixsite.mupbam1.client.services.PictureClientService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
public class PictureController {

    private final PictureClientService pictureClientService;

    @GetMapping
    public List<Picture> getPictures(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // убрать "Bearer "
        return pictureClientService.getPicturesByOwner("mupbam1@gmail.com", token);
    }

    @GetMapping("/test")
    public String test(Authentication authentication) {
        return "Hello, " + authentication.getName() +
               " | Roles: " + authentication.getAuthorities();
    }
}
