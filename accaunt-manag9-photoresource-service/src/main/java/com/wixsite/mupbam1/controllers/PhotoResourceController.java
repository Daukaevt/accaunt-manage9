package com.wixsite.mupbam1.controllers;

import com.wixsite.mupbam1.model.Picture;
import com.wixsite.mupbam1.service.PictureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
@Slf4j
public class PhotoResourceController {

    private final PictureService pictureService;

    @GetMapping("/test")
    public Map<String, String> testEndpoint() {
        return Map.of("message", "тест пройден!!!");
    }

    @GetMapping
    public List<Picture> getAllPictures() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null) ? auth.getName() : "anonymous";
        log.info("Запрос всех картинок пользователем: {}", user);
        return pictureService.findAll();
    }

    @GetMapping("/owner")
    public List<Picture> getPicturesByOwner(@RequestParam String ownerKey,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "24") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth != null) ? auth.getName() : "anonymous";
        log.info("Запрос картинок по ownerKey={} пользователем={} (page={}, size={})", ownerKey, user, page, size);
        return pictureService.getPicturesByOwner(ownerKey, page, size);
    }
}
