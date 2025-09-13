package com.wixsite.mupbam1.client.controllers;

import com.wixsite.mupbam1.client.model.Picture;
import com.wixsite.mupbam1.client.services.PictureClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
public class PictureController {

    private final PictureClientService pictureClientService;

    @GetMapping
    public List<Picture> getPictures(Authentication authentication,
                                     @RequestHeader("Authorization") String authHeader) {
        String username = authentication.getName();
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return pictureClientService.getPicturesByOwner(username, token); 
    }
}
