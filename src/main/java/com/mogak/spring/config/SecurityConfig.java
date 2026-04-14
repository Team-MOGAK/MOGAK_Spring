package com.mogak.spring.config;

import com.mogak.spring.jwt.JwtAuthenticationFilter;
import com.mogak.spring.jwt.JwtAuthenticationProvider;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.security.ApiAccessDeniedHandler;
import com.mogak.spring.security.ApiAuthenticationEntryPoint;
import com.mogak.spring.security.SecurityAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    private static final List<String> CORS_ALLOWED_METHODS = List.of(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH"
    );

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
    private final ApiAccessDeniedHandler apiAccessDeniedHandler;

    @Value("${server.domain}")
    private String domain;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(apiAuthenticationEntryPoint)
                        .accessDeniedHandler(apiAccessDeniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_API_PATTERNS.toArray(String[]::new)).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/join")
                        .hasAuthority(SecurityAuthority.PENDING.getAuthority())
                        .requestMatchers("/api/auth/logout")
                        .authenticated()
                        .requestMatchers("/api/**")
                        .hasAnyAuthority(
                                SecurityAuthority.USER.getAuthority(),
                                SecurityAuthority.ADMIN.getAuthority())
                        .anyRequest().permitAll())
                .addFilterBefore(
                        new JwtAuthenticationFilter(
                                jwtAuthenticationProvider,
                                apiAuthenticationEntryPoint,
                                PUBLIC_API_PATTERNS),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of(domain));
        corsConfiguration.setAllowedMethods(CORS_ALLOWED_METHODS);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", corsConfiguration);
        return source;
    }

}
