package com.mogak.spring.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityProbeController {

    @GetMapping("/internal/security-probe")
    String internalProbe() {
        return "ok";
    }

    @GetMapping("/actuator/health")
    String health() {
        return "UP";
    }

    @GetMapping("/actuator/info")
    String info() {
        return "info";
    }
}
