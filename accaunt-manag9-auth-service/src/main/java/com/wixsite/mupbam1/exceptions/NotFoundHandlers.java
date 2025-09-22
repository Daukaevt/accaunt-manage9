package com.wixsite.mupbam1.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NotFoundHandlers {

    public static class NotFoundEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             org.springframework.security.core.AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 вместо 401/403
        }
    }

    public static class NotFoundDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request,
                           HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 вместо 403
        }
    }
}
