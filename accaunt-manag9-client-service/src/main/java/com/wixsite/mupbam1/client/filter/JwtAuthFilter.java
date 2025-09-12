package com.wixsite.mupbam1.client.filter;

import com.wixsite.mupbam1.client.services.UserService;
import com.wixsite.mupbam1.client.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

	

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            log.debug("JwtAuthFilter - Extracted token: {}", token);

            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.getClaims(token);
                    String username = claims.getSubject();
                    String role = claims.get("role", String.class);

                    log.info("JwtAuthFilter - Username: {}, Role: {}", username, role);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        var userDetails = userService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                                );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.debug("JwtAuthFilter - Authentication set for user: {}", username);
                    }
                }
            } catch (Exception e) {
                log.warn("JwtAuthFilter - Token validation failed: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
