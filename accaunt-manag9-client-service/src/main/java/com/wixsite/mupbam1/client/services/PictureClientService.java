package com.wixsite.mupbam1.client.services;
import com.wixsite.mupbam1.client.model.Picture; 
import lombok.RequiredArgsConstructor; 
import org.springframework.stereotype.Service; 
import org.springframework.web.reactive.function.client.WebClient; 
import java.util.List;
@Service 
@RequiredArgsConstructor 
public class PictureClientService { 
	private final WebClient.Builder webClientBuilder;
	public List<Picture> getPicturesByOwner(String username, String token) {
		System.out.println("Calling /pictures for user: " + username);
		return webClientBuilder.build()
				.get() // Обращаемся через имя сервиса, которое зарегистрировано в Eureka 
				.uri("http://ACCAUNT-MANAG9-PHOTORESOURCE-SERVICE/pictures?owner=" + username)
				.header("Authorization", "Bearer " + token)
				.retrieve() .bodyToFlux(Picture.class)
				.collectList()
				.block();
	}
}
	
