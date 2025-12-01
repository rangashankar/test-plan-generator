package com.testplan.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Minimal configuration holder for AI features (Bedrock by default).
 */
public class AIConfig {
    private final String provider;
    private final String modelId;
    private final String region;
    private final int maxRetries;
    private final long baseBackoffMs;

    private AIConfig(String provider, String modelId, String region, int maxRetries, long baseBackoffMs) {
        this.provider = provider;
        this.modelId = modelId;
        this.region = region;
        this.maxRetries = maxRetries;
        this.baseBackoffMs = baseBackoffMs;
    }

    public String getProvider() {
        return provider;
    }

    public String getModelId() {
        return modelId;
    }

    public String getRegion() {
        return region;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getBaseBackoffMs() {
        return baseBackoffMs;
    }

    public static AIConfig load() {
        Properties props = new Properties();
        try (InputStream in = AIConfig.class.getClassLoader().getResourceAsStream("ai.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
        }

        String provider = props.getProperty("ai.provider", "bedrock");
        String modelId = props.getProperty("ai.modelId", "anthropic.claude-3-sonnet-20240229-v1:0");
        String region = props.getProperty("ai.region", "us-east-1");
        int maxRetries = parseInt(props.getProperty("ai.maxRetries"), 2);
        long baseBackoffMs = parseLong(props.getProperty("ai.baseBackoffMs"), 600L);

        return new AIConfig(provider, modelId, region, maxRetries, baseBackoffMs);
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static long parseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
