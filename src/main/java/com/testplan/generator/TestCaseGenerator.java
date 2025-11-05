package com.testplan.generator;

import com.testplan.model.*;
import java.util.List;
import java.util.ArrayList;

public class TestCaseGenerator {
    
    private int testCaseCounter = 1;
    
    /**
     * Generate test cases based on requirements and design components
     */
    public List<TestCase> generateTestCases(List<Requirement> requirements, 
                                          List<DesignComponent> components) {
        List<TestCase> testCases = new ArrayList<>();
        
        // Generate functional test cases from requirements
        for (Requirement req : requirements) {
            testCases.addAll(generateFunctionalTestCases(req));
        }
        
        // Generate integration test cases from design components
        for (DesignComponent component : components) {
            testCases.addAll(generateIntegrationTestCases(component, components));
        }
        
        // If no integration tests were generated, create some default ones
        long integrationTestCount = testCases.stream().filter(tc -> "Integration".equals(tc.getTestType())).count();
        if (integrationTestCount == 0) {
            testCases.addAll(generateDefaultIntegrationTests());
        }
        
        // Generate boundary and negative test cases
        testCases.addAll(generateBoundaryTestCases(requirements));
        testCases.addAll(generateNegativeTestCases(requirements));
        
        return testCases;
    }
    
    private List<TestCase> generateFunctionalTestCases(Requirement requirement) {
        List<TestCase> testCases = new ArrayList<>();
        
        // Generate positive test case for the requirement
        TestCase positiveTest = createTestCase(
            "TC_" + String.format("%03d", testCaseCounter++),
            "Verify " + requirement.getTitle(),
            "Functional test to verify " + requirement.getDescription(),
            "Functional",
            requirement.getPriority()
        );
        
        positiveTest.getRelatedRequirements().add(requirement.getId());
        
        // Generate detailed test steps from acceptance criteria
        if (!requirement.getAcceptanceCriteria().isEmpty()) {
            // Add setup step
            TestStep setupStep = new TestStep(1, 
                "Navigate to the " + requirement.getTitle().toLowerCase() + " feature", 
                "Feature interface is accessible and loads properly");
            positiveTest.getTestSteps().add(setupStep);
            
            int stepNumber = 2;
            for (String criteria : requirement.getAcceptanceCriteria()) {
                TestStep step = new TestStep(stepNumber++, 
                    "Verify: " + criteria, 
                    "Criterion is satisfied: " + criteria);
                positiveTest.getTestSteps().add(step);
            }
            
            // Add validation step
            TestStep validationStep = new TestStep(stepNumber, 
                "Validate overall functionality and user experience", 
                "All acceptance criteria are met and user can complete the workflow successfully");
            positiveTest.getTestSteps().add(validationStep);
            
        } else {
            // Enhanced default test steps
            TestStep step1 = new TestStep(1, 
                "Access the system and navigate to " + requirement.getTitle().toLowerCase(), 
                "System is accessible and feature is available in the interface");
            TestStep step2 = new TestStep(2, 
                "Execute the primary functionality: " + requirement.getTitle(), 
                "Feature executes without errors and provides expected functionality");
            TestStep step3 = new TestStep(3, 
                "Verify the result matches the requirement: " + requirement.getDescription(), 
                "System behavior aligns with documented requirement and user expectations");
            TestStep step4 = new TestStep(4, 
                "Test with different valid inputs and user scenarios", 
                "System handles various valid scenarios consistently and correctly");
            
            positiveTest.getTestSteps().add(step1);
            positiveTest.getTestSteps().add(step2);
            positiveTest.getTestSteps().add(step3);
            positiveTest.getTestSteps().add(step4);
        }
        
        positiveTest.setExpectedResult("All acceptance criteria are met and requirement is satisfied");
        testCases.add(positiveTest);
        
        return testCases;
    }
    
