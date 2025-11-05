package com.testplan.model;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * IEEE 829 Standard Test Plan Document
 * Follows industry best practices for test plan documentation
 */
public class TestPlan {
    // 1. Test Plan Identifier
    private String id;                          // Unique test plan identifier
    private String title;                       // Test plan title
    private String version;                     // Document version
    private String status;                      // Draft/Review/Approved/Active
    
    // 2. Introduction
    private String description;                 // Test plan description
    private String purpose;                     // Purpose of testing
    private String background;                  // Project background
    private String documentScope;               // Document scope
    
    // 3. Test Items
    private List<String> testItems;             // Items to be tested
    private List<String> featuresNotTested;    // Features not to be tested
    
    // 4. Approach
    private TestStrategy testStrategy;          // Overall test strategy
    private List<String> testApproach;          // Testing approach details
    private List<String> testLevels;            // Unit/Integration/System/Acceptance
    private List<String> testTypes;             // Functional/Performance/Security/etc.
    
    // 5. Item Pass/Fail Criteria
    private List<String> passCriteria;          // Pass criteria
    private List<String> failCriteria;          // Fail criteria
    private List<String> suspensionCriteria;   // When to suspend testing
    private List<String> resumptionCriteria;   // When to resume testing
    
    // 6. Test Deliverables
    private List<String> testDeliverables;      // Test deliverables
    private List<String> testDocuments;         // Test documentation
    
    // 7. Environmental Needs
    private String testEnvironment;             // Test environment requirements
    private List<String> hardwareRequirements; // Hardware needs
    private List<String> softwareRequirements; // Software needs
    private List<String> toolsRequired;         // Testing tools required
    
    // 8. Responsibilities
    private String testManager;                 // Test manager
    private String createdBy;                   // Document author
    private String reviewedBy;                  // Document reviewer
    private String approvedBy;                  // Document approver
    private List<String> testTeam;              // Test team members
    
    // 9. Staffing and Training
    private List<String> staffingNeeds;         // Staffing requirements
    private List<String> trainingNeeds;         // Training requirements
    
    // 10. Schedule
    private LocalDateTime createdDate;          // Creation date
    private LocalDateTime lastModified;         // Last modification date
    private String startDate;                   // Test start date
    private String endDate;                     // Test end date
    private List<String> milestones;            // Key milestones
    
    // 11. Risks and Contingencies
    private List<String> risks;                 // Identified risks
    private List<String> contingencies;         // Contingency plans
    private List<String> assumptions;           // Assumptions made
    
    // 12. Approvals
    private String approvalDate;                // Approval date
    private String approvalStatus;              // Approval status
    
    // Test Execution
    private List<String> objectives;            // Test objectives
    private List<String> scope;                 // Test scope
    private List<String> outOfScope;            // Out of scope items
    private List<TestCase> testCases;           // Test cases
    
    // Metrics and Reporting
    private String entryExitCriteria;           // Entry/Exit criteria
    private List<String> metricsToCollect;      // Metrics to be collected
    private String reportingFrequency;          // Reporting frequency
    
    public TestPlan() {
        // Initialize all lists
        this.testItems = new ArrayList<>();
        this.featuresNotTested = new ArrayList<>();
        this.testApproach = new ArrayList<>();
        this.testLevels = new ArrayList<>();
        this.testTypes = new ArrayList<>();
        this.passCriteria = new ArrayList<>();
        this.failCriteria = new ArrayList<>();
        this.suspensionCriteria = new ArrayList<>();
        this.resumptionCriteria = new ArrayList<>();
        this.testDeliverables = new ArrayList<>();
        this.testDocuments = new ArrayList<>();
        this.hardwareRequirements = new ArrayList<>();
        this.softwareRequirements = new ArrayList<>();
        this.toolsRequired = new ArrayList<>();
        this.testTeam = new ArrayList<>();
        this.staffingNeeds = new ArrayList<>();
        this.trainingNeeds = new ArrayList<>();
        this.milestones = new ArrayList<>();
        this.risks = new ArrayList<>();
        this.contingencies = new ArrayList<>();
        this.assumptions = new ArrayList<>();
        this.objectives = new ArrayList<>();
        this.scope = new ArrayList<>();
        this.outOfScope = new ArrayList<>();
        this.testCases = new ArrayList<>();
        this.metricsToCollect = new ArrayList<>();
        
        // Set default values
        this.createdDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.status = "Draft";
        this.version = "1.0";
        this.approvalStatus = "Pending";
        this.reportingFrequency = "Daily";
        
        // Set default standard values
        initializeDefaults();
    }
    
