package com.mogak.spring.config;

import com.mogak.spring.login.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
                .logout(Customizer.withDefaults()) //로그아웃은 기본 설정으로(/logout으로 인증해제)
                .cors().and()
                .authorizeRequests()//권한필요한 부분
                .antMatchers("**").permitAll() // 우선 모든 권한 허용 - 로그인 안해도 모든 접근 가능
//                .antMatchers(HttpMethod.POST,"/api/v1/**").authenticated()
                // 모든 post 요청을 인증된 사용자인지 순서 중요. authenticated 🡪 인증된 사용자인지 확인
                // .antMatchers("/api/**").authenticated() // 다른 api는 인증 필요
//                .oauth2Login()
//                .userInfoEndpoint()
//                .userService(oAuthService)
//                .and()
//                .successHandler(authenticationSuccessHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 사용하는 경우 사용
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                //UserNamePasswordAuthenticationFilter 적용하기 전에 JWTTokenFilter를 적용 하라는 뜻.
                .build();
    }



}
