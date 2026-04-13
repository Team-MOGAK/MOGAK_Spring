package com.mogak.spring.config;

import com.mogak.spring.jwt.JwtTokenFilter;
import com.mogak.spring.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private final JwtTokenFilter jwtTokenFilter;

    //    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
////        return httpSecurity
//                .httpBasic().disable()
//                .cors().disable()
//                .csrf().disable()
//                .formLogin().disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 사용하는 경우 사용
//                .and()
//                .authorizeRequests()
//                .antMatchers("/","/swagger-ui/index.html","/swagger-ui.html",
//                        "/swagger-ui/**", "/v3/api-docs", "/swagger-resources/**",
//                        "/webjars/**","/api-docs/**",
//                        "/api/auth/login","/api/auth/logout","/api/auth/refresh",
//                        "/api/users/nickname/verify","/api/users/join").permitAll()
//                .antMatchers("/api/**").hasRole("USER")
////                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                //UserNamePasswordAuthenticationFilter 적용하기 전에 JWTTokenFilter를 적용 하라는 뜻.
//                .build();
//    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

}
