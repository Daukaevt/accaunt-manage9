package com.wixsite.mupbam1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AccauntManag9ClientServiceApplication {

	public static void main(String[] args) {
		 Dotenv dotenv = Dotenv.load(); // загружает .env
	        //System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
	        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
	        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
	        System.setProperty("DB_URL", dotenv.get("DB_URL"));
		SpringApplication.run(AccauntManag9ClientServiceApplication.class, args);
	}

}
