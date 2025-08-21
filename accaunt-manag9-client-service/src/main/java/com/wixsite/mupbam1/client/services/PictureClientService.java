package com.wixsite.mupbam1.client.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.wixsite.mupbam1.client.model.Picture;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PictureClientService {

    private final WebClient.Builder webClientBuilder;

    public List<Picture> getPicturesByOwner(String ownerKey, String token) {
        String url = "http://gateway/resource-service/resource/pictures/owner?ownerKey=" + ownerKey;

        Picture[] pictures = webClientBuilder.build()
                .get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Picture[].class)
                .block();

        return pictures != null ? Arrays.asList(pictures) : List.of();
    }
}
