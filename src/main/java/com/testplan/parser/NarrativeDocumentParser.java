package com.testplan.parser;

import com.testplan.model.Requirement;
import com.testplan.model.DesignComponent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Parser for narrative documents (press releases, product descriptions, feature announcements) 
 * that extracts requirements and design elements from unstructured text
 */
public class NarrativeDocumentParser implements DocumentParser {
    
    // Patterns to identify features and capabilities in narrative text
    private static final Pattern FEATURE_PATTERN = Pattern.compile(
        "(?i)(?:key features?|capabilities?|includes?)[:\\s]*([^\\n]+(?:\\n[•\\-\\*]\\s*[^\\n]+)*)", 
        Pattern.DOTALL
    );
    
    private static final Pattern BULLET_FEATURE_PATTERN = Pattern.compile(
        "(?i)[•\\-\\*]\\s*([^:\\n]+):\\s*([^\\n]+)"
    );
    
    private static final Pattern QA_PATTERN = Pattern.compile(
        "(?i)Q:\\s*([^\\n]+)\\n+A:\\s*([^\\n]+(?:\\n(?!Q:)[^\\n]+)*)", 
        Pattern.DOTALL
    );
    
    private static final Pattern INTEGRATION_PATTERN = Pattern.compile(
        "(?i)(integrat|work|connect|seamless)[^\\n]*(?:with|using)\\s+(\\w+(?:\\s+\\w+)*)"
    );
    
    @Override
    public List<Requirement> parseRequirements(File file) {
        List<Requirement> requirements = new ArrayList<>();
        
        try {
            String content = Files.readString(file.toPath());
            return parseRequirementsFromContent(content);
        } catch (IOException e) {
            System.err.println("Error reading narrative document: " + e.getMessage());
        }
        
        return requirements;
    }
    
    /**
     * Parse requirements from content string (used by PDF parser)
     */
    public List<Requirement> parseRequirementsFromContent(String content) {
        List<Requirement> requirements = new ArrayList<>();
        
        // Extract requirements from key features section
        List<Requirement> keyFeatureReqs = parseKeyFeatures(content);
        requirements.addAll(keyFeatureReqs);
        
        // Extract requirements from Q&A sections
        List<Requirement> qaReqs = parseQARequirements(content);
        requirements.addAll(qaReqs);
        
        // Extract requirements from narrative text
        List<Requirement> narrativeReqs = parseNarrativeRequirements(content);
        requirements.addAll(narrativeReqs);
        
        return requirements;
    }
    
    @Override
    public List<DesignComponent> parseDesignComponents(File file) {
        List<DesignComponent> components = new ArrayList<>();
        
        try {
            String content = Files.readString(file.toPath());
            return parseDesignComponentsFromContent(content);
        } catch (IOException e) {
            System.err.println("Error reading narrative document: " + e.getMessage());
        }
        
        return components;
    }
    
    /**
     * Parse design components from content string (used by PDF parser)
     */
    public List<DesignComponent> parseDesignComponentsFromContent(String content) {
        List<DesignComponent> components = new ArrayList<>();
        
        // Extract design components from integration mentions
        components.addAll(parseIntegrationComponents(content));
        
        // Extract components from technical descriptions in Q&A sections
        components.addAll(parseTechnicalComponents(content));
        
        return components;
    }
    
    @Override
    public boolean supports(File file) {
        String name = file.getName().toLowerCase();
        return name.contains("narrative") || name.contains("press") || name.contains("announcement") ||
               name.contains("product") || name.contains("feature") ||
               (name.endsWith(".txt") || name.endsWith(".md"));
    }
    
