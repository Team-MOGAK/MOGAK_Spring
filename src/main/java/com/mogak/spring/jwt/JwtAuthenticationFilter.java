package com.mogak.spring.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ProviderManager authenticationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final List<String> skipPathPatterns;

    public JwtAuthenticationFilter(AuthenticationProvider authenticationProvider,
                                   AuthenticationEntryPoint authenticationEntryPoint,
                                   List<String> skipPathPatterns) {
        this.authenticationManager = new ProviderManager(List.of(authenticationProvider));
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.skipPathPatterns = List.copyOf(skipPathPatterns);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        return skipPathPatterns.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = resolveBearerToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            JwtAuthenticationToken authenticationRequest = new JwtAuthenticationToken(token);
            authenticationRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            Authentication authentication = authenticationManager.authenticate(authenticationRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, exception);
        }
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String header = request.getHeader(JwtTokenProvider.access_header);
        if (header == null || header.isBlank()) {
            return null;
        }
        if (!header.startsWith("Bearer ")) {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid authorization header");
        }
        String token = header.substring("Bearer ".length()).trim();
        return token.isEmpty() ? null : token;
    }
}
