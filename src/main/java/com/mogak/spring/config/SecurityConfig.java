package com.mogak.spring.config;

import com.mogak.spring.login.JwtTokenFilter;
import com.mogak.spring.login.JwtTokenHandler;
import com.mogak.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("**").permitAll() // 우선 모든 권한 허용
//                .antMatchers(HttpMethod.POST,"/api/v1/**").authenticated()
                // 모든 post 요청을 인증된 사용자인지 순서 중요. authenticated 🡪 인증된 사용자인지 확인
                // .antMatchers("/api/**").authenticated() // 다른 api는 인증 필요

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 사용하는 경우 사용
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                //UserNamePasswordAuthenticationFilter 적용하기 전에 JWTTokenFilter를 적용 하라는 뜻.
                .build();
    }
}
