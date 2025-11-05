package com.testplan.generator;

import com.testplan.model.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Enhanced test plan generator that can use AI for intelligent test case generation
 */
public class AITestPlanGenerator {
    private AITestCaseGenerator aiTestCaseGenerator;
    private TestPlanGenerator baseGenerator;
    private boolean useAI;
    
    public AITestPlanGenerator(boolean useAI) {
        this.useAI = useAI;
        this.baseGenerator = new TestPlanGenerator();
        if (useAI) {
            this.aiTestCaseGenerator = new AITestCaseGenerator();
        }
    }
    
    public TestPlan generateTestPlan(String projectName, String version,
                                   List<Requirement> requirements, 
                                   List<DesignComponent> components) {
        // Start with base test plan
        TestPlan testPlan = baseGenerator.generateTestPlan(projectName, version, requirements, components);
        
        // Update description and creator if using AI
        if (useAI) {
            testPlan.setDescription("AI-powered comprehensive test plan generated from requirements and design documents");
            testPlan.setCreatedBy("AI Test Plan Generator");
            
            // Enhance objectives with AI-specific ones
            List<String> objectives = new ArrayList<>(testPlan.getObjectives());
            objectives.add("Leverage AI analysis for comprehensive test coverage");
            objectives.add("Ensure intelligent test case generation covers edge cases");
            objectives.add("Validate system behavior through AI-driven test scenarios");
            testPlan.setObjectives(objectives);
        }
        
        // Generate test cases - use AI if available
        List<TestCase> testCases;
        if (useAI && aiTestCaseGenerator != null) {
            String projectContext = buildProjectContext(projectName, testPlan.getDescription(), requirements, components);
            testCases = aiTestCaseGenerator.generateAITestCases(requirements, components, projectContext);
        } else {
            TestCaseGenerator fallbackGenerator = new TestCaseGenerator();
            testCases = fallbackGenerator.generateTestCases(requirements, components);
        }
        
        testPlan.setTestCases(testCases);
        return testPlan;
    }
    
    private String buildProjectContext(String projectName, String description, 
                                     List<Requirement> requirements, 
                                     List<DesignComponent> components) {
        StringBuilder context = new StringBuilder();
        context.append("Project: ").append(projectName).append("\n");
        context.append("Description: ").append(description).append("\n");
        context.append("Total Requirements: ").append(requirements.size()).append("\n");
        context.append("Total Components: ").append(components.size()).append("\n");
        
        // Add requirement categories
        long functionalReqs = requirements.stream().filter(r -> "Functional".equals(r.getCategory())).count();
        long performanceReqs = requirements.stream().filter(r -> "Performance".equals(r.getCategory())).count();
        long securityReqs = requirements.stream().filter(r -> "Security".equals(r.getCategory())).count();
        
        context.append("Functional Requirements: ").append(functionalReqs).append("\n");
        context.append("Performance Requirements: ").append(performanceReqs).append("\n");
        context.append("Security Requirements: ").append(securityReqs).append("\n");
        
        // Add component types
        long apiComponents = components.stream().filter(c -> "API".equals(c.getType())).count();
        long uiComponents = components.stream().filter(c -> "UI".equals(c.getType())).count();
        long serviceComponents = components.stream().filter(c -> "Service".equals(c.getType())).count();
        
        context.append("API Components: ").append(apiComponents).append("\n");
        context.append("UI Components: ").append(uiComponents).append("\n");
        context.append("Service Components: ").append(serviceComponents).append("\n");
        
        return context.toString();
    }
    

}