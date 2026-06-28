package com.learnforge.server.util;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class HttpRetryHelper {

    private HttpRetryHelper() {
    }

    public static HttpResponse<String> sendWithRetry(
            HttpClient client,
            HttpRequest request,
            int maxAttempts
    ) throws IOException, InterruptedException {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }

        IOException lastIoException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (isRetriableStatus(response.statusCode()) && attempt < maxAttempts) {
                    sleepBackoff(attempt);
                    continue;
                }
                return response;
            } catch (IOException ex) {
                lastIoException = ex;
                if (attempt >= maxAttempts) {
                    throw ex;
                }
                sleepBackoff(attempt);
            }
        }

        if (lastIoException != null) {
            throw lastIoException;
        }
        throw new IOException("Request failed after retries");
    }

    private static boolean isRetriableStatus(int statusCode) {
        return statusCode == 429 || statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

    private static void sleepBackoff(int attempt) throws InterruptedException {
        long delayMs = 400L * attempt;
        Thread.sleep(delayMs);
    }
}