    private List<Requirement> parseKeyFeatures(String content) {
        List<Requirement> requirements = new ArrayList<>();
        
        // Look for the key features section
        Pattern keyFeaturesSection = Pattern.compile(
            "(?i)key features[^:]*:([^\\n]+(?:\\n[•\\-\\*]\\s*[^\\n]+)*)", 
            Pattern.DOTALL
        );
        
        Matcher sectionMatcher = keyFeaturesSection.matcher(content);
        int reqCounter = 1;
        
        if (sectionMatcher.find()) {
            String featuresText = sectionMatcher.group(1);
            
            // Parse bullet points with feature descriptions
            Pattern bulletPattern = Pattern.compile("(?m)^[•\\-\\*]\\s*([^:\\n]+):\\s*([^\\n]+)");
            Matcher bulletMatcher = bulletPattern.matcher(featuresText);
            
            while (bulletMatcher.find()) {
                String featureName = bulletMatcher.group(1).trim();
                String featureDescription = bulletMatcher.group(2).trim();
                
                Requirement req = new Requirement();
                req.setId("NAR-REQ-" + String.format("%03d", reqCounter++));
                req.setTitle(featureName);
                req.setDescription(featureDescription);
                req.setPriority(determinePriority(featureName, featureDescription));
                req.setCategory("Functional");
                
                // Generate acceptance criteria from the description
                req.getAcceptanceCriteria().addAll(generateAcceptanceCriteria(featureDescription));
                
                requirements.add(req);
            }
        }
        
        // Also look for simple bullet points without colons
        Pattern simpleBulletPattern = Pattern.compile("(?m)^[•\\-\\*]\\s*([^\\n]{20,})");
        Matcher simpleMatcher = simpleBulletPattern.matcher(content);
        
        while (simpleMatcher.find()) {
            String featureText = simpleMatcher.group(1).trim();
            
            // Skip if it's too short or looks like a question
            if (featureText.length() < 20 || featureText.contains("?")) {
                continue;
            }
            
            Requirement req = new Requirement();
            req.setId("NAR-REQ-" + String.format("%03d", reqCounter++));
            req.setTitle(extractFeatureTitle(featureText));
            req.setDescription(featureText);
            req.setPriority(determinePriority("", featureText));
            req.setCategory("Functional");
            
            req.getAcceptanceCriteria().addAll(generateAcceptanceCriteria(featureText));
            
            requirements.add(req);
        }
        
        return requirements;
    }
    
    private String extractFeatureTitle(String featureText) {
        // Take the first few words or up to the first verb
        String[] words = featureText.split("\\s+");
        if (words.length <= 4) {
            return featureText;
        }
        
        // Look for the first verb and take everything before it
        for (int i = 1; i < Math.min(words.length, 6); i++) {
            String word = words[i].toLowerCase();
            if (word.matches("(uses|analyzes|provides|suggests|monitors|works|integrates|leverages)")) {
                return String.join(" ", java.util.Arrays.copyOfRange(words, 0, i));
            }
        }
        
        // Default to first 4 words
        return String.join(" ", java.util.Arrays.copyOfRange(words, 0, Math.min(4, words.length)));
    }
    
    private List<Requirement> parseQARequirements(String content) {
        List<Requirement> requirements = new ArrayList<>();
        Matcher qaMatcher = QA_PATTERN.matcher(content);
        
        int reqCounter = 100; // Start at 100 to differentiate from feature requirements
        while (qaMatcher.find()) {
            String question = qaMatcher.group(1).trim();
            String answer = qaMatcher.group(2).trim();
            
            // Only extract requirements from Q&A that describe system capabilities
            if (isSystemCapabilityQA(question, answer)) {
                Requirement req = new Requirement();
                req.setId("NAR-REQ-" + String.format("%03d", reqCounter++));
                req.setTitle(extractCapabilityTitle(question));
                req.setDescription(extractCapabilityDescription(answer));
                req.setPriority(determineQAPriority(question, answer));
                req.setCategory(determineCategory(question, answer));
                
                // Generate meaningful acceptance criteria
                req.getAcceptanceCriteria().addAll(generateQAAcceptanceCriteria(question, answer));
                
                requirements.add(req);
            }
        }
        
        return requirements;
    }
    
    private List<Requirement> parseNarrativeRequirements(String content) {
        List<Requirement> requirements = new ArrayList<>();
        
        // Look for performance metrics mentioned in the narrative
        Pattern metricsPattern = Pattern.compile(
            "(?i)(\\d+)%\\s+(accuracy|satisfaction|success)\\s+(?:rate|in)\\s+([^\\n\\.]+)"
        );
        
        Matcher metricsMatcher = metricsPattern.matcher(content);
        int reqCounter = 200;
        
        while (metricsMatcher.find()) {
            String percentage = metricsMatcher.group(1);
            String metric = metricsMatcher.group(2);
            String context = metricsMatcher.group(3);
            
            Requirement req = new Requirement();
            req.setId("NAR-REQ-" + String.format("%03d", reqCounter++));
            req.setTitle("Performance Requirement: " + metric);
            req.setDescription("System must achieve " + percentage + "% " + metric + " " + context);
            req.setPriority("High");
            req.setCategory("Performance");
            
            req.getAcceptanceCriteria().add("Achieve minimum " + percentage + "% " + metric);
            req.getAcceptanceCriteria().add("Measure and report " + metric + " metrics");
            req.getAcceptanceCriteria().add("Continuously monitor performance in " + context);
            
            requirements.add(req);
        }
        
        return requirements;
    }
    
