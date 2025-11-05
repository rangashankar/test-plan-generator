package com.testplan.parser;

import com.testplan.model.Requirement;
import com.testplan.model.DesignComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;

/**
 * AI-powered document parser using AWS Bedrock
 * Can analyze any type of document and extract requirements and design components
 */
public class BedrockDocumentParser implements DocumentParser {
    
    private BedrockRuntimeClient bedrockClient;
    private ObjectMapper objectMapper;
    private static final String MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";
    
    public BedrockDocumentParser() {
        this.bedrockClient = BedrockRuntimeClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public List<Requirement> parseRequirements(File file) {
        try {
            String content = extractContent(file);
            String prompt = buildRequirementsPrompt(content);
            String response = invokeBedrockModel(prompt);
            return parseRequirementsFromAIResponse(response);
        } catch (Exception e) {
            System.err.println("Error parsing requirements with Bedrock: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<DesignComponent> parseDesignComponents(File file) {
        try {
            String content = extractContent(file);
            String prompt = buildDesignPrompt(content);
            String response = invokeBedrockModel(prompt);
            return parseDesignComponentsFromAIResponse(response);
        } catch (Exception e) {
            System.err.println("Error parsing design components with Bedrock: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean supports(File file) {
        // Bedrock parser can handle any text-based document
        String name = file.getName().toLowerCase();
        return name.endsWith(".txt") || name.endsWith(".md") || 
               name.endsWith(".doc") || name.endsWith(".docx") ||
               name.endsWith(".pdf");
    }
    
    /**
     * Extract content from file based on file type
     */
    private String extractContent(File file) throws Exception {
        String fileName = file.getName().toLowerCase();
        
        if (fileName.endsWith(".pdf")) {
            // Use PDFBox to extract text from PDF
            return extractTextFromPDF(file);
        } else {
            // For text files, read directly
            return Files.readString(file.toPath());
        }
    }
    
    /**
     * Extract text from PDF file using PDFBox
     */
    private String extractTextFromPDF(File pdfFile) throws Exception {
        try (org.apache.pdfbox.pdmodel.PDDocument document = 
             org.apache.pdfbox.pdmodel.PDDocument.load(pdfFile)) {
            org.apache.pdfbox.text.PDFTextStripper stripper = 
                new org.apache.pdfbox.text.PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
  private String buildRequirementsPrompt(String documentContent) {
    StringBuilder p = new StringBuilder();

    p.append("You are a senior QA requirements engineer with expertise in IEEE 830 / ISO 29148 standards.\n");
    p.append("Analyze the document below and extract ALL functional and non-functional requirements.\n\n");

    p.append("CRITICAL INSTRUCTIONS:\n");
    p.append("- Use ONLY requirement statements that are supported by the document.\n");
    p.append("- If inference is required, set \"inferred\": true and include a brief justification in \"notes\".\n");
    p.append("- Do NOT invent new requirements.\n");
    p.append("- Output MUST be strictly valid JSON. No comments, no markdown, no extra text.\n");
    p.append("- Requirements must be deduplicated and consolidated.\n");
    p.append("- IDs must be sequential: REQ-001, REQ-002, …\n\n");

    p.append("REQUIREMENT CATEGORIES:\n");
    p.append("- Functional\n");
    p.append("- Performance\n");
    p.append("- Security\n");
    p.append("- Integration\n");
    p.append("- UI/UX\n");
    p.append("- Data\n");
    p.append("- Operational\n\n");

    p.append("PRIORITY SCALE:\n");
    p.append("- Critical: System fails without this\n");
    p.append("- High: Required for core user journeys\n");
    p.append("- Medium: Enhances reliability or usability\n");
    p.append("- Low: Nice-to-have\n\n");

    p.append("OUTPUT FORMAT:\n");
    p.append("Return a JSON array. Each requirement object MUST match this schema:\n\n");
    p.append("[\n");
    p.append("  {\n");
    p.append("    \"id\": \"REQ-001\",\n");
    p.append("    \"title\": \"Concise requirement title (<= 80 chars)\",\n");
    p.append("    \"description\": \"Detailed requirement statement, including context and purpose.\",\n");
    p.append("    \"priority\": \"Critical | High | Medium | Low\",\n");
    p.append("    \"category\": \"Functional | Security | Performance | Integration | UI/UX | Data | Operational\",\n");
    p.append("    \"acceptanceCriteria\": [\n");
    p.append("      \"Each acceptance criterion must be specific, measurable, and testable.\",\n");
    p.append("      \"Must include expected behavior AND edge / error conditions.\",\n");
    p.append("      \"Avoid vague terms (ex: fast, robust, user-friendly). Use numeric targets instead.\"\n");
    p.append("    ],\n");
    p.append("    \"businessValue\": \"Why this requirement matters for users or the organization.\",\n");
    p.append("    \"sourceEvidence\": [\"Direct quotes or line references from the document\"],\n");
    p.append("    \"inferred\": false,\n");
    p.append("    \"notes\": \"Clarifications if inferred OR trade-offs or assumptions\"\n");
    p.append("  }\n");
    p.append("]\n\n");

    p.append("EVIDENCE RULES:\n");
    p.append("- For explicit requirements (\"must\", \"shall\", \"should\"), mark inferred=false.\n");
    p.append("- For derived / implied requirements, set inferred=true and provide short rationale.\n");
    p.append("- If a requirement cannot be supported or inferred, DO NOT include it.\n\n");

    p.append("Now extract ALL requirements.\n");
    p.append("Return ONLY the JSON array. Ensure valid JSON (no trailing commas).\n\n");

    p.append("DOCUMENT TO ANALYZE:\n");
    p.append("=====================================\n");
    p.append(documentContent);
    p.append("\n=====================================\n");

    return p.toString();
}

    
   private String buildDesignPrompt(String documentContent) {
    StringBuilder p = new StringBuilder();
    p.append("You are a senior software architect... distributed systems.\n");
    p.append("Analyze the document and extract ALL components present. Use ONLY evidence in the document; if inferred, set \"inferred\": true and explain in \"notes\".\n\n");

    p.append("RULES:\n");
    p.append("- Output ONLY valid UTF-8 JSON (no markdown, no comments, no prose).\n");
    p.append("- Max 200 components; dedupe synonyms; stable IDs COMP-001..N.\n");
    p.append("- Dependencies must reference existing IDs; otherwise empty.\n");
    p.append("- Do NOT invent endpoints/schemas not evidenced.\n");
    p.append("- Order by id ascending.\n\n");

    p.append("TYPE ENUM: [API,Service,UI,Database,Integration,Security,Infrastructure,External]\n");
    p.append("Normalize protocol/method (HTTPS/HTTP/gRPC/AMQP/Kafka/S3; GET/POST/PUT/PATCH/DELETE/STREAM).\n\n");

    p.append("RETURN JSON ARRAY OF OBJECTS WITH THIS SHAPE:\n[\n  {\n");
    p.append("    \"id\": \"COMP-001\",\n");
    p.append("    \"name\": \"User Authentication Service\",\n");
    p.append("    \"type\": \"API\",\n");
    p.append("    \"description\": \"Microservice handling authentication and JWT issuance.\",\n");
    p.append("    \"interfaces\": [\n");
    p.append("      {\"direction\":\"exposes\",\"protocol\":\"HTTPS\",\"method\":\"POST\",\"path\":\"/auth/login\",\"requestSchemaRef\":\"AuthLoginRequest\",\"responseSchemaRef\":\"AuthLoginResponse\"}\n");
    p.append("    ],\n");
    p.append("    \"datastores\": [\n");
    p.append("      {\"engine\":\"PostgreSQL\",\"name\":\"user_db\",\"tablesOrCollections\":[\"users\",\"sessions\"],\"primaryKeys\":[\"users.id\",\"sessions.id\"]}\n");
    p.append("    ],\n");
    p.append("    \"dependencies\": [\"COMP-003\"],\n");
    p.append("    \"businessRules\": [\"MFA required for admins\"],\n");
    p.append("    \"nonFunctional\": {\"sla\":\"p99 < 250ms\",\"availability\":\"99.9%\",\"rps\":500,\"burstRps\":2000},\n");
    p.append("    \"security\": {\"authzModel\":\"RBAC\",\"token\":\"JWT\",\"encryptionAtRest\":true,\"encryptionInTransit\":true,\"dataClassification\":\"Confidential-PII\",\"pii\":[\"email\"]},\n");
    p.append("    \"monitoring\": {\"logs\":[\"auth.login.success\"],\"metrics\":[\"rps\",\"latency.p99\",\"error_rate\"],\"alerts\":[\"elevated_login_failures\"]},\n");
    p.append("    \"owners\": [\"team-auth-platform\"],\n");
    p.append("    \"region\": [\"us-east-1\"],\n");
    p.append("    \"evidence\": [\"Security > Auth\"],\n");
    p.append("    \"notes\": \"\",\n");
    p.append("    \"inferred\": false\n");
    p.append("  }\n]\n\n");

    p.append("DOCUMENT TO ANALYZE:\n=====================================\n");
    p.append(documentContent);
    p.append("\n=====================================\n\n");
    p.append("Return ONLY the JSON array as specified. Ensure valid JSON.\n");

    return p.toString();
}

    
    private String invokeBedrockModel(String prompt) throws Exception {
        // Build the request payload for Claude
        String requestBody = objectMapper.writeValueAsString(new ClaudeRequest(prompt));
        
        InvokeModelRequest request = InvokeModelRequest.builder()
            .modelId(MODEL_ID)
            .body(SdkBytes.fromUtf8String(requestBody))
            .build();
        
        InvokeModelResponse response = bedrockClient.invokeModel(request);
        String responseBody = response.body().asUtf8String();
        
        // Parse Claude's response
        JsonNode responseJson = objectMapper.readTree(responseBody);
        return responseJson.get("content").get(0).get("text").asText();
    }
    
    private List<Requirement> parseRequirementsFromAIResponse(String aiResponse) {
        List<Requirement> requirements = new ArrayList<>();
        
        try {
            // Extract JSON from AI response (it might have extra text)
            String jsonPart = extractJsonFromResponse(aiResponse);
            JsonNode requirementsJson = objectMapper.readTree(jsonPart);
            
            for (JsonNode reqNode : requirementsJson) {
                Requirement req = new Requirement();
                req.setId(reqNode.get("id").asText());
                req.setTitle(reqNode.get("title").asText());
                req.setDescription(reqNode.get("description").asText());
                req.setPriority(reqNode.get("priority").asText());
                req.setCategory(reqNode.get("category").asText());
                
                // Parse acceptance criteria
                JsonNode criteriaNode = reqNode.get("acceptanceCriteria");
                if (criteriaNode != null && criteriaNode.isArray()) {
                    for (JsonNode criterion : criteriaNode) {
                        req.getAcceptanceCriteria().add(criterion.asText());
                    }
                }
                
                requirements.add(req);
            }
        } catch (Exception e) {
            System.err.println("Error parsing AI response for requirements: " + e.getMessage());
        }
        
        return requirements;
    }
    
    private List<DesignComponent> parseDesignComponentsFromAIResponse(String aiResponse) {
        List<DesignComponent> components = new ArrayList<>();
        
        try {
            String jsonPart = extractJsonFromResponse(aiResponse);
            JsonNode componentsJson = objectMapper.readTree(jsonPart);
            
            for (JsonNode compNode : componentsJson) {
                DesignComponent comp = new DesignComponent();
                comp.setId(compNode.get("id").asText());
                comp.setName(compNode.get("name").asText());
                comp.setType(compNode.get("type").asText());
                comp.setDescription(compNode.get("description").asText());
                
                // Parse interfaces
                JsonNode interfacesNode = compNode.get("interfaces");
                if (interfacesNode != null && interfacesNode.isArray()) {
                    for (JsonNode interfaceNode : interfacesNode) {
                        comp.getInterfaces().add(interfaceNode.asText());
                    }
                }
                
                // Parse dependencies
                JsonNode dependenciesNode = compNode.get("dependencies");
                if (dependenciesNode != null && dependenciesNode.isArray()) {
                    for (JsonNode depNode : dependenciesNode) {
                        comp.getDependencies().add(depNode.asText());
                    }
                }
                
                // Parse business rules
                JsonNode rulesNode = compNode.get("businessRules");
                if (rulesNode != null && rulesNode.isArray()) {
                    for (JsonNode ruleNode : rulesNode) {
                        comp.getBusinessRules().add(ruleNode.asText());
                    }
                }
                
                components.add(comp);
            }
        } catch (Exception e) {
            System.err.println("Error parsing AI response for design components: " + e.getMessage());
        }
        
        return components;
    }
    
    private String extractJsonFromResponse(String response) {
        // Find JSON array in the response
        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']') + 1;
        
        if (startIndex >= 0 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex);
        }
        
        // If no array found, return empty array
        return "[]";
    }
    
    /**
     * Request object for Claude API
     */
    private static class ClaudeRequest {
        public String anthropic_version = "bedrock-2023-05-31";
        public int max_tokens = 4000;
        public Message[] messages;
        
        public ClaudeRequest(String prompt) {
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