    public TestPlan(String id, String title, String description) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
    }
    
    private void initializeDefaults() {
        // Default test levels
        this.testLevels.add("Unit Testing");
        this.testLevels.add("Integration Testing");
        this.testLevels.add("System Testing");
        this.testLevels.add("Acceptance Testing");
        
        // Default test types
        this.testTypes.add("Functional Testing");
        this.testTypes.add("Integration Testing");
        this.testTypes.add("System Testing");
        this.testTypes.add("Regression Testing");
        
        // Default deliverables
        this.testDeliverables.add("Test Plan Document");
        this.testDeliverables.add("Test Cases");
        this.testDeliverables.add("Test Execution Report");
        this.testDeliverables.add("Defect Report");
        
        // Default metrics
        this.metricsToCollect.add("Test Case Execution Status");
        this.metricsToCollect.add("Defect Discovery Rate");
        this.metricsToCollect.add("Test Coverage");
        this.metricsToCollect.add("Pass/Fail Rate");
        
        // Default pass/fail criteria
        this.passCriteria.add("All critical test cases pass");
        this.passCriteria.add("No critical or high severity defects");
        this.passCriteria.add("Test coverage >= 90%");
        
        this.failCriteria.add("Any critical test case fails");
        this.failCriteria.add("Critical or high severity defects found");
        this.failCriteria.add("Test coverage < 80%");
    }
    
    // IEEE 829 Standard Getters and Setters
    
    // 1. Test Plan Identifier
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // 2. Introduction
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    
    public String getDocumentScope() { return documentScope; }
    public void setDocumentScope(String documentScope) { this.documentScope = documentScope; }
    
    // 3. Test Items
    public List<String> getTestItems() { return testItems; }
    public void setTestItems(List<String> testItems) { this.testItems = testItems; }
    
    public List<String> getFeaturesNotTested() { return featuresNotTested; }
    public void setFeaturesNotTested(List<String> featuresNotTested) { this.featuresNotTested = featuresNotTested; }
    
    // 4. Approach
    public TestStrategy getTestStrategy() { return testStrategy; }
    public void setTestStrategy(TestStrategy testStrategy) { this.testStrategy = testStrategy; }
    
    public List<String> getTestApproach() { return testApproach; }
    public void setTestApproach(List<String> testApproach) { this.testApproach = testApproach; }
    
    public List<String> getTestLevels() { return testLevels; }
    public void setTestLevels(List<String> testLevels) { this.testLevels = testLevels; }
    
    public List<String> getTestTypes() { return testTypes; }
    public void setTestTypes(List<String> testTypes) { this.testTypes = testTypes; }
    
    // 5. Pass/Fail Criteria
    public List<String> getPassCriteria() { return passCriteria; }
    public void setPassCriteria(List<String> passCriteria) { this.passCriteria = passCriteria; }
    
    public List<String> getFailCriteria() { return failCriteria; }
    public void setFailCriteria(List<String> failCriteria) { this.failCriteria = failCriteria; }
    
    public List<String> getSuspensionCriteria() { return suspensionCriteria; }
    public void setSuspensionCriteria(List<String> suspensionCriteria) { this.suspensionCriteria = suspensionCriteria; }
    
    public List<String> getResumptionCriteria() { return resumptionCriteria; }
    public void setResumptionCriteria(List<String> resumptionCriteria) { this.resumptionCriteria = resumptionCriteria; }
    
    // 6. Test Deliverables
    public List<String> getTestDeliverables() { return testDeliverables; }
    public void setTestDeliverables(List<String> testDeliverables) { this.testDeliverables = testDeliverables; }
    
    public List<String> getTestDocuments() { return testDocuments; }
    public void setTestDocuments(List<String> testDocuments) { this.testDocuments = testDocuments; }
    
    // 7. Environmental Needs
    public String getTestEnvironment() { return testEnvironment; }
    public void setTestEnvironment(String testEnvironment) { this.testEnvironment = testEnvironment; }
    
    public List<String> getHardwareRequirements() { return hardwareRequirements; }
    public void setHardwareRequirements(List<String> hardwareRequirements) { this.hardwareRequirements = hardwareRequirements; }
    
    public List<String> getSoftwareRequirements() { return softwareRequirements; }
    public void setSoftwareRequirements(List<String> softwareRequirements) { this.softwareRequirements = softwareRequirements; }
    
    public List<String> getToolsRequired() { return toolsRequired; }
    public void setToolsRequired(List<String> toolsRequired) { this.toolsRequired = toolsRequired; }
    
    // 8. Responsibilities
    public String getTestManager() { return testManager; }
    public void setTestManager(String testManager) { this.testManager = testManager; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    
    public List<String> getTestTeam() { return testTeam; }
    public void setTestTeam(List<String> testTeam) { this.testTeam = testTeam; }
    
    // 9. Staffing and Training
    public List<String> getStaffingNeeds() { return staffingNeeds; }
    public void setStaffingNeeds(List<String> staffingNeeds) { this.staffingNeeds = staffingNeeds; }
    
    public List<String> getTrainingNeeds() { return trainingNeeds; }
    public void setTrainingNeeds(List<String> trainingNeeds) { this.trainingNeeds = trainingNeeds; }
    
    // 10. Schedule
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public List<String> getMilestones() { return milestones; }
    public void setMilestones(List<String> milestones) { this.milestones = milestones; }
    
    // 11. Risks and Contingencies
    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> risks) { this.risks = risks; }
    
    public List<String> getContingencies() { return contingencies; }
    public void setContingencies(List<String> contingencies) { this.contingencies = contingencies; }
    
    public List<String> getAssumptions() { return assumptions; }
    public void setAssumptions(List<String> assumptions) { this.assumptions = assumptions; }
    
    // 12. Approvals
    public String getApprovalDate() { return approvalDate; }
    public void setApprovalDate(String approvalDate) { this.approvalDate = approvalDate; }
    
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    
    // Test Execution (Legacy compatibility)
    public List<String> getObjectives() { return objectives; }
    public void setObjectives(List<String> objectives) { this.objectives = objectives; }
    
    public List<String> getScope() { return scope; }
    public void setScope(List<String> scope) { this.scope = scope; }
    
    public List<String> getOutOfScope() { return outOfScope; }
    public void setOutOfScope(List<String> outOfScope) { this.outOfScope = outOfScope; }
    
    public List<TestCase> getTestCases() { return testCases; }
    public void setTestCases(List<TestCase> testCases) { this.testCases = testCases; }
    
    // Metrics and Reporting
    public String getEntryExitCriteria() { return entryExitCriteria; }
    public void setEntryExitCriteria(String entryExitCriteria) { this.entryExitCriteria = entryExitCriteria; }
    
    public List<String> getMetricsToCollect() { return metricsToCollect; }
    public void setMetricsToCollect(List<String> metricsToCollect) { this.metricsToCollect = metricsToCollect; }
    
    public String getReportingFrequency() { return reportingFrequency; }
    public void setReportingFrequency(String reportingFrequency) { this.reportingFrequency = reportingFrequency; }
    
    @Override
    public String toString() {
        return String.format("TestPlan{id='%s', title='%s', testCases=%d}", 
                           id, title, testCases.size());
    }
}