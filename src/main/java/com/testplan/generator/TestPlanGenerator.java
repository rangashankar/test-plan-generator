package com.testplan.generator;

import com.testplan.model.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class TestPlanGenerator {
    
    private TestCaseGenerator testCaseGenerator;
    
    public TestPlanGenerator() {
        this.testCaseGenerator = new TestCaseGenerator();
    }
    
    /**
     * Generate a comprehensive test plan from requirements and design documents
     */
    public TestPlan generateTestPlan(String projectName, String version,
                                   List<Requirement> requirements, 
                                   List<DesignComponent> components) {
        
        TestPlan testPlan = new TestPlan();
        testPlan.setId("TP_" + projectName.replaceAll("\\s+", "_").toUpperCase());
        testPlan.setTitle("Test Plan for " + projectName);
        testPlan.setDescription("Comprehensive test plan generated from requirements and design documents");
        testPlan.setVersion(version);
        testPlan.setCreatedBy("TestPlan Generator");
        
        // Set objectives
        testPlan.setObjectives(generateObjectives(requirements, components));
        
        // Set scope
        testPlan.setScope(generateScope(requirements, components));
        
        // Set out of scope
        testPlan.setOutOfScope(generateOutOfScope());
        
        // Generate test strategy
        testPlan.setTestStrategy(generateTestStrategy(requirements, components));
        
        // Generate test cases
        List<TestCase> testCases = testCaseGenerator.generateTestCases(requirements, components);
        testPlan.setTestCases(testCases);
        
        return testPlan;
    }
    
    private List<String> generateObjectives(List<Requirement> requirements, 
                                          List<DesignComponent> components) {
        List<String> objectives = new ArrayList<>();
        
        objectives.add("Verify all functional requirements are implemented correctly");
        objectives.add("Ensure system integration works as designed");
        objectives.add("Validate system performance meets specified criteria");
        objectives.add("Confirm system security and data integrity");
        objectives.add("Test system usability and user experience");
        
        if (!requirements.isEmpty()) {
            objectives.add("Validate " + requirements.size() + " documented requirements");
        }
        
        if (!components.isEmpty()) {
            objectives.add("Test integration of " + components.size() + " system components");
        }
        
        return objectives;
    }
    
    private List<String> generateScope(List<Requirement> requirements, 
                                     List<DesignComponent> components) {
        List<String> scope = new ArrayList<>();
        
        // Add requirement categories to scope
        for (Requirement req : requirements) {
            String category = req.getCategory();
            if (category != null && !scope.contains(category + " requirements")) {
                scope.add(category + " requirements");
            }
        }
        
        // Add component types to scope
        for (DesignComponent component : components) {
            String type = component.getType();
            if (type != null && !scope.contains(type + " components")) {
                scope.add(type + " components");
            }
        }
        
        // Add standard testing areas
        scope.add("Functional testing");
        scope.add("Integration testing");
        scope.add("System testing");
        scope.add("User acceptance testing");
        
        return scope;
    }
    
    private List<String> generateOutOfScope() {
        return Arrays.asList(
            "Performance testing beyond basic validation",
            "Load testing with production-level data",
            "Security penetration testing",
            "Third-party system testing",
            "Hardware compatibility testing",
            "Disaster recovery testing"
        );
    }
    
    private TestStrategy generateTestStrategy(List<Requirement> requirements, 
                                            List<DesignComponent> components) {
        TestStrategy strategy = new TestStrategy();
        
        // Set test types based on requirements and components
        List<String> testTypes = new ArrayList<>();
        testTypes.add("Functional Testing");
        testTypes.add("Integration Testing");
        testTypes.add("System Testing");
        testTypes.add("User Acceptance Testing");
        
        // Add specific test types based on component types
        for (DesignComponent component : components) {
            String type = component.getType();
            if ("API".equalsIgnoreCase(type) && !testTypes.contains("API Testing")) {
                testTypes.add("API Testing");
            }
            if ("UI".equalsIgnoreCase(type) && !testTypes.contains("UI Testing")) {
                testTypes.add("UI Testing");
            }
            if ("Database".equalsIgnoreCase(type) && !testTypes.contains("Database Testing")) {
                testTypes.add("Database Testing");
            }
        }
        
        strategy.setTestTypes(testTypes);
        
        // Set test levels
        strategy.setTestLevels(Arrays.asList(
            "Unit Testing",
            "Integration Testing", 
            "System Testing",
            "Acceptance Testing"
        ));
        
        // Set approach
        strategy.setApproach("Risk-based testing approach focusing on critical functionality first. " +
                           "Combination of manual and automated testing based on test case complexity.");
        
        // Set tools
        strategy.setTools(Arrays.asList(
            "Test Management Tool",
            "Automation Framework",
            "API Testing Tool",
            "Database Testing Tool",
            "Performance Testing Tool"
        ));
        
        // Set environments
        strategy.setEnvironments(Arrays.asList(
            "Development Environment",
            "System Integration Testing Environment",
            "User Acceptance Testing Environment",
            "Production-like Environment"
        ));
        
        // Set risk assessment
        strategy.setRiskAssessment("Medium risk project. Key risks include integration complexity " +
                                 "and data migration. Mitigation through comprehensive integration testing " +
                                 "and early stakeholder involvement.");
        
        return strategy;
    }
}