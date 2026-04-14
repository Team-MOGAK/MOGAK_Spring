package com.mogak.spring.jwt;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        writeError(response, ErrorResponse.of(ErrorCode.INVALID_PERMISSION));
    }

    private void writeError(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(errorResponse.convertToJson());
    }
}
