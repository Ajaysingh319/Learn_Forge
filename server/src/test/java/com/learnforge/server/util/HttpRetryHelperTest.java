package com.learnforge.server.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class HttpRetryHelperTest {

    @Test
    void sendWithRetry_rejectsInvalidAttemptCount() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost")).GET().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> HttpRetryHelper.sendWithRetry(client, request, 0)
        );
    }

    @Test
    void sendWithRetry_retriesRetriableStatus() throws Exception {
        AtomicInteger attempts = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/retry", exchange -> {
            if (attempts.incrementAndGet() < 2) {
                exchange.sendResponseHeaders(503, -1);
                exchange.close();
                return;
            }

            byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(body);
            }
            exchange.close();
        });
        server.start();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + server.getAddress().getPort() + "/retry"))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpRetryHelper.sendWithRetry(client, request, 3);

            assertEquals(200, response.statusCode());
            assertEquals("ok", response.body());
            assertEquals(2, attempts.get());
        } finally {
            server.stop(0);
        }
    }
}
