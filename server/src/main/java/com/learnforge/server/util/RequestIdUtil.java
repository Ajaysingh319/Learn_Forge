package com.learnforge.server.util;

import java.util.UUID;

public final class RequestIdUtil {
    private RequestIdUtil() {
    }

    public static String newRequestId() {
        return UUID.randomUUID().toString();
    }
}
