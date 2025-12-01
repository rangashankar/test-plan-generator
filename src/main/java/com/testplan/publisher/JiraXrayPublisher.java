package com.testplan.publisher;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.testplan.model.TestCase;
import com.testplan.model.TestPlan;
import com.testplan.model.TestStep;

import java.util.ArrayList;
import java.util.List;

public class JiraXrayPublisher extends AbstractPublisher {

    @Override
    public PublishResult publish(TestPlan testPlan, PublishConfig config) throws Exception {
        List<String> warnings = new ArrayList<>();
        if (config == null || config.getTarget() != PublishTarget.JIRA_XRAY) {
            return PublishResult.failure("Jira/Xray configuration missing", warnings);
        }
        if (isBlank(config.getBaseUrl()) || isBlank(config.getProjectKey())) {
            warnings.add("Base URL and project key are required for Jira/Xray publishing.");
            return PublishResult.failure("Missing Jira/Xray configuration", warnings);
        }
        if (!config.isDryRun() && isBlank(config.getApiToken())) {
            warnings.add("API token is empty; falling back to dry-run.");
            return PublishResult.failure("API token required for live Jira/Xray publish", warnings);
        }

        ObjectNode payload = buildPayload(testPlan, config);
        String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(payload);

        if (config.isDryRun()) {
            warnings.add("Dry-run enabled. No data sent to Jira/Xray.");
            return writeDryRunPayload("jira-xray", json, warnings, config);
        }

        return postJson(config.getBaseUrl(), config.getApiToken(), json, "Jira/Xray", warnings);
    }

    private ObjectNode buildPayload(TestPlan testPlan, PublishConfig config) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("projectKey", config.getProjectKey());
        root.put("testPlanKey", safe(testPlan.getId()));
        root.put("testPlanSummary", safe(testPlan.getTitle()));
        root.put("testPlanDescription", safe(testPlan.getDescription()));
        root.put("version", safe(testPlan.getVersion()));
        root.put("testExecutionSummary", config.getTestRunName());
        root.put("attachExport", config.isAttachExport());
        if (config.getExportPath() != null) {
            root.put("exportPath", config.getExportPath());
        }

        ArrayNode tests = root.putArray("tests");
        for (TestCase testCase : testPlan.getTestCases()) {
            ObjectNode t = tests.addObject();
            t.put("key", safe(testCase.getId()));
            t.put("summary", safe(testCase.getTitle()));
            t.put("objective", safe(testCase.getObjective()));
            t.put("priority", safe(testCase.getPriority()));
            t.put("severity", safe(testCase.getSeverity()));
            t.put("type", safe(testCase.getTestType()));
            t.put("level", safe(testCase.getTestLevel()));
            t.put("status", safe(testCase.getStatus()));
            if (testCase.getRelatedRequirements() != null) {
                ArrayNode reqs = t.putArray("requirementKeys");
                for (String req : testCase.getRelatedRequirements()) {
                    reqs.add(req);
                }
            }
            if (testCase.getRelatedComponents() != null) {
                ArrayNode comps = t.putArray("components");
                for (String comp : testCase.getRelatedComponents()) {
                    comps.add(comp);
                }
            }
            ArrayNode steps = t.putArray("steps");
            if (testCase.getTestSteps() != null) {
                for (TestStep step : testCase.getTestSteps()) {
                    ObjectNode s = steps.addObject();
                    s.put("action", safe(step.getAction()));
                    s.put("data", safe(step.getTestData()));
                    s.put("result", safe(step.getExpectedResult()));
                }
            }
            if (testCase.getExpectedResult() != null) {
                t.put("overallExpectedResult", safe(testCase.getExpectedResult()));
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
        return "Jira/Xray";
    }
}
