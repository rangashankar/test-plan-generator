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
        prompt.append("You are a senior QA architect. Produce IEEE 829 style manual test cases that are concise, executable, and cover the documented scope.\n\n");

        prompt.append("Constraints:\n");
        prompt.append("- Deliver 12-18 total test cases.\n");
        prompt.append("- Aim for 1-2 focused cases per requirement (happy path + one negative/boundary). Only add more if the requirement is complex.\n");
        prompt.append("- Include integration/interaction tests whenever components have dependencies, plus at least one security/privacy and one performance/resilience scenario.\n");
        prompt.append("- Every test must cite related requirements/components for traceability.\n");
        prompt.append("- Steps must be specific enough for a manual tester (4-8 steps each) with clear expected outcomes and data/role references.\n");
        prompt.append("- Use priority levels Critical/High/Medium/Low to reflect business impact.\n\n");

        prompt.append("Test case JSON schema:\n");
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
        
        prompt.append("Execution checklist:\n");
        prompt.append("1. Cover functional, integration, negative, boundary, and at least one security/performance scenario.\n");
        prompt.append("2. Call out required data, user roles, or environmental preconditions explicitly.\n");
        prompt.append("3. Use realistic but concise test data values (e.g., sample emails, IDs, payloads).\n");
        prompt.append("4. Keep language actionable (\"Click\", \"Submit\", \"Verify ...\").\n");
        prompt.append("5. Return ONLY the JSON array with no leading/trailing commentary.\n\n");
        
        prompt.append("Generate the test cases now.");
        
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
