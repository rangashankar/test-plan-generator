package com.testplan.publisher;

/**
 * Supported external publishing targets.
 */
public enum PublishTarget {
    JIRA_XRAY,
    TESTRAIL;

    public static PublishTarget fromString(String raw) {
        if (raw == null) {
            return null;
        }
        switch (raw.trim().toLowerCase()) {
            case "jira":
            case "xray":
            case "jira-xray":
            case "jira_xray":
                return JIRA_XRAY;
            case "testrail":
            case "test-rail":
            case "test_rail":
                return TESTRAIL;
            default:
                return null;
        }
    }
}