    private List<DesignComponent> parseIntegrationComponents(String content) {
        List<DesignComponent> components = new ArrayList<>();
        
        // Look for explicit integration mentions
        Matcher integrationMatcher = INTEGRATION_PATTERN.matcher(content);
        int compCounter = 1;
        
        while (integrationMatcher.find()) {
            String integrationTarget = integrationMatcher.group(2).trim();
            
            DesignComponent component = new DesignComponent();
            component.setId("NAR-COMP-" + String.format("%03d", compCounter++));
            component.setName(integrationTarget + " Integration");
            component.setType("Integration");
            component.setDescription("Integration component for connecting with " + integrationTarget);
            
            component.getInterfaces().add("Integration API for " + integrationTarget);
            component.getDependencies().add(integrationTarget + " Service");
            component.getBusinessRules().add("Must maintain compatibility with " + integrationTarget);
            
            components.add(component);
        }
        
        // Add common system components that typically exist in modern applications
        String[] commonComponents = {
            "User Authentication System",
            "Recommendation Engine", 
            "Notification Service",
            "Data Analytics Platform",
            "Mobile Application Interface",
            "Web Application Interface"
        };
        
        for (String componentName : commonComponents) {
            // Check if the content mentions this type of component
            String lowerContent = content.toLowerCase();
            String lowerComponentName = componentName.toLowerCase();
            
            if (shouldIncludeComponent(lowerContent, lowerComponentName)) {
                DesignComponent component = new DesignComponent();
                component.setId("NAR-COMP-" + String.format("%03d", compCounter++));
                component.setName(componentName);
                component.setType(getComponentType(componentName));
                component.setDescription("System component for " + componentName.toLowerCase());
                
                // Add realistic dependencies and interfaces
                addComponentDependencies(component, componentName);
                addComponentInterfaces(component, componentName);
                addComponentBusinessRules(component, componentName);
                
                components.add(component);
            }
        }
        
        return components;
    }
    
    private boolean shouldIncludeComponent(String content, String componentName) {
        if (componentName.contains("authentication") || componentName.contains("user")) {
            return content.contains("user") || content.contains("login") || content.contains("account");
        } else if (componentName.contains("recommendation")) {
            return content.contains("recommend") || content.contains("suggest") || content.contains("predict");
        } else if (componentName.contains("notification")) {
            return content.contains("notification") || content.contains("alert") || content.contains("notify");
        } else if (componentName.contains("analytics")) {
            return content.contains("analyz") || content.contains("data") || content.contains("pattern");
        } else if (componentName.contains("mobile")) {
            return content.contains("mobile") || content.contains("app") || content.contains("ios") || content.contains("android");
        } else if (componentName.contains("web")) {
            return content.contains("website") || content.contains("web") || content.contains("browser");
        }
        return false;
    }
    
    private String getComponentType(String componentName) {
        if (componentName.toLowerCase().contains("service")) return "Service";
        if (componentName.toLowerCase().contains("interface") || componentName.toLowerCase().contains("application")) return "UI";
        if (componentName.toLowerCase().contains("engine") || componentName.toLowerCase().contains("platform")) return "Service";
        if (componentName.toLowerCase().contains("system")) return "System";
        return "Component";
    }
    
    private void addComponentDependencies(DesignComponent component, String componentName) {
        String lowerName = componentName.toLowerCase();
        
        if (lowerName.contains("authentication")) {
            component.getDependencies().add("User Database");
            component.getDependencies().add("Session Management Service");
            component.getDependencies().add("Security Token Service");
        } else if (lowerName.contains("recommendation")) {
            component.getDependencies().add("User Behavior Analytics");
            component.getDependencies().add("Product Catalog Service");
            component.getDependencies().add("Machine Learning Platform");
        } else if (lowerName.contains("notification")) {
            component.getDependencies().add("Message Queue Service");
            component.getDependencies().add("User Preference Service");
            component.getDependencies().add("External Notification Providers");
        } else if (lowerName.contains("analytics")) {
            component.getDependencies().add("Data Collection Service");
            component.getDependencies().add("Data Storage System");
            component.getDependencies().add("Reporting Engine");
        } else if (lowerName.contains("mobile") || lowerName.contains("web")) {
            component.getDependencies().add("Backend API Gateway");
            component.getDependencies().add("Authentication Service");
            component.getDependencies().add("Content Delivery Network");
        }
    }
    
