package com.wixsite.mupbam1.filter;

import com.wixsite.mupbam1.service.TokenStoreService;
import com.wixsite.mupbam1.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenStoreService tokenStoreService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Проверка в Redis (отозван или истёк TTL)
            if (!tokenStoreService.isTokenValid(token)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
                return;
            }

            try {
                // Проверка подписи и exp
                if (jwtUtil.validateToken(token)) {
                    var claims = jwtUtil.getClaims(token);
                    String username = claims.getSubject();
                    String role = (String) claims.get("role");

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            } catch (ExpiredJwtException e) {
                // JWT истёк по claim exp
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            } catch (Exception e) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT: " + e.getMessage());
                return;
            }
        } else {
            // Нет токена для защищённого ресурса
            if (requiresAuth(request)) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied: no token provided");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getOutputStream().println("{ \"error\": \"" + status + "\", \"message\": \"" + message + "\" }");
    }

    private boolean requiresAuth(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Перечисляем открытые эндпоинты
        return !(path.startsWith("/auth/register")
                || path.startsWith("/auth/login")
                || path.startsWith("/auth/verify"));
    }
}
