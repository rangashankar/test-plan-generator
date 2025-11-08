package com.testplan.generator;

import com.testplan.model.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

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
        
        List<Requirement> effectiveRequirements = requirements != null 
            ? new ArrayList<>(requirements) 
            : new ArrayList<>();
        List<DesignComponent> effectiveComponents = components != null 
            ? new ArrayList<>(components) 
            : new ArrayList<>();
        
        if (effectiveRequirements.isEmpty() && !effectiveComponents.isEmpty()) {
            effectiveRequirements.addAll(deriveRequirementsFromComponents(effectiveComponents));
        }
        
        TestPlan testPlan = new TestPlan();
        testPlan.setId("TP_" + projectName.replaceAll("\\s+", "_").toUpperCase());
        testPlan.setTitle("Test Plan for " + projectName);
        testPlan.setDescription("Comprehensive test plan generated from requirements and design documents");
        testPlan.setVersion(version);
        testPlan.setCreatedBy("TestPlan Generator");
        
        // Set objectives
        testPlan.setObjectives(generateObjectives(effectiveRequirements, effectiveComponents));
        
        // Set scope
        testPlan.setScope(generateScope(effectiveRequirements, effectiveComponents));
        testPlan.setTestItems(buildTestItems(effectiveRequirements, effectiveComponents));
        
        // Set out of scope
        testPlan.setOutOfScope(generateOutOfScope());
        
        // Generate test strategy
        testPlan.setTestStrategy(generateTestStrategy(effectiveRequirements, effectiveComponents));
        
        // Generate test cases
        List<TestCase> testCases = testCaseGenerator.generateTestCases(effectiveRequirements, effectiveComponents);
        testPlan.setTestCases(testCases);
        
        return testPlan;
    }
    
    /**
     * Derive baseline requirements from design components when a dedicated requirement
     * document is not available.
     */
    public List<Requirement> deriveRequirementsFromComponents(List<DesignComponent> components) {
        List<Requirement> derived = new ArrayList<>();
        if (components == null) {
            return derived;
        }
        
        int counter = 1;
        for (DesignComponent component : components) {
            Requirement requirement = new Requirement();
            requirement.setId("DES-REQ-" + String.format("%03d", counter++));
            String componentName = safeComponentName(component);
            requirement.setTitle(componentName + " Capability");
            
            StringBuilder description = new StringBuilder();
            if (component.getDescription() != null && !component.getDescription().isEmpty()) {
                description.append(component.getDescription().trim());
            } else {
                description.append("Validate that ").append(componentName)
                           .append(" performs its intended functionality.");
            }
            
            if (!component.getInterfaces().isEmpty()) {
                description.append(" Interfaces/APIs: ").append(String.join(", ", component.getInterfaces())).append(".");
            }
            if (!component.getDependencies().isEmpty()) {
                description.append(" Dependencies: ").append(String.join(", ", component.getDependencies())).append(".");
            }
            requirement.setDescription(description.toString());
            requirement.setPriority(deriveRequirementPriority(component.getType()));
            requirement.setCategory(deriveRequirementCategory(component.getType()));
            requirement.getAcceptanceCriteria().addAll(buildAcceptanceCriteriaFromComponent(component));
            requirement.getDependencies().addAll(component.getDependencies());
            
            derived.add(requirement);
        }
        
        return derived;
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
    
    private List<String> buildTestItems(List<Requirement> requirements, List<DesignComponent> components) {
        LinkedHashSet<String> items = new LinkedHashSet<>();
        
        if (requirements != null) {
            for (Requirement requirement : requirements) {
                if (requirement == null) continue;
                String title = requirement.getTitle() != null ? requirement.getTitle() : requirement.getId();
                items.add("Requirement: " + title);
            }
        }
        
        if (components != null) {
            for (DesignComponent component : components) {
                if (component == null) continue;
                items.add("Component: " + safeComponentName(component));
            }
        }
        
        return new ArrayList<>(items);
    }
    
    private List<String> generateScope(List<Requirement> requirements, 
                                     List<DesignComponent> components) {
        LinkedHashSet<String> scope = new LinkedHashSet<>();
        
        // Add requirement categories to scope
        for (Requirement req : requirements) {
            String category = req.getCategory();
            if (category != null && !category.isEmpty()) {
                scope.add(category + " requirements");
            }
        }
        
        // Add component types to scope
        for (DesignComponent component : components) {
            String type = component.getType();
            if (type != null && !type.isEmpty()) {
                scope.add(type + " components");
            }
        }
        
        // Add standard testing areas
        scope.add("Functional testing");
        scope.add("Integration testing");
        scope.add("System testing");
        scope.add("User acceptance testing");
        
        return new ArrayList<>(scope);
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
    
    private String safeComponentName(DesignComponent component) {
        if (component == null) {
            return "Component";
        }
        if (component.getName() != null && !component.getName().isEmpty()) {
            return component.getName();
        }
        if (component.getId() != null && !component.getId().isEmpty()) {
            return component.getId();
        }
        return "Component";
    }
    
    private String deriveRequirementPriority(String componentType) {
        if (componentType == null) {
            return "Medium";
        }
        String type = componentType.toLowerCase();
        if (type.contains("api") || type.contains("service") || type.contains("integration")) {
            return "High";
        }
        if (type.contains("database") || type.contains("data")) {
            return "High";
        }
        if (type.contains("ui") || type.contains("interface")) {
            return "Medium";
        }
        return "Medium";
    }
    
    private String deriveRequirementCategory(String componentType) {
        if (componentType == null) {
            return "Functional";
        }
        String type = componentType.toLowerCase();
        if (type.contains("api") || type.contains("integration")) {
            return "Integration";
        }
        if (type.contains("database") || type.contains("data")) {
            return "Data";
        }
        if (type.contains("ui") || type.contains("interface")) {
            return "UI/UX";
        }
        return "Functional";
    }
    
    private List<String> buildAcceptanceCriteriaFromComponent(DesignComponent component) {
        List<String> criteria = new ArrayList<>();
        String componentName = safeComponentName(component);
        criteria.add(componentName + " executes its primary responsibilities without errors.");
        
        if (component != null) {
            if (!component.getInterfaces().isEmpty()) {
                criteria.add("Interfaces/APIs (" + String.join(", ", component.getInterfaces()) + 
                             ") respond with correct data and status codes.");
            }
            
            if (!component.getDependencies().isEmpty()) {
                criteria.add("Dependencies (" + String.join(", ", component.getDependencies()) + 
                             ") are invoked following contract expectations.");
            }
            
            if (!component.getBusinessRules().isEmpty()) {
                criteria.add("Business rules are enforced: " + String.join("; ", component.getBusinessRules()));
            }
        }
        
        criteria.add("Monitoring and logging exist for " + componentName + " critical paths.");
        return criteria;
    }
}
