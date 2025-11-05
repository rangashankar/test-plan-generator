package com.testplan.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Standard Test Case following IEEE 829 and industry best practices
 */
public class TestCase {
    // Standard Test Case Identification
    private String id;                      // Unique test case identifier (TC-XXX)
    private String title;                   // Test case name/title
    private String description;             // Brief description of what is being tested
    private String objective;               // Test objective/purpose
    
    // Test Classification
    private String priority;                // Critical/High/Medium/Low
    private String severity;                // Blocker/Critical/Major/Minor/Trivial
    private String testType;                // Functional/Integration/System/Acceptance/etc.
    private String testLevel;               // Unit/Integration/System/Acceptance
    private String category;                // Smoke/Regression/Sanity/etc.
    
    // Test Environment & Setup
    private String testEnvironment;         // Test environment details
    private List<String> preconditions;    // Prerequisites before test execution
    private List<String> testData;          // Required test data
    private List<String> dependencies;      // Dependencies on other tests/systems
    
    // Test Execution
    private List<TestStep> testSteps;       // Detailed test steps
    private String expectedResult;          // Overall expected result
    private String actualResult;            // Actual result (for execution tracking)
    private String status;                  // Pass/Fail/Blocked/Not Executed
    
    // Test Management
    private String author;                  // Test case author
    private String reviewer;                // Test case reviewer
    private String createdDate;             // Creation date
    private String lastModified;            // Last modification date
    private String version;                 // Test case version
    
    // Traceability
    private List<String> relatedRequirements;  // Linked requirements
    private List<String> relatedComponents;    // Related system components
    private List<String> relatedDefects;       // Associated defects/bugs
    
    // Execution Details
    private String estimatedTime;           // Estimated execution time
    private String actualTime;              // Actual execution time
    private String executedBy;              // Who executed the test
    private String executionDate;           // When test was executed
    private String comments;                // Additional comments/notes
    
    public TestCase() {
        this.preconditions = new ArrayList<>();
        this.testData = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.testSteps = new ArrayList<>();
        this.relatedRequirements = new ArrayList<>();
        this.relatedComponents = new ArrayList<>();
        this.relatedDefects = new ArrayList<>();
        
        // Set default values
        this.priority = "Medium";
        this.severity = "Major";
        this.testLevel = "System";
        this.category = "Functional";
        this.status = "Not Executed";
        this.author = "Test Plan Generator";
        this.version = "1.0";
        this.createdDate = java.time.LocalDateTime.now().toString();
        this.lastModified = this.createdDate;
    }
    
    public TestCase(String id, String title, String description) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
    }
    
    // Standard Getters and Setters
    
    // Test Case Identification
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }
    
    // Test Classification
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    
    public String getTestLevel() { return testLevel; }
    public void setTestLevel(String testLevel) { this.testLevel = testLevel; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    // Test Environment & Setup
    public String getTestEnvironment() { return testEnvironment; }
    public void setTestEnvironment(String testEnvironment) { this.testEnvironment = testEnvironment; }
    
    public List<String> getPreconditions() { return preconditions; }
    public void setPreconditions(List<String> preconditions) { this.preconditions = preconditions; }
    
    public List<String> getTestData() { return testData; }
    public void setTestData(List<String> testData) { this.testData = testData; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    // Test Execution
    public List<TestStep> getTestSteps() { return testSteps; }
    public void setTestSteps(List<TestStep> testSteps) { this.testSteps = testSteps; }
    
    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
    
    public String getActualResult() { return actualResult; }
    public void setActualResult(String actualResult) { this.actualResult = actualResult; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Test Management
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    
    public String getLastModified() { return lastModified; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    // Traceability
    public List<String> getRelatedRequirements() { return relatedRequirements; }
    public void setRelatedRequirements(List<String> relatedRequirements) { 
        this.relatedRequirements = relatedRequirements; 
    }
    
    public List<String> getRelatedComponents() { return relatedComponents; }
    public void setRelatedComponents(List<String> relatedComponents) { 
        this.relatedComponents = relatedComponents; 
    }
    
    public List<String> getRelatedDefects() { return relatedDefects; }
    public void setRelatedDefects(List<String> relatedDefects) { 
        this.relatedDefects = relatedDefects; 
    }
    
    // Execution Details
    public String getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }
    
    public String getActualTime() { return actualTime; }
    public void setActualTime(String actualTime) { this.actualTime = actualTime; }
    
    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }
    
    public String getExecutionDate() { return executionDate; }
    public void setExecutionDate(String executionDate) { this.executionDate = executionDate; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    
    @Override
    public String toString() {
        return String.format("TestCase{id='%s', title='%s', type='%s'}", 
                           id, title, testType);
    }
}