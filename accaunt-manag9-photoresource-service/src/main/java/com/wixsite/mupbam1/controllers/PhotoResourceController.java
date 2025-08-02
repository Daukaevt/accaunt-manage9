package com.wixsite.mupbam1.controllers;

import com.wixsite.mupbam1.model.Picture;
import com.wixsite.mupbam1.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class PhotoResourceController {

    private final PictureService pictureService;

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is public!";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "This is protected resource!";
    }

    @GetMapping("/pictures")
    public List<Picture> getAllPictures() {
        return pictureService.findAll();
    }

    @GetMapping("/pictures/owner")
    public List<Picture> getPicturesByOwnerKey(@RequestParam String ownerKey) {
        return pictureService.findByOwnerKey(ownerKey);
    }

}