    private List<TestCase> generateIntegrationTestCases(DesignComponent component, 
                                                       List<DesignComponent> allComponents) {
        List<TestCase> testCases = new ArrayList<>();
        
        // Generate integration tests for components with dependencies
        if (!component.getDependencies().isEmpty()) {
            TestCase integrationTest = createTestCase(
                "TC_" + String.format("%03d", testCaseCounter++),
                "Integration test for " + component.getName(),
                "Verify integration between " + component.getName() + " and its dependencies",
                "Integration",
                "High"
            );
            
            integrationTest.getRelatedComponents().add(component.getId());
            
            // Enhanced integration test steps
            TestStep setupStep = new TestStep(1,
                "Ensure all dependent systems are running and accessible: " + String.join(", ", component.getDependencies()),
                "All dependencies are online and responding to health checks");
            integrationTest.getTestSteps().add(setupStep);
            
            int stepNumber = 2;
            for (String dependency : component.getDependencies()) {
                TestStep step = new TestStep(stepNumber++,
                    "Test data flow and communication between " + component.getName() + " and " + dependency,
                    "Data is exchanged correctly, APIs respond within SLA, no data loss or corruption");
                integrationTest.getTestSteps().add(step);
            }
            
            // Add comprehensive validation steps
            TestStep errorHandlingStep = new TestStep(stepNumber++,
                "Test error handling when dependencies are unavailable or return errors",
                "System handles dependency failures gracefully with appropriate fallback mechanisms");
            integrationTest.getTestSteps().add(errorHandlingStep);
            
            TestStep performanceStep = new TestStep(stepNumber++,
                "Verify integration performance under normal and peak load conditions",
                "Integration meets performance requirements and doesn't create bottlenecks");
            integrationTest.getTestSteps().add(performanceStep);
            
            TestStep dataConsistencyStep = new TestStep(stepNumber,
                "Validate data consistency and integrity across integrated components",
                "Data remains consistent across all systems and no data corruption occurs");
            integrationTest.getTestSteps().add(dataConsistencyStep);
            
            integrationTest.setExpectedResult("All integrations work correctly without errors");
            testCases.add(integrationTest);
        }
        
        // Also generate integration tests for common system integrations even if no specific components are found
        if (allComponents.isEmpty() || allComponents.size() < 3) {
            testCases.addAll(generateDefaultIntegrationTests());
        }
        
        return testCases;
    }
    
    private List<TestCase> generateDefaultIntegrationTests() {
        List<TestCase> testCases = new ArrayList<>();
        
        // Common integration scenarios for most systems
        String[] integrationScenarios = {
            "User Authentication System Integration",
            "Database Integration and Data Persistence", 
            "External API Integration",
            "Frontend-Backend Integration",
            "Third-party Service Integration"
        };
        
        for (String scenario : integrationScenarios) {
            TestCase integrationTest = createTestCase(
                "TC_" + String.format("%03d", testCaseCounter++),
                scenario + " Test",
                "Verify " + scenario.toLowerCase() + " works correctly with the main system",
                "Integration",
                "High"
            );
            
            // Generate scenario-specific test steps
            if (scenario.contains("Authentication")) {
                TestStep step1 = new TestStep(1,
                    "Verify user login integration with authentication service",
                    "Users can successfully authenticate and receive proper session tokens");
                TestStep step2 = new TestStep(2,
                    "Test session management and token validation across system components",
                    "Session tokens are validated correctly and user permissions are enforced");
                TestStep step3 = new TestStep(3,
                    "Verify logout and session cleanup integration",
                    "User sessions are properly terminated and resources are cleaned up");
                
                integrationTest.getTestSteps().add(step1);
                integrationTest.getTestSteps().add(step2);
                integrationTest.getTestSteps().add(step3);
                
            } else if (scenario.contains("Database")) {
                TestStep step1 = new TestStep(1,
                    "Test data creation, reading, updating, and deletion operations",
                    "All CRUD operations work correctly with proper data validation");
                TestStep step2 = new TestStep(2,
                    "Verify transaction handling and data consistency",
                    "Database transactions maintain ACID properties and data integrity");
                TestStep step3 = new TestStep(3,
                    "Test database connection pooling and error handling",
                    "System handles database connectivity issues gracefully");
                
                integrationTest.getTestSteps().add(step1);
                integrationTest.getTestSteps().add(step2);
                integrationTest.getTestSteps().add(step3);
                
            } else if (scenario.contains("API")) {
                TestStep step1 = new TestStep(1,
                    "Test API request/response handling and data format validation",
                    "API calls are made correctly with proper request formatting and response parsing");
                TestStep step2 = new TestStep(2,
                    "Verify API error handling and retry mechanisms",
                    "System handles API failures gracefully with appropriate retry logic");
                TestStep step3 = new TestStep(3,
                    "Test API rate limiting and timeout handling",
                    "System respects API rate limits and handles timeouts appropriately");
                
                integrationTest.getTestSteps().add(step1);
                integrationTest.getTestSteps().add(step2);
                integrationTest.getTestSteps().add(step3);
                
            } else {
                // Generic integration test steps
                TestStep step1 = new TestStep(1,
                    "Verify component communication and data exchange",
                    "Components communicate correctly and exchange data as expected");
                TestStep step2 = new TestStep(2,
                    "Test error handling and fallback mechanisms",
                    "System handles integration failures gracefully");
                TestStep step3 = new TestStep(3,
                    "Validate end-to-end workflow integration",
                    "Complete user workflows work correctly across integrated components");
                
                integrationTest.getTestSteps().add(step1);
                integrationTest.getTestSteps().add(step2);
                integrationTest.getTestSteps().add(step3);
            }
            
            integrationTest.setExpectedResult("Integration works seamlessly with proper error handling and performance");
            testCases.add(integrationTest);
        }
        
        return testCases;
    }
    
