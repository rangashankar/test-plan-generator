package com.testplan.exporter;

import com.testplan.model.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class SimpleTextExporter implements TestPlanExporter {
    
    @Override
    public void export(TestPlan testPlan, File outputFile) throws Exception {
        try (FileWriter writer = new FileWriter(outputFile)) {
            
            // Test Plan Header
            writer.write("=====================================\n");
            writer.write("         TEST PLAN DOCUMENT\n");
            writer.write("=====================================\n\n");
            
            // Basic Information
            writer.write("Test Plan ID: " + testPlan.getId() + "\n");
            writer.write("Title: " + testPlan.getTitle() + "\n");
            writer.write("Version: " + testPlan.getVersion() + "\n");
            writer.write("Created By: " + testPlan.getCreatedBy() + "\n");
            writer.write("Created Date: " + testPlan.getCreatedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
            writer.write("Description: " + testPlan.getDescription() + "\n\n");
            
            // Objectives
            writer.write("TEST OBJECTIVES:\n");
            writer.write("================\n");
            for (String objective : testPlan.getObjectives()) {
                writer.write("• " + objective + "\n");
            }
            writer.write("\n");
            
            // Scope
            writer.write("TEST SCOPE:\n");
            writer.write("===========\n");
            for (String scope : testPlan.getScope()) {
                writer.write("• " + scope + "\n");
            }
            writer.write("\n");
            
            // Out of Scope
            writer.write("OUT OF SCOPE:\n");
            writer.write("=============\n");
            for (String outOfScope : testPlan.getOutOfScope()) {
                writer.write("• " + outOfScope + "\n");
            }
            writer.write("\n");
            
            // Test Strategy
            if (testPlan.getTestStrategy() != null) {
                writeTestStrategy(writer, testPlan.getTestStrategy());
            }
            
            // Test Cases
            writer.write("TEST CASES:\n");
            writer.write("===========\n\n");
            
            for (TestCase testCase : testPlan.getTestCases()) {
                writeTestCase(writer, testCase);
                writer.write("\n" + "=".repeat(80) + "\n\n");
            }
            
        } catch (IOException e) {
            throw new Exception("Failed to export test plan to text file: " + e.getMessage(), e);
        }
    }
    
    private void writeTestStrategy(FileWriter writer, TestStrategy strategy) throws IOException {
        writer.write("TEST STRATEGY:\n");
        writer.write("==============\n");
        
        writer.write("\nTest Types:\n");
        for (String testType : strategy.getTestTypes()) {
            writer.write("• " + testType + "\n");
        }
        
        writer.write("\nTest Levels:\n");
        for (String testLevel : strategy.getTestLevels()) {
            writer.write("• " + testLevel + "\n");
        }
        
        writer.write("\nApproach:\n");
        writer.write(strategy.getApproach() + "\n");
        
        writer.write("\nTest Tools:\n");
        for (String tool : strategy.getTools()) {
            writer.write("• " + tool + "\n");
        }
        
        writer.write("\nTest Environments:\n");
        for (String environment : strategy.getEnvironments()) {
            writer.write("• " + environment + "\n");
        }
        
        writer.write("\nRisk Assessment:\n");
        writer.write(strategy.getRiskAssessment() + "\n\n");
    }
    
    private void writeTestCase(FileWriter writer, TestCase testCase) throws IOException {
        writer.write("Test Case ID: " + testCase.getId() + "\n");
        writer.write("Title: " + testCase.getTitle() + "\n");
        writer.write("Description: " + testCase.getDescription() + "\n");
        writer.write("Type: " + testCase.getTestType() + "\n");
        writer.write("Priority: " + testCase.getPriority() + "\n");
        writer.write("Category: " + testCase.getCategory() + "\n");
        
        if (!testCase.getPreconditions().isEmpty()) {
            writer.write("Preconditions:\n");
            for (String precondition : testCase.getPreconditions()) {
                writer.write("• " + precondition + "\n");
            }
        }
        
        writer.write("\nTest Steps:\n");
        for (TestStep step : testCase.getTestSteps()) {
            writer.write(step.getStepNumber() + ". " + step.getAction() + "\n");
            writer.write("   Expected: " + step.getExpectedResult() + "\n");
        }
        
        writer.write("\nOverall Expected Result:\n");
        writer.write(testCase.getExpectedResult() + "\n");
        
        if (!testCase.getRelatedRequirements().isEmpty()) {
            writer.write("\nRelated Requirements: " + String.join(", ", testCase.getRelatedRequirements()) + "\n");
        }
        
        if (!testCase.getRelatedComponents().isEmpty()) {
            writer.write("Related Components: " + String.join(", ", testCase.getRelatedComponents()) + "\n");
        }
    }
    
    @Override
    public String getFileExtension() {
        return "txt";
    }
}