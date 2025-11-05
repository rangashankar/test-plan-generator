package com.testplan.model;

import java.util.List;
import java.util.ArrayList;

public class TestStrategy {
    private List<String> testTypes;
    private List<String> testLevels;
    private String approach;
    private List<String> tools;
    private List<String> environments;
    private String riskAssessment;
    
    public TestStrategy() {
        this.testTypes = new ArrayList<>();
        this.testLevels = new ArrayList<>();
        this.tools = new ArrayList<>();
        this.environments = new ArrayList<>();
    }
    
    // Getters and Setters
    public List<String> getTestTypes() { return testTypes; }
    public void setTestTypes(List<String> testTypes) { this.testTypes = testTypes; }
    
    public List<String> getTestLevels() { return testLevels; }
    public void setTestLevels(List<String> testLevels) { this.testLevels = testLevels; }
    
    public String getApproach() { return approach; }
    public void setApproach(String approach) { this.approach = approach; }
    
    public List<String> getTools() { return tools; }
    public void setTools(List<String> tools) { this.tools = tools; }
    
    public List<String> getEnvironments() { return environments; }
    public void setEnvironments(List<String> environments) { this.environments = environments; }
    
    public String getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(String riskAssessment) { this.riskAssessment = riskAssessment; }
}