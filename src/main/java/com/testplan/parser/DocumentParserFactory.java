package com.testplan.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Factory class to create the appropriate document parser based on file content and name
 */
public class DocumentParserFactory {
    
    /**
     * Creates the appropriate parser for the given file
     * @param file The file to be parsed
     * @return The most suitable DocumentParser implementation
     */
    public static DocumentParser createParser(File file) {
        return createParser(file, false);
    }
    
    /**
     * Creates the appropriate parser for the given file with AI option
     * @param file The file to be parsed
     * @param useAI Whether to use AI-powered parsing
     * @return The most suitable DocumentParser implementation
     */
    public static DocumentParser createParser(File file, boolean useAI) {
        if (file == null || !file.exists()) {
            return new TextDocumentParser(); // Default fallback
        }
        
        // If AI is requested and cloud AI credentials are available, use AI parser
        if (useAI && isCloudAIConfigured()) {
            return new BedrockDocumentParser(); // Cloud AI document parser
        }
        
        String fileName = file.getName().toLowerCase();
        
        // Check if it's a PDF document
        if (fileName.endsWith(".pdf")) {
            return new PDFDocumentParser();
        }
        
        // Check if it's a narrative document based on filename
        if (fileName.contains("narrative") || fileName.contains("press") || 
            fileName.contains("announcement") || fileName.contains("product")) {
            return new NarrativeDocumentParser();
        }
        
        // Check file content for PRFAQ indicators (only for text files)
        if (fileName.endsWith(".txt") || fileName.endsWith(".md")) {
            try {
                String content = Files.readString(file.toPath());
                if (isNarrativeContent(content)) {
                    return new NarrativeDocumentParser();
                }
            } catch (IOException e) {
                System.err.println("Warning: Could not read file content for parser detection: " + e.getMessage());
            }
        }
        
        // Default to structured text parser
        return new TextDocumentParser();
    }
    
    /**
     * Check if cloud AI credentials are configured for AI service access
     */
    private static boolean isCloudAIConfigured() {
        try {
            // Check for cloud AI service credentials
            String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
            String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
            String profile = System.getenv("AWS_PROFILE");
            return (accessKey != null && secretKey != null) || profile != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Determines if the content appears to be a narrative document
     * @param content The file content to analyze
     * @return true if content appears to be narrative format
     */
    private static boolean isNarrativeContent(String content) {
        String lowerContent = content.toLowerCase();
        
        // Look for narrative document indicators
        boolean hasPress = lowerContent.contains("press release") || 
                          lowerContent.contains("for immediate release") ||
                          lowerContent.contains("announces");
        
        boolean hasQA = lowerContent.contains("frequently asked questions") || 
                       lowerContent.contains("q:") && lowerContent.contains("a:");
        
        boolean hasProductDesc = lowerContent.contains("features") || 
                               lowerContent.contains("capabilities") ||
                               lowerContent.contains("benefits");
        
        boolean hasNarrative = !lowerContent.contains("requirement:") && 
                              !lowerContent.contains("design:");
        
        // If it has narrative indicators and doesn't have structured requirements
        return (hasPress || hasQA || hasProductDesc) && hasNarrative;
    }
}