    private void addComponentInterfaces(DesignComponent component, String componentName) {
        String lowerName = componentName.toLowerCase();
        
        if (lowerName.contains("authentication")) {
            component.getInterfaces().add("REST API for user login/logout");
            component.getInterfaces().add("Token validation endpoint");
            component.getInterfaces().add("User session management API");
        } else if (lowerName.contains("recommendation")) {
            component.getInterfaces().add("Recommendation API endpoint");
            component.getInterfaces().add("User preference update API");
            component.getInterfaces().add("Product similarity API");
        } else if (lowerName.contains("notification")) {
            component.getInterfaces().add("Send notification API");
            component.getInterfaces().add("Notification preference API");
            component.getInterfaces().add("Notification status API");
        } else if (lowerName.contains("analytics")) {
            component.getInterfaces().add("Data ingestion API");
            component.getInterfaces().add("Analytics query API");
            component.getInterfaces().add("Report generation API");
        } else if (lowerName.contains("mobile") || lowerName.contains("web")) {
            component.getInterfaces().add("User interface components");
            component.getInterfaces().add("API integration layer");
            component.getInterfaces().add("State management interface");
        }
    }
    
    private void addComponentBusinessRules(DesignComponent component, String componentName) {
        String lowerName = componentName.toLowerCase();
        
        if (lowerName.contains("authentication")) {
            component.getBusinessRules().add("Must enforce strong password policies");
            component.getBusinessRules().add("Must implement secure session management");
            component.getBusinessRules().add("Must comply with data privacy regulations");
        } else if (lowerName.contains("recommendation")) {
            component.getBusinessRules().add("Must respect user privacy preferences");
            component.getBusinessRules().add("Must provide explainable recommendations");
            component.getBusinessRules().add("Must handle cold start scenarios");
        } else if (lowerName.contains("notification")) {
            component.getBusinessRules().add("Must respect user notification preferences");
            component.getBusinessRules().add("Must implement rate limiting to prevent spam");
            component.getBusinessRules().add("Must provide opt-out mechanisms");
        } else if (lowerName.contains("analytics")) {
            component.getBusinessRules().add("Must anonymize personal data");
            component.getBusinessRules().add("Must ensure data accuracy and integrity");
            component.getBusinessRules().add("Must comply with data retention policies");
        } else if (lowerName.contains("mobile") || lowerName.contains("web")) {
            component.getBusinessRules().add("Must be responsive and accessible");
            component.getBusinessRules().add("Must provide consistent user experience");
            component.getBusinessRules().add("Must handle offline scenarios gracefully");
        }
    }
    
    private List<DesignComponent> parseTechnicalComponents(String content) {
        List<DesignComponent> components = new ArrayList<>();
        
        // Look for mentions of AI/ML, APIs, databases, etc.
        String[] technicalTerms = {
            "machine learning", "AI", "artificial intelligence", 
            "API", "database", "service", "algorithm", "model"
        };
        
        int compCounter = 100;
        for (String term : technicalTerms) {
            Pattern termPattern = Pattern.compile(
                "(?i)" + Pattern.quote(term) + "\\s+([^\\n\\.]+)", 
                Pattern.CASE_INSENSITIVE
            );
            
            Matcher termMatcher = termPattern.matcher(content);
            if (termMatcher.find()) {
                String context = termMatcher.group(1);
                
                DesignComponent component = new DesignComponent();
                component.setId("NAR-COMP-" + String.format("%03d", compCounter++));
                component.setName(capitalizeFirst(term) + " Component");
                component.setType("Service");
                component.setDescription(capitalizeFirst(term) + " component that " + context);
                
                component.getInterfaces().add("REST API for " + term);
                component.getBusinessRules().add("Must handle " + term + " operations efficiently");
                
                components.add(component);
            }
        }
        
        return components;
    }
    
    private String determinePriority(String featureName, String description) {
        String combined = (featureName + " " + description).toLowerCase();
        
        if (combined.contains("core") || combined.contains("key") || 
            combined.contains("primary") || combined.contains("essential")) {
            return "High";
        } else if (combined.contains("nice") || combined.contains("optional") || 
                   combined.contains("future")) {
            return "Low";
        }
        return "Medium";
    }
    
