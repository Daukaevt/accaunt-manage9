package com.wixsite.mupbam1.client.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                     .ignoreIfMissing() // если .env нет — не падать
                     .load();
    }
}

