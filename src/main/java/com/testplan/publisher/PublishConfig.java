package com.testplan.publisher;

/**
 * Configuration for publishing to an external test management system.
 */
public class PublishConfig {
    private final PublishTarget target;
    private final String baseUrl;
    private final String projectKey;
    private final String apiToken;
    private final String testRunName;
    private final boolean dryRun;
    private final boolean attachExport;
    private final String exportPath;

    private PublishConfig(Builder builder) {
        this.target = builder.target;
        this.baseUrl = builder.baseUrl;
        this.projectKey = builder.projectKey;
        this.apiToken = builder.apiToken;
        this.testRunName = builder.testRunName;
        this.dryRun = builder.dryRun;
        this.attachExport = builder.attachExport;
        this.exportPath = builder.exportPath;
    }

    public PublishTarget getTarget() {
        return target;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getTestRunName() {
        return testRunName;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public boolean isAttachExport() {
        return attachExport;
    }

    public String getExportPath() {
        return exportPath;
    }

    public PublishConfig withExportPath(String path) {
        return new Builder(this).exportPath(path).build();
    }

    public static class Builder {
        private PublishTarget target;
        private String baseUrl;
        private String projectKey;
        private String apiToken;
        private String testRunName = "Automated Test Run";
        private boolean dryRun = true;
        private boolean attachExport = true;
        private String exportPath;

        public Builder() {}

        public Builder(PublishConfig base) {
            this.target = base.target;
            this.baseUrl = base.baseUrl;
            this.projectKey = base.projectKey;
            this.apiToken = base.apiToken;
            this.testRunName = base.testRunName;
            this.dryRun = base.dryRun;
            this.attachExport = base.attachExport;
            this.exportPath = base.exportPath;
        }

        public Builder target(PublishTarget target) {
            this.target = target;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder projectKey(String projectKey) {
            this.projectKey = projectKey;
            return this;
        }

        public Builder apiToken(String apiToken) {
            this.apiToken = apiToken;
            return this;
        }

        public Builder testRunName(String testRunName) {
            if (testRunName != null && !testRunName.isBlank()) {
                this.testRunName = testRunName;
            }
            return this;
        }

        public Builder dryRun(boolean dryRun) {
            this.dryRun = dryRun;
            return this;
        }

        public Builder attachExport(boolean attachExport) {
            this.attachExport = attachExport;
            return this;
        }

        public Builder exportPath(String exportPath) {
            this.exportPath = exportPath;
            return this;
        }

        public PublishConfig build() {
            return new PublishConfig(this);
        }
    }
}
