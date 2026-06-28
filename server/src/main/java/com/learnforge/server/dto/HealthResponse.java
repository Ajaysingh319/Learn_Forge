package com.learnforge.server.dto;

import java.time.Instant;

public class HealthResponse {
    private final String status;
    private final String service;
    private final String environment;
    private final Instant timestamp;

    public HealthResponse(String status, String service, String environment, Instant timestamp) {
        this.status = status;
        this.service = service;
        this.environment = environment;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getService() {
        return service;
    }

    public String getEnvironment() {
        return environment;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
