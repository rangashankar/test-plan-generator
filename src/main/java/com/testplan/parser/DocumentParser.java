package com.testplan.parser;

import com.testplan.model.Requirement;
import com.testplan.model.DesignComponent;
import java.io.File;
import java.util.List;

public interface DocumentParser {
    
    /**
     * Parse requirements from a document file
     * @param file The document file to parse
     * @return List of requirements extracted from the document
     */
    List<Requirement> parseRequirements(File file);
    
    /**
     * Parse design components from a document file
     * @param file The document file to parse
     * @return List of design components extracted from the document
     */
    List<DesignComponent> parseDesignComponents(File file);
    
    /**
     * Check if this parser supports the given file type
     * @param file The file to check
     * @return true if this parser can handle the file type
     */
    boolean supports(File file);
}