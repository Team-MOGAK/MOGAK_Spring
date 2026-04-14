package com.mogak.spring.jwt;

import com.mogak.spring.exception.BaseException;
import com.mogak.spring.global.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        if (accessToken == null) {
            throw new BaseException(ErrorCode.EMPTY_TOKEN);
        }
        if (jwtTokenProvider.validateAccessToken(accessToken)) {
            setAuthentication(accessToken);
            log.info("인증 성공");
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Authentication 객체 생성
     */
    public void setAuthentication(String accessToken){
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String[] excludePath = {"/","/swagger-ui/**", "/v3/api-docs", "/swagger-resources/**",
                "/webjars/**", "/swagger-ui.html", "/swagger-ui/index.html","/api-docs/**",
                "/api/auth/login","/api/auth/refresh","/api/auth/logout",
                "/api/users/nickname/verify","/api/users/join"};
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }
}
