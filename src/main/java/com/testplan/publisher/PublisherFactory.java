package com.testplan.publisher;

public class PublisherFactory {
    public static TestManagementPublisher create(PublishTarget target) {
        if (target == null) {
            return null;
        }
        switch (target) {
            case JIRA_XRAY:
                return new JiraXrayPublisher();
            case TESTRAIL:
                return new TestRailPublisher();
            default:
                return null;
        }
    }
}
