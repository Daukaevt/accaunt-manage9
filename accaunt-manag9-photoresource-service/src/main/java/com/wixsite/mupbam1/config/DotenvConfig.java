package com.wixsite.mupbam1.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./")  // Корень проекта
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }
}
