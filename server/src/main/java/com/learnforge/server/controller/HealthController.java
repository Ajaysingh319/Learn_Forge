package com.learnforge.server.controller;

import com.learnforge.server.dto.HealthResponse;
import com.learnforge.server.service.HealthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("${app.api-base-path:/api}")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return healthService.getHealth();
    }
}
