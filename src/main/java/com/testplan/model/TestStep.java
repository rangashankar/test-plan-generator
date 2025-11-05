package com.testplan.model;

public class TestStep {
    private int stepNumber;
    private String action;
    private String expectedResult;
    private String testData;
    
    public TestStep() {}
    
    public TestStep(int stepNumber, String action, String expectedResult) {
        this.stepNumber = stepNumber;
        this.action = action;
        this.expectedResult = expectedResult;
    }
    
    // Getters and Setters
    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
    
    public String getTestData() { return testData; }
    public void setTestData(String testData) { this.testData = testData; }
    
    @Override
    public String toString() {
        return String.format("Step %d: %s -> %s", stepNumber, action, expectedResult);
    }
}