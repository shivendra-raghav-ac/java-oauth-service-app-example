package com.example.oktausersvc.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Liveness probe
@RestController
public class HealthController {
    @GetMapping("/healthy")
    public Map<String, Object> health() {
        return Map.of("status", "UP");
    }
}
