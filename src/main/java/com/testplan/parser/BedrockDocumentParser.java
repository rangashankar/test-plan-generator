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
    private static final int MAX_AI_RETRIES = 2;
    
    public BedrockDocumentParser() {
        this.bedrockClient = BedrockRuntimeClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    private String reinforceJsonReminder(String basePrompt) {
        return basePrompt + "\n\nREMINDER: Previous response was invalid. Return ONLY a valid JSON array matching the schema. Do not include prose or explanations.";
    }
    private List<Requirement> parseRequirementsWithFallback(File file) {
        try {
            DocumentParser fallback = DocumentParserFactory.createParser(file, false);
            return fallback.parseRequirements(file);
        } catch (Exception fallbackError) {
            System.err.println("   ❌ Fallback requirement parser failed: " + fallbackError.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<DesignComponent> parseComponentsWithFallback(File file) {
        try {
            DocumentParser fallback = DocumentParserFactory.createParser(file, false);
            return fallback.parseDesignComponents(file);
        } catch (Exception fallbackError) {
            System.err.println("   ❌ Fallback design parser failed: " + fallbackError.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Requirement> parseRequirements(File file) {
        try {
            String content = extractContent(file);
            List<Requirement> requirements = attemptRequirementExtraction(content);
            if (requirements.isEmpty()) {
                System.out.println("   ♻️  AI parser returned no requirements, falling back to traditional parser.");
                return parseRequirementsWithFallback(file);
            }
            return requirements;
        } catch (Exception e) {
            System.err.println("Error parsing requirements with Bedrock: " + e.getMessage());
            return parseRequirementsWithFallback(file);
        }
    }
    
    @Override
    public List<DesignComponent> parseDesignComponents(File file) {
        try {
            String content = extractContent(file);
            List<DesignComponent> components = attemptComponentExtraction(content);
            if (components.isEmpty()) {
                System.out.println("   ♻️  AI parser returned no design components, using fallback parser.");
                return parseComponentsWithFallback(file);
            }
            return components;
        } catch (Exception e) {
            System.err.println("Error parsing design components with Bedrock: " + e.getMessage());
            return parseComponentsWithFallback(file);
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
    
    private List<Requirement> attemptRequirementExtraction(String content) {
        List<Requirement> requirements = new ArrayList<>();
        String basePrompt = buildRequirementsPrompt(content);
        
        for (int attempt = 1; attempt <= MAX_AI_RETRIES; attempt++) {
            String prompt = attempt == 1 ? basePrompt : reinforceJsonReminder(basePrompt);
            try {
                String response = invokeBedrockModel(prompt);
                requirements = parseRequirementsFromAIResponse(response);
                if (!requirements.isEmpty()) {
                    if (attempt > 1) {
                        System.out.println("   ✅ AI retry succeeded for requirements.");
                    }
                    return requirements;
                }
                System.out.println("   ⚠️  AI response contained no requirements (attempt " + attempt + ")");
            } catch (Exception e) {
                System.err.println("   ⚠️  AI requirement extraction attempt " + attempt + " failed: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }
    
    private List<DesignComponent> attemptComponentExtraction(String content) {
        List<DesignComponent> components = new ArrayList<>();
        String basePrompt = buildDesignPrompt(content);
        
        for (int attempt = 1; attempt <= MAX_AI_RETRIES; attempt++) {
            String prompt = attempt == 1 ? basePrompt : reinforceJsonReminder(basePrompt);
            try {
                String response = invokeBedrockModel(prompt);
                components = parseDesignComponentsFromAIResponse(response);
                if (!components.isEmpty()) {
                    if (attempt > 1) {
                        System.out.println("   ✅ AI retry succeeded for design components.");
                    }
                    return components;
                }
                System.out.println("   ⚠️  AI response contained no design components (attempt " + attempt + ")");
            } catch (Exception e) {
                System.err.println("   ⚠️  AI component extraction attempt " + attempt + " failed: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }
    
  private String buildRequirementsPrompt(String documentContent) {
    StringBuilder p = new StringBuilder();

    p.append("You are a senior QA requirements engineer. Extract the smallest complete set of requirements that the document explicitly supports (cap at 40 items).\n\n");

    p.append("Guidelines:\n");
    p.append("- Use only evidence from the document. When inferring, set \"inferred\": true and explain briefly in \"notes\".\n");
    p.append("- Keep IDs sequential (REQ-001…). Deduplicate overlapping statements.\n");
    p.append("- Priorities: Critical (system fails), High (core journeys), Medium (quality/reliability), Low (nice-to-have).\n");
    p.append("- Categories: Functional, Performance, Security, Integration, UI/UX, Data, Operational.\n");
    p.append("- Output must be valid JSON only—no prose, markdown, or comments.\n\n");

    p.append("Return a JSON array where each object uses the following keys:\n");
    p.append("{\n");
    p.append("  \"id\": \"REQ-001\",\n");
    p.append("  \"title\": \"<=80 char summary\",\n");
    p.append("  \"description\": \"Full requirement statement with context\",\n");
    p.append("  \"priority\": \"Critical|High|Medium|Low\",\n");
    p.append("  \"category\": \"Functional|Performance|Security|Integration|UI/UX|Data|Operational\",\n");
    p.append("  \"acceptanceCriteria\": [\"Specific, measurable, and testable clauses (2-4 entries)\"],\n");
    p.append("  \"businessValue\": \"User or business impact\",\n");
    p.append("  \"sourceEvidence\": [\"Quotes or section references\"],\n");
    p.append("  \"inferred\": false,\n");
    p.append("  \"notes\": \"Rationale when inferred or any assumptions\"\n");
    p.append("}\n\n");

    p.append("Return ONLY the JSON array (no trailing commas).\n\n");
    p.append("DOCUMENT TO ANALYZE:\n");
    p.append("=====================================\n");
    p.append(documentContent);
    p.append("\n=====================================\n");

    return p.toString();
}

    
   private String buildDesignPrompt(String documentContent) {
    StringBuilder p = new StringBuilder();
    p.append("You are a software architect. Extract every distinct component mentioned in the document (max 150) using only supported evidence.\n\n");
    p.append("Rules:\n");
    p.append("- Stable IDs COMP-001..N ordered sequentially.\n");
    p.append("- Types limited to: API, Service, UI, Database, Integration, Security, Infrastructure, External.\n");
    p.append("- Dependencies must reference other component IDs; omit when unknown.\n");
    p.append("- Output must be valid JSON only. No free-form text.\n\n");

    p.append("Each component object should contain:\n");
    p.append("{\n");
    p.append("  \"id\": \"COMP-001\",\n");
    p.append("  \"name\": \"User Authentication Service\",\n");
    p.append("  \"type\": \"API\",\n");
    p.append("  \"description\": \"One-sentence summary of responsibility\",\n");
    p.append("  \"interfaces\": [\"HTTPS POST /auth/login\", \"Kafka topic auth.events\"],\n");
    p.append("  \"datastores\": [\"PostgreSQL user_db\"],\n");
    p.append("  \"dependencies\": [\"COMP-003\"],\n");
    p.append("  \"businessRules\": [\"Requires MFA for admin accounts\"],\n");
    p.append("  \"nonFunctional\": \"Key SLA, throughput, or availability targets\",\n");
    p.append("  \"security\": \"Notable auth/encryption constraints\",\n");
    p.append("  \"evidence\": [\"Section or quote\"],\n");
    p.append("  \"inferred\": false,\n");
    p.append("  \"notes\": \"Short justification when inferred\"\n");
    p.append("}\n\n");

    p.append("Return ONLY the JSON array.\n\n");
    p.append("DOCUMENT TO ANALYZE:\n=====================================\n");
    p.append(documentContent);
    p.append("\n=====================================\n");

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
            String jsonPart = extractJsonArray(aiResponse);
            JsonNode requirementsJson = objectMapper.readTree(jsonPart);
            if (!requirementsJson.isArray()) {
                return requirements;
            }
            
            for (JsonNode reqNode : requirementsJson) {
                if (!reqNode.hasNonNull("id") || !reqNode.hasNonNull("title")) {
                    continue;
                }
                Requirement req = new Requirement();
                req.setId(reqNode.get("id").asText());
                req.setTitle(reqNode.get("title").asText());
                req.setDescription(reqNode.path("description").asText(""));
                req.setPriority(reqNode.path("priority").asText("Medium"));
                req.setCategory(reqNode.path("category").asText("Functional"));
                
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
            String jsonPart = extractJsonArray(aiResponse);
            JsonNode componentsJson = objectMapper.readTree(jsonPart);
            if (!componentsJson.isArray()) {
                return components;
            }
            
            for (JsonNode compNode : componentsJson) {
                if (!compNode.hasNonNull("id") || !compNode.hasNonNull("name")) {
                    continue;
                }
                DesignComponent comp = new DesignComponent();
                comp.setId(compNode.get("id").asText());
                comp.setName(compNode.get("name").asText());
                comp.setType(compNode.path("type").asText("Component"));
                comp.setDescription(compNode.path("description").asText(""));
                
                JsonNode interfacesNode = compNode.get("interfaces");
                if (interfacesNode != null && interfacesNode.isArray()) {
                    for (JsonNode interfaceNode : interfacesNode) {
                        comp.getInterfaces().add(interfaceNode.asText());
                    }
                }
                
                JsonNode dependenciesNode = compNode.get("dependencies");
                if (dependenciesNode != null && dependenciesNode.isArray()) {
                    for (JsonNode depNode : dependenciesNode) {
                        comp.getDependencies().add(depNode.asText());
                    }
                }
                
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
    
    private String extractJsonArray(String response) {
        if (response == null) {
            return "[]";
        }
        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']');
        if (startIndex >= 0 && endIndex >= startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
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
