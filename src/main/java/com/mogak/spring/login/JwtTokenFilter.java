package com.mogak.spring.login;

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
        if (jwtToken != null) {
            jwtTokenHandler.validateToken(jwtToken);
//            if (jwtTokenHandler.validateToken(jwtToken)) {
//                throw new CommonException(ErrorCode.WRONG_TOKEN);
//            }
        }
        filterChain.doFilter(request, response);
    }

}
