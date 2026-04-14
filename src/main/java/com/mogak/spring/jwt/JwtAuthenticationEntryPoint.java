package com.mogak.spring.jwt;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorCode errorCode = authException instanceof JwtAuthenticationException jwtAuthenticationException
                ? jwtAuthenticationException.getErrorCode()
                : authException instanceof BadCredentialsException
                ? ErrorCode.WRONG_TOKEN
                : ErrorCode.EMPTY_TOKEN;
        writeError(response, ErrorResponse.of(errorCode));
    }

    private void writeError(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(errorResponse.convertToJson());
    }
}
