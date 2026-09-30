package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health Check & System Verification Controller.
 * Provides endpoints for checking service availability and connectivity.
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "Endpoints for verifying service health and connectivity")
public class HealthController {

    @Value("${spring.application.name:jewelry-store-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping
    @Operation(summary = "Check API health status", description = "Returns system information and health indicator")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> healthInfo = new LinkedHashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("application", applicationName);
        healthInfo.put("environment", activeProfile);
        healthInfo.put("version", "1.0.0-SNAPSHOT");
        healthInfo.put("serverTime", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success("System is operating normally", healthInfo));
    }

    @GetMapping("/ping")
    @Operation(summary = "Simple ping endpoint", description = "Lightweight ping test returning pong")
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(ApiResponse.success("Ping successful", "pong"));
    }
}
