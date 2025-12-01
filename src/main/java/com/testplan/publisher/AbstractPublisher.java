package com.testplan.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared helpers for concrete publishers.
 */
abstract class AbstractPublisher implements TestManagementPublisher {
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected PublishResult writeDryRunPayload(String name, String payload, List<String> warnings, PublishConfig config) {
        try {
            Path dir = Path.of("target", "publish");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            String fileName = name.toLowerCase().replaceAll("\\s+", "-") + "-payload.json";
            Path out = dir.resolve(fileName);
            Files.writeString(out, payload, StandardCharsets.UTF_8);
            String message = "Dry-run only. Payload saved to " + out;
            return PublishResult.success(message, new ArrayList<>(), warnings, out.toString());
        } catch (IOException e) {
            List<String> warn = new ArrayList<>(warnings);
            warn.add("Could not write dry-run payload: " + e.getMessage());
            return PublishResult.failure("Failed to persist dry-run payload", warn);
        }
    }

    protected PublishResult postJson(String url, String token, String payload, String name, List<String> warnings) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload));
            if (token != null && !token.isBlank()) {
                builder.header("Authorization", "Bearer " + token);
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            boolean success = status >= 200 && status < 300;
            String message = name + " response: HTTP " + status;
            if (!success) {
                List<String> warn = new ArrayList<>(warnings);
                warn.add("Response body: " + response.body());
                return PublishResult.failure(message, warn);
            }
            List<String> created = new ArrayList<>();
            created.add("Remote response body: " + response.body());
            return PublishResult.success(message, created, warnings, null);
        } catch (Exception e) {
            List<String> warn = new ArrayList<>(warnings);
            warn.add("HTTP error: " + e.getMessage());
            return PublishResult.failure(name + " publish failed", warn);
        }
    }
}
