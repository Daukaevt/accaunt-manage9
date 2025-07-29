package com.wixsite.mupbam1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AccauntManag9AuthServiceApplication {

	public static void main(String[] args) {
		 // Загружаем переменные из .env в системные свойства
        Dotenv dotenv = Dotenv.load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		SpringApplication.run(AccauntManag9AuthServiceApplication.class, args);
	}

}