    private List<TestCase> generateBoundaryTestCases(List<Requirement> requirements) {
        List<TestCase> testCases = new ArrayList<>();
        
        // Generate boundary tests for all functional requirements, not just those with specific keywords
        for (Requirement req : requirements) {
            if ("Functional".equals(req.getCategory()) || containsBoundaryConditions(req.getDescription())) {
                TestCase boundaryTest = createTestCase(
                    "TC_" + String.format("%03d", testCaseCounter++),
                    "Boundary test for " + req.getTitle(),
                    "Test edge cases and boundary conditions for " + req.getDescription(),
                    "Boundary",
                    req.getPriority()
                );
                
                boundaryTest.getRelatedRequirements().add(req.getId());
                
                // Generate context-specific boundary test steps
                if (req.getDescription().toLowerCase().contains("predict") || 
                    req.getDescription().toLowerCase().contains("recommend")) {
                    TestStep step1 = new TestStep(1, 
                        "Test with minimal user data (new user with no history)", 
                        "System should provide basic recommendations or gracefully handle lack of data");
                    TestStep step2 = new TestStep(2, 
                        "Test with maximum user data (extensive purchase and browsing history)", 
                        "System should process large datasets efficiently and provide accurate predictions");
                    TestStep step3 = new TestStep(3, 
                        "Test with edge case scenarios (conflicting preferences, seasonal changes)", 
                        "System should handle complex scenarios and provide reasonable recommendations");
                    
                    boundaryTest.getTestSteps().add(step1);
                    boundaryTest.getTestSteps().add(step2);
                    boundaryTest.getTestSteps().add(step3);
                } else if (req.getDescription().toLowerCase().contains("notification") || 
                          req.getDescription().toLowerCase().contains("alert")) {
                    TestStep step1 = new TestStep(1, 
                        "Test notification frequency limits (minimum and maximum intervals)", 
                        "System should respect notification preferences and avoid spam");
                    TestStep step2 = new TestStep(2, 
                        "Test with large number of simultaneous notifications", 
                        "System should handle notification queues efficiently without delays");
                    TestStep step3 = new TestStep(3, 
                        "Test notification delivery during system peak loads", 
                        "Notifications should be delivered reliably even under high system load");
                    
                    boundaryTest.getTestSteps().add(step1);
                    boundaryTest.getTestSteps().add(step2);
                    boundaryTest.getTestSteps().add(step3);
                } else {
                    // Generic boundary tests for other functional requirements
                    TestStep step1 = new TestStep(1, 
                        "Test with minimum valid input data for " + req.getTitle().toLowerCase(), 
                        "System should accept and process minimal valid input correctly");
                    TestStep step2 = new TestStep(2, 
                        "Test with maximum allowed input data for " + req.getTitle().toLowerCase(), 
                        "System should handle large input volumes without performance degradation");
                    TestStep step3 = new TestStep(3, 
                        "Test with edge case inputs (empty strings, special characters, unicode)", 
                        "System should validate input properly and handle edge cases gracefully");
                    
                    boundaryTest.getTestSteps().add(step1);
                    boundaryTest.getTestSteps().add(step2);
                    boundaryTest.getTestSteps().add(step3);
                }
                
                boundaryTest.setExpectedResult("System handles all boundary conditions and edge cases correctly without errors or performance issues");
                testCases.add(boundaryTest);
            }
        }
        
        return testCases;
    }
    
