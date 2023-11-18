package com.mogak.spring.config;

import com.mogak.spring.Application;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = Application.class)
public class FeignClientConfig {
}
