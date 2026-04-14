package com.mogak.spring.security;

import com.mogak.spring.exception.ErrorResponse;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorCode errorCode = resolveErrorCode(authException);
        writeError(response, ErrorResponse.of(errorCode));
    }

    private ErrorCode resolveErrorCode(AuthenticationException authException) {
        if (authException instanceof JwtAuthenticationException jwtAuthenticationException) {
            return jwtAuthenticationException.getErrorCode();
        }
        if (authException instanceof BadCredentialsException) {
            return ErrorCode.WRONG_TOKEN;
        }
        return ErrorCode.EMPTY_TOKEN;
    }

    private void writeError(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(errorResponse.convertToJson());
    }
}
