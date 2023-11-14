package com.mogak.spring.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import javax.websocket.server.ServerApplicationConfig;

@Configuration
@EnableFeignClients(basePackageClasses = ServerApplicationConfig.class)
public class FeignClientConfig {
}
