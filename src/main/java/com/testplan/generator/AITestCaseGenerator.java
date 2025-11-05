package com.testplan.generator;

import com.testplan.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import java.util.List;
import java.util.ArrayList;

/**
 * AI-powered test case generator using cloud AI services
 * Creates comprehensive test cases with intelligent analysis
 */
public class AITestCaseGenerator {
    private BedrockRuntimeClient bedrockClient;
    private ObjectMapper objectMapper;
    private static final String MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
    
    public AITestCaseGenerator() {
        this.bedrockClient = BedrockRuntimeClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate comprehensive test cases using AI analysis
     */
    public List<TestCase> generateAITestCases(List<Requirement> requirements, 
                                            List<DesignComponent> components,
                                            String projectContext) {
        List<TestCase> testCases = new ArrayList<>();
        
        try {
            String prompt = buildTestCasePrompt(requirements, components, projectContext);
            String response = invokeCloudAIModel(prompt);
            testCases = parseTestCasesFromAIResponse(response);
            System.out.println("   🤖 AI generated " + testCases.size() + " intelligent test cases");
        } catch (Exception e) {
            System.err.println("Error generating AI test cases: " + e.getMessage());
            // Fallback to traditional generation if AI fails
            TestCaseGenerator fallbackGenerator = new TestCaseGenerator();
            testCases = fallbackGenerator.generateTestCases(requirements, components);
        }
        
        return testCases;
    }
    
    private String buildTestCasePrompt(List<Requirement> requirements, 
                                     List<DesignComponent> components,
                                     String projectContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a senior QA engineer and test architect with expertise in IEEE 829 standards and comprehensive test design. ");
        prompt.append("Create detailed, professional test cases that ensure complete coverage and quality assurance.\n\n");
        
        prompt.append("TEST DESIGN PRINCIPLES:\n");
        prompt.append("1. Create test cases that are specific, measurable, and executable\n");
        prompt.append("2. Ensure comprehensive coverage of all requirements and user scenarios\n");
        prompt.append("3. Include both positive (happy path) and negative (error) test cases\n");
        prompt.append("4. Design tests that validate business logic, data integrity, and user experience\n");
        prompt.append("5. Consider real-world usage patterns and edge cases\n");
        prompt.append("6. Ensure tests are maintainable and provide clear pass/fail criteria\n\n");
        
        prompt.append("TEST CATEGORIES TO COVER:\n");
        prompt.append("• FUNCTIONAL: Core business logic, user workflows, data processing\n");
        prompt.append("• INTEGRATION: Component interactions, API calls, data flow between systems\n");
        prompt.append("• BOUNDARY: Input validation, limits, edge cases, data boundaries\n");
        prompt.append("• NEGATIVE: Error handling, invalid inputs, unauthorized access, failure scenarios\n");
        prompt.append("• PERFORMANCE: Response times, load handling, resource usage, scalability\n");
        prompt.append("• SECURITY: Authentication, authorization, data protection, input sanitization\n");
        prompt.append("• USABILITY: User experience, accessibility, navigation, error messages\n");
        prompt.append("• DATA: Data validation, integrity, backup/recovery, migration\n\n");
        
        prompt.append("PRIORITY GUIDELINES:\n");
        prompt.append("• Critical: Core functionality, security, data integrity (must pass for release)\n");
        prompt.append("• High: Primary user workflows, important business logic\n");
        prompt.append("• Medium: Secondary features, edge cases, performance validation\n");
        prompt.append("• Low: Nice-to-have features, cosmetic issues, minor enhancements\n\n");
        
        prompt.append("TEST CASE REQUIREMENTS:\n");
        prompt.append("- Each test case must have a clear, specific objective\n");
        prompt.append("- Test steps must be detailed enough for any tester to execute\n");
        prompt.append("- Expected results must be specific and measurable\n");
        prompt.append("- Include realistic test data and scenarios\n");
        prompt.append("- Consider different user roles and permissions\n");
        prompt.append("- Cover both success and failure paths\n");
        prompt.append("- Ensure traceability to requirements and components\n\n");
        
        prompt.append("For each test case, provide:\n");
        prompt.append("- Unique ID (TC-001, TC-002, etc.)\n");
        prompt.append("- Clear, descriptive title (max 100 characters)\n");
        prompt.append("- Detailed description explaining what is being tested and why\n");
        prompt.append("- Test type from categories above\n");
        prompt.append("- Priority (Critical/High/Medium/Low)\n");
        prompt.append("- Preconditions (system state, data setup, user permissions)\n");
        prompt.append("- Detailed test steps with specific actions and expected results\n");
        prompt.append("- Overall expected result (what success looks like)\n");
        prompt.append("- Related requirements and components for traceability\n\n");
        
        prompt.append("Return ONLY a valid JSON array with this exact structure:\n");
        prompt.append("[\n");
        prompt.append("  {\n");
        prompt.append("    \"id\": \"TC-001\",\n");
        prompt.append("    \"title\": \"User Login with Valid Credentials\",\n");
        prompt.append("    \"description\": \"Verify that users can successfully log into the system using valid username and password, and are redirected to the appropriate dashboard based on their role.\",\n");
        prompt.append("    \"testType\": \"Functional\",\n");
        prompt.append("    \"priority\": \"Critical\",\n");
        prompt.append("    \"preconditions\": [\n");
        prompt.append("      \"User account exists in the system with valid credentials\",\n");
        prompt.append("      \"Login page is accessible and loads properly\",\n");
        prompt.append("      \"Database connection is established\"\n");
        prompt.append("    ],\n");
        prompt.append("    \"testSteps\": [\n");
        prompt.append("      {\n");
        prompt.append("        \"stepNumber\": 1,\n");
        prompt.append("        \"action\": \"Navigate to the login page (e.g., /login)\",\n");
        prompt.append("        \"expectedResult\": \"Login page displays with username and password fields, login button is visible\"\n");
        prompt.append("      },\n");
        prompt.append("      {\n");
        prompt.append("        \"stepNumber\": 2,\n");
        prompt.append("        \"action\": \"Enter valid username 'testuser@example.com' in the username field\",\n");
        prompt.append("        \"expectedResult\": \"Username is accepted and displayed in the field\"\n");
        prompt.append("      },\n");
        prompt.append("      {\n");
        prompt.append("        \"stepNumber\": 3,\n");
        prompt.append("        \"action\": \"Enter valid password 'SecurePass123!' in the password field\",\n");
        prompt.append("        \"expectedResult\": \"Password is masked with asterisks or dots\"\n");
        prompt.append("      },\n");
        prompt.append("      {\n");
        prompt.append("        \"stepNumber\": 4,\n");
        prompt.append("        \"action\": \"Click the 'Login' button\",\n");
        prompt.append("        \"expectedResult\": \"System processes login request, user is authenticated and redirected to dashboard within 3 seconds\"\n");
        prompt.append("      }\n");
        prompt.append("    ],\n");
        prompt.append("    \"expectedResult\": \"User successfully logs in and is redirected to the appropriate dashboard with welcome message and user-specific content displayed\",\n");
        prompt.append("    \"relatedRequirements\": [\"REQ-001\", \"REQ-002\"],\n");
        prompt.append("    \"relatedComponents\": [\"COMP-001\", \"COMP-003\"]\n");
        prompt.append("  }\n");
        prompt.append("]\n\n");
        
        prompt.append("PROJECT CONTEXT:\n");
        prompt.append("=====================================\n");
        prompt.append(projectContext).append("\n");
        prompt.append("=====================================\n\n");
        
        // Add requirements with detailed analysis
        prompt.append("REQUIREMENTS TO TEST:\n");
        prompt.append("=====================================\n");
        if (requirements.isEmpty()) {
            prompt.append("No specific requirements provided. Generate comprehensive test cases based on common software functionality.\n\n");
        } else {
            for (Requirement req : requirements) {
                prompt.append("ID: ").append(req.getId()).append("\n");
                prompt.append("Title: ").append(req.getTitle()).append("\n");
                prompt.append("Category: ").append(req.getCategory()).append(" | Priority: ").append(req.getPriority()).append("\n");
                prompt.append("Description: ").append(req.getDescription()).append("\n");
                
                if (!req.getAcceptanceCriteria().isEmpty()) {
                    prompt.append("Acceptance Criteria:\n");
                    for (String criteria : req.getAcceptanceCriteria()) {
                        prompt.append("  ✓ ").append(criteria).append("\n");
                    }
                }
                prompt.append("---\n");
            }
        }
        prompt.append("=====================================\n\n");
        
        // Add design components with detailed analysis
        if (!components.isEmpty()) {
            prompt.append("SYSTEM COMPONENTS TO TEST:\n");
            prompt.append("=====================================\n");
            for (DesignComponent comp : components) {
                prompt.append("ID: ").append(comp.getId()).append("\n");
                prompt.append("Name: ").append(comp.getName()).append("\n");
                prompt.append("Type: ").append(comp.getType()).append("\n");
                prompt.append("Description: ").append(comp.getDescription()).append("\n");
                
                if (!comp.getInterfaces().isEmpty()) {
                    prompt.append("Interfaces/APIs:\n");
                    for (String iface : comp.getInterfaces()) {
                        prompt.append("  • ").append(iface).append("\n");
                    }
                }
                
                if (!comp.getDependencies().isEmpty()) {
                    prompt.append("Dependencies:\n");
                    for (String dep : comp.getDependencies()) {
                        prompt.append("  → ").append(dep).append("\n");
                    }
                }
                
                if (!comp.getBusinessRules().isEmpty()) {
                    prompt.append("Business Rules:\n");
                    for (String rule : comp.getBusinessRules()) {
                        prompt.append("  ⚠ ").append(rule).append("\n");
                    }
                }
                prompt.append("---\n");
            }
            prompt.append("=====================================\n\n");
        }
        
        prompt.append("INSTRUCTIONS:\n");
        prompt.append("1. Generate 15-25 comprehensive test cases covering all requirements and components\n");
        prompt.append("2. Ensure each requirement has at least 2-3 test cases (positive, negative, edge case)\n");
        prompt.append("3. Create integration tests for component interactions\n");
        prompt.append("4. Include realistic test data and user scenarios\n");
        prompt.append("5. Make test steps detailed enough for manual execution\n");
        prompt.append("6. Ensure proper traceability to requirements and components\n");
        prompt.append("7. Cover different user roles and permission levels\n");
        prompt.append("8. Include both success and failure scenarios\n\n");
        
        prompt.append("Generate comprehensive test cases now. Return ONLY the JSON array, no additional text.");
        
        return prompt.toString();
    }
    
    private String invokeCloudAIModel(String prompt) throws Exception {
        String requestBody = objectMapper.writeValueAsString(new AIRequest(prompt));
        
        InvokeModelRequest request = InvokeModelRequest.builder()
            .modelId(MODEL_ID)
            .body(SdkBytes.fromUtf8String(requestBody))
            .build();
            
        InvokeModelResponse response = bedrockClient.invokeModel(request);
        String responseBody = response.body().asUtf8String();
        
        JsonNode responseJson = objectMapper.readTree(responseBody);
        return responseJson.get("content").get(0).get("text").asText();
    }
    
    private List<TestCase> parseTestCasesFromAIResponse(String aiResponse) {
        List<TestCase> testCases = new ArrayList<>();
        
        try {
            String jsonPart = extractJsonFromResponse(aiResponse);
            JsonNode testCasesJson = objectMapper.readTree(jsonPart);
            
            for (JsonNode tcNode : testCasesJson) {
                TestCase testCase = new TestCase();
                testCase.setId(tcNode.get("id").asText());
                testCase.setTitle(tcNode.get("title").asText());
                testCase.setDescription(tcNode.get("description").asText());
                testCase.setTestType(tcNode.get("testType").asText());
                testCase.setPriority(tcNode.get("priority").asText());
                testCase.setCategory("AI-Generated");
                
                // Parse preconditions
                JsonNode preconditionsNode = tcNode.get("preconditions");
                if (preconditionsNode != null && preconditionsNode.isArray()) {
                    for (JsonNode precondition : preconditionsNode) {
                        testCase.getPreconditions().add(precondition.asText());
                    }
                }
                
                // Parse test steps
                JsonNode stepsNode = tcNode.get("testSteps");
                if (stepsNode != null && stepsNode.isArray()) {
                    for (JsonNode stepNode : stepsNode) {
                        TestStep step = new TestStep();
                        step.setStepNumber(stepNode.get("stepNumber").asInt());
                        step.setAction(stepNode.get("action").asText());
                        step.setExpectedResult(stepNode.get("expectedResult").asText());
                        testCase.getTestSteps().add(step);
                    }
                }
                
                // Set expected result
                JsonNode expectedResultNode = tcNode.get("expectedResult");
                if (expectedResultNode != null) {
                    testCase.setExpectedResult(expectedResultNode.asText());
                }
                
                // Parse related requirements
                JsonNode relatedReqsNode = tcNode.get("relatedRequirements");
                if (relatedReqsNode != null && relatedReqsNode.isArray()) {
                    for (JsonNode reqNode : relatedReqsNode) {
                        testCase.getRelatedRequirements().add(reqNode.asText());
                    }
                }
                
                // Parse related components
                JsonNode relatedCompsNode = tcNode.get("relatedComponents");
                if (relatedCompsNode != null && relatedCompsNode.isArray()) {
                    for (JsonNode compNode : relatedCompsNode) {
                        testCase.getRelatedComponents().add(compNode.asText());
                    }
                }
                
                testCases.add(testCase);
            }
        } catch (Exception e) {
            System.err.println("Error parsing AI test cases response: " + e.getMessage());
        }
        
        return testCases;
    }
    
    private String extractJsonFromResponse(String response) {
        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']') + 1;
        if (startIndex >= 0 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex);
        }
        return "[]";
    }
    
    /**
     * Request object for AI API
     */
    private static class AIRequest {
        public String anthropic_version = "bedrock-2023-05-31";
        public int max_tokens = 8000;
        public Message[] messages;
        
        public AIRequest(String prompt) {
            this.messages = new Message[]{new Message("user", prompt)};
        }
        
        private static class Message {
            public String role;
            public String content;
            
            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
    }
}