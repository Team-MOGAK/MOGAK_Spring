package com.mogak.spring.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
@Profile("local")
@Configuration
@ConditionalOnProperty(prefix = "feature.redis", name = "enabled", havingValue = "true")
public class EmbeddedRedisConfig {

    private static final String REDIS_PING_COMMAND = "*1\r\n$4\r\nPING\r\n";
    private static final String REDIS_PONG_PREFIX = "+PONG";

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        try {
            if (isPortAvailable(redisPort)) {
                redisServer = new RedisServer(redisPort);
                redisServer.start();
                log.info("Embedded Redis started on port {}", redisPort);
                return;
            }

            if (isRedisResponsive(redisPort)) {
                log.info("Redis is already reachable on port {}. Embedded Redis startup skipped.", redisPort);
                return;
            }

            log.warn("Port {} is occupied by a non-Redis process. Embedded Redis startup skipped.", redisPort);
        } catch (Exception e) {
            log.warn("Embedded Redis start skipped", e);
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            try {
                redisServer.stop();
            } catch (IOException e) {
                log.warn("Embedded Redis stop skipped", e);
            }
        }
    }

    private boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket()) {
            socket.setReuseAddress(false);
            socket.bind(new InetSocketAddress("127.0.0.1", port));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isRedisResponsive(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), 200);
            socket.setSoTimeout(200);
            socket.getOutputStream().write(REDIS_PING_COMMAND.getBytes(StandardCharsets.US_ASCII));
            socket.getOutputStream().flush();

            byte[] response = socket.getInputStream().readNBytes(16);
            return new String(response, StandardCharsets.US_ASCII).startsWith(REDIS_PONG_PREFIX);
        } catch (IOException e) {
            return false;
        }
    }
}
