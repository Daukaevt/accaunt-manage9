package com.wixsite.mupbam1.client.controllers;

import com.wixsite.mupbam1.client.model.Picture;
import com.wixsite.mupbam1.client.services.PictureClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
public class PictureController {

    private final PictureClientService pictureClientService;

    @GetMapping
    public List<Picture> getPictures(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
    	System.out.println("/PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPIIIIIIIIIIIIIIIIIIIIIIIIIICCCCCCCCCCCCCCCCCC");
        // Берём username из токена
        String username = authentication.getName();

        // Извлекаем JWT из заголовка Authorization
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        System.out.println("Calling /pictures with token: " + token);

        return pictureClientService.getPicturesByOwner(username, token);
    }

    @GetMapping("/test1")
    public Map<String, Object> test(
            Authentication authentication,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
    	System.out.println("ТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕССССССССССССССССССССССССССТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТТ");

        // Логирование заголовка Authorization
        System.out.println("Authorization header: " + authHeader);

        Map<String, Object> result = new HashMap<>();
        result.put("authentication", authentication);

        if (authentication != null) {
            result.put("name", authentication.getName());
            result.put("roles", authentication.getAuthorities());
        }

        // Логирование состояния authentication
        System.out.println("Authentication object: " + authentication);

        return result;
    }

}
