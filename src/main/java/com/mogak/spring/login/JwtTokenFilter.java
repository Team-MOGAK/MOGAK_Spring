package com.mogak.spring.login;

import com.mogak.spring.exception.CommonException;
import com.mogak.spring.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenHandler jwtTokenHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtToken = request.getHeader(JwtTokenHandler.AUTHORIZATION);
        try {
            if (jwtToken != null) {
                jwtTokenHandler.validateToken(jwtToken);
            }
            filterChain.doFilter(request, response);
        } catch (CommonException e) {
            setErrorResponse(e, response);
        }
    }

    public void setErrorResponse(CommonException e, HttpServletResponse response) throws IOException {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(
                new ErrorResponse(
                        e.getHttpStatus().value(),
                        e.getCode(),
                        e.getMessage()
                ).convertToJson()
        );
    }

}