    private List<TestCase> generateNegativeTestCases(List<Requirement> requirements) {
        List<TestCase> testCases = new ArrayList<>();
        
        for (Requirement req : requirements) {
            TestCase negativeTest = createTestCase(
                "TC_" + String.format("%03d", testCaseCounter++),
                "Negative test for " + req.getTitle(),
                "Test error handling and invalid scenarios for " + req.getDescription(),
                "Negative",
                req.getPriority()
            );
            
            negativeTest.getRelatedRequirements().add(req.getId());
            
            // Enhanced negative test steps
            TestStep step1 = new TestStep(1, 
                "Access the " + req.getTitle().toLowerCase() + " feature", 
                "Feature interface is accessible");
            TestStep step2 = new TestStep(2, 
                "Provide invalid input data (empty, null, malformed, out-of-range values)", 
                "System should reject invalid input with clear error message");
            TestStep step3 = new TestStep(3, 
                "Attempt to access feature without proper authentication/authorization", 
                "System should deny access with appropriate security message");
            TestStep step4 = new TestStep(4, 
                "Submit incomplete or missing required data", 
                "System should validate input and display specific error messages for missing fields");
            TestStep step5 = new TestStep(5, 
                "Test with malicious input (SQL injection, XSS, script injection)", 
                "System should sanitize input and prevent security vulnerabilities");
            TestStep step6 = new TestStep(6, 
                "Verify error logging and monitoring", 
                "System should log errors appropriately without exposing sensitive information");
            
            negativeTest.getTestSteps().add(step1);
            negativeTest.getTestSteps().add(step2);
            negativeTest.getTestSteps().add(step3);
            negativeTest.getTestSteps().add(step4);
            negativeTest.getTestSteps().add(step5);
            negativeTest.getTestSteps().add(step6);
            
            negativeTest.setExpectedResult("System handles all error scenarios gracefully with appropriate error messages, maintains security, logs errors properly, and preserves system stability");
            testCases.add(negativeTest);
        }
        
        return testCases;
    }
    
    private TestCase createTestCase(String id, String title, String description, 
                                  String testType, String priority) {
        TestCase testCase = new TestCase(id, title, description);
        testCase.setTestType(testType);
        testCase.setPriority(priority);
        testCase.setCategory("Regression");
        
        // Set standard fields following IEEE 829
        testCase.setObjective("Verify " + title.toLowerCase());
        testCase.setSeverity(mapPriorityToSeverity(priority));
        testCase.setTestLevel("System");
        testCase.setTestEnvironment("Test Environment");
        testCase.setEstimatedTime("15 minutes");
        
        // Add standard preconditions
        testCase.getPreconditions().add("System is accessible and running");
        testCase.getPreconditions().add("Test data is available");
        testCase.getPreconditions().add("User has appropriate permissions");
        
        return testCase;
    }
    
    private String mapPriorityToSeverity(String priority) {
        if (priority == null) return "Major";
        
        switch (priority.toLowerCase()) {
            case "critical":
            case "high":
                return "Critical";
            case "medium":
                return "Major";
            case "low":
                return "Minor";
            default:
                return "Major";
        }
    }
    
    private boolean containsBoundaryConditions(String description) {
        String lowerDesc = description.toLowerCase();
        return lowerDesc.contains("limit") || lowerDesc.contains("maximum") || 
               lowerDesc.contains("minimum") || lowerDesc.contains("range") ||
               lowerDesc.contains("length") || lowerDesc.contains("size") ||
               lowerDesc.contains("threshold") || lowerDesc.contains("capacity") ||
               lowerDesc.contains("volume") || lowerDesc.contains("frequency") ||
               lowerDesc.contains("rate") || lowerDesc.contains("count") ||
               lowerDesc.contains("number") || lowerDesc.contains("amount") ||
               lowerDesc.contains("percentage") || lowerDesc.contains("accuracy") ||
               lowerDesc.contains("performance") || lowerDesc.contains("speed") ||
               lowerDesc.contains("time") || lowerDesc.contains("duration");
    }
}