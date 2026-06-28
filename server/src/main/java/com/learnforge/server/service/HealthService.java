package com.learnforge.server.service;

import com.learnforge.server.config.AppProperties;
import com.learnforge.server.dto.HealthResponse;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final AppProperties appProperties;
    private final String activeProfile;

    public HealthService(AppProperties appProperties,
                         @Value("${spring.profiles.active:default}") String activeProfile) {
        this.appProperties = appProperties;
        this.activeProfile = activeProfile;
    }

    public HealthResponse getHealth() {
        return new HealthResponse(
                "UP",
                appProperties.getName(),
                activeProfile,
                Instant.now()
        );
    }
}
