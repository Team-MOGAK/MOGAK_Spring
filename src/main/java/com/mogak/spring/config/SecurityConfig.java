package com.mogak.spring.config;

import com.mogak.spring.jwt.JwtAccessDeniedHandler;
import com.mogak.spring.jwt.JwtAuthenticationEntryPoint;
import com.mogak.spring.jwt.JwtAuthenticationFilter;
import com.mogak.spring.jwt.JwtAuthenticationProvider;
import com.mogak.spring.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final List<String> PUBLIC_API_PATTERNS = List.of(
            "/",
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api-docs/**",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/users/nickname/verify",
            "/api/users/login"
    );

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_API_PATTERNS.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/join")
                        .hasAuthority(JwtTokenProvider.ROLE_PENDING)
                        .requestMatchers("/api/auth/logout")
                        .authenticated()
                        .requestMatchers("/api/**")
                        .hasAnyAuthority(JwtTokenProvider.ROLE_USER, "ROLE_ADMIN")
                        .anyRequest().permitAll())
                .addFilterBefore(
                        new JwtAuthenticationFilter(
                                jwtAuthenticationProvider,
                                jwtAuthenticationEntryPoint,
                                PUBLIC_API_PATTERNS),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
