package com.testplan.publisher;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.testplan.model.TestCase;
import com.testplan.model.TestPlan;
import com.testplan.model.TestStep;

import java.util.ArrayList;
import java.util.List;

public class TestRailPublisher extends AbstractPublisher {

    @Override
    public PublishResult publish(TestPlan testPlan, PublishConfig config) throws Exception {
        List<String> warnings = new ArrayList<>();
        if (config == null || config.getTarget() != PublishTarget.TESTRAIL) {
            return PublishResult.failure("TestRail configuration missing", warnings);
        }
        if (isBlank(config.getBaseUrl()) || isBlank(config.getProjectKey())) {
            warnings.add("Base URL and project key are required for TestRail publishing.");
            return PublishResult.failure("Missing TestRail configuration", warnings);
        }
        if (!config.isDryRun() && isBlank(config.getApiToken())) {
            warnings.add("API token is empty; falling back to dry-run.");
            return PublishResult.failure("API token required for live TestRail publish", warnings);
        }

        ObjectNode payload = buildPayload(testPlan, config);
        String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload);

        if (config.isDryRun()) {
            warnings.add("Dry-run enabled. No data sent to TestRail.");
            return writeDryRunPayload("testrail", json, warnings, config);
        }

        return postJson(config.getBaseUrl(), config.getApiToken(), json, "TestRail", warnings);
    }

    private ObjectNode buildPayload(TestPlan testPlan, PublishConfig config) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("projectId", config.getProjectKey());
        root.put("runName", config.getTestRunName());
        root.put("suiteName", safe(testPlan.getTitle()));
        root.put("description", safe(testPlan.getDescription()));
        root.put("version", safe(testPlan.getVersion()));
        root.put("attachExport", config.isAttachExport());
        if (config.getExportPath() != null) {
            root.put("exportPath", config.getExportPath());
        }

        ArrayNode sections = root.putArray("sections");
        ObjectNode defaultSection = sections.addObject();
        defaultSection.put("name", "Generated Tests");
        ArrayNode cases = defaultSection.putArray("cases");

        for (TestCase testCase : testPlan.getTestCases()) {
            ObjectNode c = cases.addObject();
            c.put("title", safe(testCase.getTitle()));
            c.put("priority", safe(testCase.getPriority()));
            c.put("type", safe(testCase.getTestType()));
            c.put("estimate", safe(testCase.getEstimatedTime()));
            c.put("refs", String.join(",", testCase.getRelatedRequirements()));
            c.put("template", "Test Plan Generator");
            if (testCase.getObjective() != null) {
                c.put("objective", testCase.getObjective());
            }
            if (testCase.getExpectedResult() != null) {
                c.put("expected", testCase.getExpectedResult());
            }

            ArrayNode steps = c.putArray("steps");
            if (testCase.getTestSteps() != null) {
                for (TestStep step : testCase.getTestSteps()) {
                    ObjectNode s = steps.addObject();
                    s.put("content", safe(step.getAction()));
                    s.put("expected", safe(step.getExpectedResult()));
                    s.put("data", safe(step.getTestData()));
                }
            }
        }
        return root;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String getName() {
        return "TestRail";
    }
}
