package com.wixsite.mupbam1;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
public class AccauntManag9ApiGatewayApplication {

    public static void main(String[] args) {
        // Загружаем переменные из .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Кладем их в карту свойств
        Map<String, Object> props = new HashMap<>();
        String jwtSecret = dotenv.get("JWT_SECRET");
        if (jwtSecret != null) {
            // Ключ должен соответствовать ключу из @Value в фильтре
            props.put("JWT_SECRET", jwtSecret);
        }

        // Создаем SpringApplication и задаем свойства до запуска
        SpringApplication app = new SpringApplication(AccauntManag9ApiGatewayApplication.class);
        app.setDefaultProperties(props);

        // Запускаем приложение один раз с нужными свойствами
        app.run(args);
    }
}
