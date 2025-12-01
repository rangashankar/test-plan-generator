package com.testplan.publisher;

import java.util.ArrayList;
import java.util.List;

public class PublishResult {
    private final boolean success;
    private final String message;
    private final List<String> createdItems;
    private final List<String> warnings;
    private final String payloadPath;

    public PublishResult(boolean success, String message, List<String> createdItems, List<String> warnings, String payloadPath) {
        this.success = success;
        this.message = message;
        this.createdItems = createdItems != null ? createdItems : new ArrayList<>();
        this.warnings = warnings != null ? warnings : new ArrayList<>();
        this.payloadPath = payloadPath;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getCreatedItems() {
        return createdItems;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public String getPayloadPath() {
        return payloadPath;
    }

    public static PublishResult success(String message, List<String> createdItems, List<String> warnings, String payloadPath) {
        return new PublishResult(true, message, createdItems, warnings, payloadPath);
    }

    public static PublishResult failure(String message, List<String> warnings) {
        return new PublishResult(false, message, new ArrayList<>(), warnings, null);
    }
}
