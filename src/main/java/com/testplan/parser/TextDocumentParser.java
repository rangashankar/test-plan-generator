package com.testplan.parser;

import com.testplan.model.Requirement;
import com.testplan.model.DesignComponent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedHashMap;

public class TextDocumentParser implements DocumentParser {
    
    private static final Pattern REQ_PATTERN = Pattern.compile(
        "(?i)(?:requirement|req)\\s*[:#-]?\\s*([A-Z0-9-]+)\\s*[:\\-]?\\s*(.+?)(?=\\n\\s*(?:requirement|req|design|$))", 
        Pattern.DOTALL
    );
    
    private static final Pattern DESIGN_PATTERN = Pattern.compile(
        "(?i)(?:design|component)\\s*[:#-]?\\s*([A-Z0-9-]+)\\s*[:\\-]?\\s*(.+?)(?=\\n\\s*(?:requirement|req|design|component|$))", 
        Pattern.DOTALL
    );
    
    @Override
    public List<Requirement> parseRequirements(File file) {
        try {
            String content = normalizeContent(file);
            return parseRequirementsFromContent(content);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<DesignComponent> parseDesignComponents(File file) {
        try {
            String content = normalizeContent(file);
            return parseComponentsFromContent(content);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean supports(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".txt") || name.endsWith(".md");
    }
    
    /**
     * Parse requirements from content string (used by PDF parser)
     */
    public List<Requirement> parseRequirementsFromContent(String content) {
        LinkedHashMap<String, Requirement> deduped = new LinkedHashMap<>();
        Matcher matcher = REQ_PATTERN.matcher(content);
        int syntheticId = 1;

        while (matcher.find()) {
            String id = matcher.group(1).trim();
            String description = matcher.group(2).trim();

            if (id.isEmpty()) {
                id = "REQ-AUTO-" + String.format("%03d", syntheticId++);
            }

            Requirement req = new Requirement();
            req.setId(id);
            req.setTitle(normalizeLabel(extractTitle(description)));
            req.setDescription(normalizeLabel(description));
            req.setPriority(extractPriority(description));
            req.setCategory(extractCategory(description));
            req.setAcceptanceCriteria(extractAcceptanceCriteria(description));

            String dedupeKey = normalizeKey(req.getTitle() + "|" + req.getDescription());
            if (!deduped.containsKey(dedupeKey)) {
                deduped.put(dedupeKey, req);
            }
        }

        return new ArrayList<>(deduped.values());
    }
    
    /**
     * Parse components from content string (used by PDF parser)
     */
    public List<DesignComponent> parseComponentsFromContent(String content) {
        LinkedHashMap<String, DesignComponent> deduped = new LinkedHashMap<>();
        Matcher matcher = DESIGN_PATTERN.matcher(content);
        int syntheticId = 1;

        while (matcher.find()) {
            String id = matcher.group(1).trim();
            String description = matcher.group(2).trim();

            if (id.isEmpty()) {
                id = "COMP-AUTO-" + String.format("%03d", syntheticId++);
            }

            DesignComponent component = new DesignComponent();
            component.setId(id);
            component.setName(normalizeLabel(extractTitle(description)));
            component.setDescription(normalizeLabel(description));
            component.setType(extractComponentType(description));
            component.setInterfaces(extractInterfaces(description));
            component.setDependencies(extractDependencies(description));
            component.setBusinessRules(extractBusinessRules(description));

            String dedupeKey = normalizeKey(component.getName() + "|" + component.getDescription());
            if (!deduped.containsKey(dedupeKey)) {
                deduped.put(dedupeKey, component);
            }
        }

        return new ArrayList<>(deduped.values());
    }

    private String normalizeContent(File file) throws IOException {
        String raw = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        // Normalize bullets and whitespace to help regex and downstream parsing
        String normalized = raw.replace('\u2022', '-') // bullet
                               .replace('\u2013', '-') // en dash
                               .replace('\u2014', '-') // em dash
                               .replace("\r\n", "\n")
                               .replace("\r", "\n");
        return normalized;
    }

    private String normalizeLabel(String text) {
        if (text == null) return "";
        String cleaned = text.trim()
            .replaceAll("[\\u2013\\u2014]", "-")
            .replaceAll("[:\\-\\s]+$", "")
            .replaceAll("\\s+", " ");
        return cleaned;
    }

    private String normalizeKey(String text) {
        if (text == null) return "";
        return normalizeLabel(text).toLowerCase().replaceAll("[^a-z0-9]+", "");
    }
    
    private String extractTitle(String description) {
        String[] lines = description.split("\\n");
        return lines.length > 0 ? lines[0].trim() : "Untitled";
    }
    
    private String extractPriority(String description) {
        Pattern priorityPattern = Pattern.compile("(?i)priority\\s*[:\\-]?\\s*(high|medium|low|critical)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = priorityPattern.matcher(description);
        return matcher.find() ? matcher.group(1) : "Medium";
    }
    
    private String extractCategory(String description) {
        Pattern categoryPattern = Pattern.compile("(?i)category\\s*[:\\-]?\\s*(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = categoryPattern.matcher(description);
        return matcher.find() ? matcher.group(1) : "Functional";
    }
    
    private List<String> extractAcceptanceCriteria(String description) {
        List<String> criteria = new ArrayList<>();
        Pattern criteriaPattern = Pattern.compile("(?i)(?:acceptance criteria|criteria)\\s*[:\\-]?\\s*(.+?)(?=\\n\\s*\\w+:|$)", Pattern.DOTALL);
        Matcher matcher = criteriaPattern.matcher(description);
        
        if (matcher.find()) {
            String criteriaText = matcher.group(1);
            String[] lines = criteriaText.split("\\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && (line.startsWith("-") || line.startsWith("*") || line.matches("\\d+\\."))) {
                    criteria.add(line.replaceFirst("^[-*]\\s*|^\\d+\\.\\s*", ""));
                }
            }
        }
        
        return criteria;
    }
    
    private String extractComponentType(String description) {
        Pattern typePattern = Pattern.compile("(?i)type\\s*[:\\-]?\\s*(UI|API|Database|Service|Component)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = typePattern.matcher(description);
        return matcher.find() ? matcher.group(1) : "Component";
    }
    
    private List<String> extractInterfaces(String description) {
        return extractListItems(description, "(?i)interfaces?\\s*[:\\-]?");
    }
    
    private List<String> extractDependencies(String description) {
        return extractListItems(description, "(?i)dependencies\\s*[:\\-]?");
    }
    
    private List<String> extractBusinessRules(String description) {
        return extractListItems(description, "(?i)(?:business rules?|rules?)\\s*[:\\-]?");
    }
    
    private List<String> extractListItems(String description, String headerPattern) {
        List<String> items = new ArrayList<>();
        Pattern pattern = Pattern.compile(headerPattern + "\\s*(.+?)(?=\\n\\s*\\w+:|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(description);
        
        if (matcher.find()) {
            String itemsText = matcher.group(1);
            String[] lines = itemsText.split("\\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && (line.startsWith("-") || line.startsWith("*") || line.matches("\\d+\\."))) {
                    items.add(line.replaceFirst("^[-*]\\s*|^\\d+\\.\\s*", ""));
                }
            }
        }
        
        return items;
    }
}
