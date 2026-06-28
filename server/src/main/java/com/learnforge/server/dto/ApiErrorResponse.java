package com.learnforge.server.dto;

import java.time.Instant;
import java.util.List;

public class ApiErrorResponse {
    private final String path;
    private final int status;
    private final String error;
    private final String message;
    private final List<String> details;
    private final Instant timestamp;

    public ApiErrorResponse(String path, int status, String error, String message, List<String> details, Instant timestamp) {
        this.path = path;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
