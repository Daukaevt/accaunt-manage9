package com.wixsite.mupbam1.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
	
	


    @Value("${JWT_SECRET}")
    private String secret;
    
    @PostConstruct
    public void logSecret() {
        System.out.println("JWT_SECRET at startup гейт: " + secret);
        // Или через логгер:
        // Logger logger = LoggerFactory.getLogger(SecretLogger.class);
        // logger.info("JWT_SECRET at startup: {}", secret);
    }

    public JwtAuthFilter() {
        super(Config.class);
    }
    

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
        	System.out.println("JwtAuthFilter invoked for request: " + exchange.getRequest().getURI());
        	//System.out.println("Authorization header: " + authHeader);

            if (secret == null || secret.isEmpty()) {
                return onError(exchange, "JWT secret is not configured", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization header", HttpStatus.BAD_REQUEST);
            }

            String token = authHeader.substring(7); // "Bearer ".length()

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

                // Логируем полезную информацию (например, subject)
                String user = claims.getSubject();
                System.out.println("Authenticated user: " + user);

            } catch (Exception e) {
                System.out.println("Invalid token: " + e.getMessage());
                e.printStackTrace();
                return onError(exchange, "Invalid token: " + e.getMessage(), HttpStatus.FORBIDDEN);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        System.out.println("JWT Error: " + message);
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // можно расширить при необходимости
    }
}
