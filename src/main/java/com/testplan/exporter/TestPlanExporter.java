package com.testplan.exporter;

import com.testplan.model.TestPlan;
import java.io.File;

public interface TestPlanExporter {
    
    /**
     * Export test plan to a file
     * @param testPlan The test plan to export
     * @param outputFile The output file
     * @throws Exception if export fails
     */
    void export(TestPlan testPlan, File outputFile) throws Exception;
    
    /**
     * Get the file extension this exporter supports
     * @return file extension (e.g., "xlsx", "json", "html")
     */
    String getFileExtension();
}