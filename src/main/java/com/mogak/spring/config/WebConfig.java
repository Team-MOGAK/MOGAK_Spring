package com.mogak.spring.config;

import com.amazonaws.HttpMethod;
//import com.mogak.spring.jwt.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

//    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .exposedHeaders("Authorization","RefreshToken")
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PATCH.name()
                )
                .allowCredentials(true);
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtInterceptor)
//                .addPathPatterns("/api/**")
//                .excludePathPatterns(
//                        "/api/apple/login",
//                        "/api/apple/refresh",
//                        "/api/apple/logout",
//                        "/api/users/join",
//                        "/api/users/{nickname}/verify",
//                        "/swagger-ui/index.html"
//
//                );
//    }
}