    private String determineCategory(String question, String answer) {
        String combined = (question + " " + answer).toLowerCase();
        
        if (combined.contains("privacy") || combined.contains("security") || 
            combined.contains("protect")) {
            return "Security";
        } else if (combined.contains("performance") || combined.contains("speed") || 
                   combined.contains("accuracy")) {
            return "Performance";
        } else if (combined.contains("integrate") || combined.contains("connect")) {
            return "Integration";
        }
        return "Functional";
    }
    
    private List<String> generateAcceptanceCriteria(String description) {
        List<String> criteria = new ArrayList<>();
        
        // Generate basic acceptance criteria based on the description
        criteria.add("Feature must be implemented as described: " + description);
        criteria.add("Feature must be accessible to all eligible users");
        criteria.add("Feature must perform reliably under normal usage conditions");
        
        // Add specific criteria based on keywords
        if (description.toLowerCase().contains("predict")) {
            criteria.add("Prediction accuracy must meet specified thresholds");
        }
        if (description.toLowerCase().contains("notification")) {
            criteria.add("Notifications must be delivered in a timely manner");
        }
        if (description.toLowerCase().contains("voice")) {
            criteria.add("Voice recognition must achieve acceptable accuracy rates");
        }
        
        return criteria;
    }
    
    private boolean containsCapabilityStatement(String answer) {
        String lower = answer.toLowerCase();
        return lower.contains("can") || lower.contains("will") || 
               lower.contains("support") || lower.contains("allow") ||
               lower.contains("enable") || lower.contains("provide");
    }
    
    private String extractCapabilityTitle(String question) {
        // Remove question words and clean up
        return question.replaceAll("(?i)^(how|what|when|where|why|can|will|does)\\s+", "")
                      .replaceAll("\\?$", "")
                      .trim();
    }
    
    private String extractCapabilityDescription(String answer) {
        // Take the first sentence or up to 200 characters
        String[] sentences = answer.split("\\.");
        if (sentences.length > 0 && sentences[0].length() < 200) {
            return sentences[0].trim() + ".";
        }
        return answer.length() > 200 ? answer.substring(0, 200) + "..." : answer;
    }
    
    private boolean isSystemCapabilityQA(String question, String answer) {
        String lowerQuestion = question.toLowerCase();
        String lowerAnswer = answer.toLowerCase();
        
        // Include questions about system capabilities
        if (lowerQuestion.contains("how does") && 
            (lowerQuestion.contains("protect") || lowerQuestion.contains("work") || 
             lowerQuestion.contains("integrate") || lowerQuestion.contains("accurate"))) {
            return true;
        }
        
        // Skip questions that are just informational
        if (lowerQuestion.contains("what happens") || lowerQuestion.contains("can i") ||
            lowerQuestion.contains("will smart cart")) {
            return false;
        }
        
        return containsCapabilityStatement(answer) && answer.length() > 50;
    }
    
    private String determineQAPriority(String question, String answer) {
        String combined = (question + " " + answer).toLowerCase();
        
        if (combined.contains("privacy") || combined.contains("security") || 
            combined.contains("accurate") || combined.contains("protect")) {
            return "High";
        }
        return "Medium";
    }
    
    private List<String> generateQAAcceptanceCriteria(String question, String answer) {
        List<String> criteria = new ArrayList<>();
        String lowerAnswer = answer.toLowerCase();
        
        if (lowerAnswer.contains("accuracy") || lowerAnswer.contains("accurate")) {
            // Extract accuracy percentages if mentioned
            Pattern accuracyPattern = Pattern.compile("(\\d+)%\\s+accuracy");
            Matcher matcher = accuracyPattern.matcher(lowerAnswer);
            if (matcher.find()) {
                criteria.add("System must achieve " + matcher.group(1) + "% accuracy");
            } else {
                criteria.add("System must meet specified accuracy requirements");
            }
        }
        
        if (lowerAnswer.contains("privacy") || lowerAnswer.contains("security")) {
            criteria.add("Must comply with privacy and security standards");
            criteria.add("User data must be protected and encrypted");
        }
        
        if (lowerAnswer.contains("integrate") || lowerAnswer.contains("work with")) {
            criteria.add("Must integrate seamlessly with specified systems");
            criteria.add("Integration must be reliable and performant");
        }
        
        if (criteria.isEmpty()) {
            criteria.add("Feature must work as described in Q&A response");
            criteria.add("User experience must be intuitive and reliable");
        }
        
        return criteria;
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}