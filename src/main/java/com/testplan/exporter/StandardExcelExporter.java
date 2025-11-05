package com.testplan.exporter;

import com.testplan.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * IEEE 829 Standard Excel Test Plan Exporter
 * Creates professional test plan documents following industry standards
 */
public class StandardExcelExporter implements TestPlanExporter {
    
    private static final String[] TEST_CASE_HEADERS = {
        "Test Case ID", "Test Case Title", "Test Objective", "Priority", "Severity",
        "Test Type", "Test Level", "Category", "Preconditions", "Test Steps",
        "Expected Result", "Test Data", "Environment", "Estimated Time",
        "Author", "Created Date", "Status", "Related Requirements", "Comments"
    };
    
    @Override
    public String getFileExtension() {
        return "xlsx";
    }
    
    @Override
    public void export(TestPlan testPlan, File outputFile) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // Create IEEE 829 standard sheets
            createCoverSheet(workbook, testPlan);
            createTestPlanSheet(workbook, testPlan);
            createTestCasesSheet(workbook, testPlan);
            createTestStrategySheet(workbook, testPlan);
            createRequirementsTraceabilitySheet(workbook, testPlan);
            createTestExecutionSheet(workbook, testPlan);
            createMetricsSheet(workbook, testPlan);
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
            
        } catch (IOException e) {
            throw new Exception("Failed to export standardized test plan: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create professional cover sheet
     */
    private void createCoverSheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Cover Page");
        
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 2;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("TEST PLAN DOCUMENT");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 1, 6));
        
        rowNum += 2;
        
        // Project Information
        createSectionHeader(sheet, rowNum++, "PROJECT INFORMATION", headerStyle);
        createInfoRow(sheet, rowNum++, "Project Name:", testPlan.getTitle(), dataStyle);
        createInfoRow(sheet, rowNum++, "Document ID:", testPlan.getId(), dataStyle);
        createInfoRow(sheet, rowNum++, "Version:", testPlan.getVersion(), dataStyle);
        createInfoRow(sheet, rowNum++, "Status:", testPlan.getStatus(), dataStyle);
        createInfoRow(sheet, rowNum++, "Created By:", testPlan.getCreatedBy(), dataStyle);
        createInfoRow(sheet, rowNum++, "Created Date:", 
                     testPlan.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dataStyle);
        
        rowNum += 2;
        
        // Document Control
        createSectionHeader(sheet, rowNum++, "DOCUMENT CONTROL", headerStyle);
        createInfoRow(sheet, rowNum++, "Test Manager:", testPlan.getTestManager(), dataStyle);
        createInfoRow(sheet, rowNum++, "Reviewed By:", testPlan.getReviewedBy(), dataStyle);
        createInfoRow(sheet, rowNum++, "Approved By:", testPlan.getApprovedBy(), dataStyle);
        createInfoRow(sheet, rowNum++, "Approval Status:", testPlan.getApprovalStatus(), dataStyle);
        
        // Auto-size columns
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Create comprehensive test plan sheet following IEEE 829
     */
    private void createTestPlanSheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Test Plan");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle subHeaderStyle = createSubHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // 1. Test Plan Identifier
        createSectionHeader(sheet, rowNum++, "1. TEST PLAN IDENTIFIER", headerStyle);
        createInfoRow(sheet, rowNum++, "Test Plan ID:", testPlan.getId(), dataStyle);
        createInfoRow(sheet, rowNum++, "Version:", testPlan.getVersion(), dataStyle);
        rowNum++;
        
        // 2. Introduction
        createSectionHeader(sheet, rowNum++, "2. INTRODUCTION", headerStyle);
        createSubSection(sheet, rowNum++, "2.1 Purpose", subHeaderStyle);
        createMultiLineText(sheet, rowNum++, testPlan.getPurpose() != null ? 
                           testPlan.getPurpose() : "Define the scope, approach, resources, and schedule of testing activities.", dataStyle);
        rowNum++;
        
        createSubSection(sheet, rowNum++, "2.2 Background", subHeaderStyle);
        createMultiLineText(sheet, rowNum++, testPlan.getBackground() != null ? 
                           testPlan.getBackground() : testPlan.getDescription(), dataStyle);
        rowNum++;
        
        // 3. Test Items
        createSectionHeader(sheet, rowNum++, "3. TEST ITEMS", headerStyle);
        rowNum = createListSection(sheet, rowNum, testPlan.getTestItems(), dataStyle);
        
        // 4. Features Not to be Tested
        createSectionHeader(sheet, rowNum++, "4. FEATURES NOT TO BE TESTED", headerStyle);
        rowNum = createListSection(sheet, rowNum, testPlan.getFeaturesNotTested(), dataStyle);
        
        // 5. Approach
        createSectionHeader(sheet, rowNum++, "5. APPROACH", headerStyle);
        createSubSection(sheet, rowNum++, "5.1 Test Levels", subHeaderStyle);
        rowNum = createListSection(sheet, rowNum, testPlan.getTestLevels(), dataStyle);
        
        createSubSection(sheet, rowNum++, "5.2 Test Types", subHeaderStyle);
        rowNum = createListSection(sheet, rowNum, testPlan.getTestTypes(), dataStyle);
        
        // 6. Item Pass/Fail Criteria
        createSectionHeader(sheet, rowNum++, "6. ITEM PASS/FAIL CRITERIA", headerStyle);
        createSubSection(sheet, rowNum++, "6.1 Pass Criteria", subHeaderStyle);
        rowNum = createListSection(sheet, rowNum, testPlan.getPassCriteria(), dataStyle);
        
        createSubSection(sheet, rowNum++, "6.2 Fail Criteria", subHeaderStyle);
        rowNum = createListSection(sheet, rowNum, testPlan.getFailCriteria(), dataStyle);
        
        // 7. Test Deliverables
        createSectionHeader(sheet, rowNum++, "7. TEST DELIVERABLES", headerStyle);
        rowNum = createListSection(sheet, rowNum, testPlan.getTestDeliverables(), dataStyle);
        
        // 8. Environmental Needs
        createSectionHeader(sheet, rowNum++, "8. ENVIRONMENTAL NEEDS", headerStyle);
        createInfoRow(sheet, rowNum++, "Test Environment:", testPlan.getTestEnvironment(), dataStyle);
        
        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Create standardized test cases sheet
     */
    private void createTestCasesSheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Test Cases");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < TEST_CASE_HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(TEST_CASE_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Add test cases
        int rowNum = 1;
        for (TestCase testCase : testPlan.getTestCases()) {
            Row row = sheet.createRow(rowNum++);
            
            int colNum = 0;
            row.createCell(colNum++).setCellValue(testCase.getId());
            row.createCell(colNum++).setCellValue(testCase.getTitle());
            row.createCell(colNum++).setCellValue(testCase.getObjective() != null ? testCase.getObjective() : testCase.getDescription());
            row.createCell(colNum++).setCellValue(testCase.getPriority());
            row.createCell(colNum++).setCellValue(testCase.getSeverity());
            row.createCell(colNum++).setCellValue(testCase.getTestType());
            row.createCell(colNum++).setCellValue(testCase.getTestLevel());
            row.createCell(colNum++).setCellValue(testCase.getCategory());
            row.createCell(colNum++).setCellValue(String.join("; ", testCase.getPreconditions()));
            row.createCell(colNum++).setCellValue(formatTestSteps(testCase.getTestSteps()));
            row.createCell(colNum++).setCellValue(testCase.getExpectedResult());
            row.createCell(colNum++).setCellValue(String.join("; ", testCase.getTestData()));
            row.createCell(colNum++).setCellValue(testCase.getTestEnvironment());
            row.createCell(colNum++).setCellValue(testCase.getEstimatedTime());
            row.createCell(colNum++).setCellValue(testCase.getAuthor());
            row.createCell(colNum++).setCellValue(testCase.getCreatedDate());
            row.createCell(colNum++).setCellValue(testCase.getStatus());
            row.createCell(colNum++).setCellValue(String.join("; ", testCase.getRelatedRequirements()));
            row.createCell(colNum++).setCellValue(testCase.getComments());
            
            // Apply data style to all cells
            for (int i = 0; i < colNum; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < TEST_CASE_HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Freeze header row
        sheet.createFreezePane(0, 1);
    }
    
    /**
     * Create test strategy sheet
     */
    private void createTestStrategySheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Test Strategy");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        if (testPlan.getTestStrategy() != null) {
            TestStrategy strategy = testPlan.getTestStrategy();
            
            createSectionHeader(sheet, rowNum++, "TEST STRATEGY", headerStyle);
            rowNum++;
            
            createInfoRow(sheet, rowNum++, "Approach:", strategy.getApproach(), dataStyle);
            rowNum++;
            
            createSectionHeader(sheet, rowNum++, "Test Types", headerStyle);
            rowNum = createListSection(sheet, rowNum, strategy.getTestTypes(), dataStyle);
            
            createSectionHeader(sheet, rowNum++, "Test Levels", headerStyle);
            rowNum = createListSection(sheet, rowNum, strategy.getTestLevels(), dataStyle);
            
            createSectionHeader(sheet, rowNum++, "Tools", headerStyle);
            rowNum = createListSection(sheet, rowNum, strategy.getTools(), dataStyle);
            
            createSectionHeader(sheet, rowNum++, "Environments", headerStyle);
            rowNum = createListSection(sheet, rowNum, strategy.getEnvironments(), dataStyle);
            
            createSectionHeader(sheet, rowNum++, "Risk Assessment", headerStyle);
            createMultiLineText(sheet, rowNum++, strategy.getRiskAssessment(), dataStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Create requirements traceability matrix
     */
    private void createRequirementsTraceabilitySheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Requirements Traceability");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // Headers
        String[] headers = {"Requirement ID", "Test Case ID", "Test Case Title", "Status", "Coverage"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Traceability data
        int rowNum = 1;
        for (TestCase testCase : testPlan.getTestCases()) {
            for (String reqId : testCase.getRelatedRequirements()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(reqId);
                row.createCell(1).setCellValue(testCase.getId());
                row.createCell(2).setCellValue(testCase.getTitle());
                row.createCell(3).setCellValue(testCase.getStatus());
                row.createCell(4).setCellValue("Covered");
                
                for (int i = 0; i < 5; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Create test execution tracking sheet
     */
    private void createTestExecutionSheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Test Execution");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        String[] headers = {"Test Case ID", "Title", "Priority", "Status", "Executed By", 
                           "Execution Date", "Actual Result", "Defects", "Comments"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (TestCase testCase : testPlan.getTestCases()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(testCase.getId());
            row.createCell(1).setCellValue(testCase.getTitle());
            row.createCell(2).setCellValue(testCase.getPriority());
            row.createCell(3).setCellValue(testCase.getStatus());
            row.createCell(4).setCellValue(testCase.getExecutedBy());
            row.createCell(5).setCellValue(testCase.getExecutionDate());
            row.createCell(6).setCellValue(testCase.getActualResult());
            row.createCell(7).setCellValue(String.join("; ", testCase.getRelatedDefects()));
            row.createCell(8).setCellValue(testCase.getComments());
            
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Create metrics and reporting sheet
     */
    private void createMetricsSheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Metrics & Reports");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // Test Summary
        createSectionHeader(sheet, rowNum++, "TEST EXECUTION SUMMARY", headerStyle);
        rowNum++;
        
        long totalTests = testPlan.getTestCases().size();
        long passedTests = testPlan.getTestCases().stream().filter(tc -> "Pass".equals(tc.getStatus())).count();
        long failedTests = testPlan.getTestCases().stream().filter(tc -> "Fail".equals(tc.getStatus())).count();
        long blockedTests = testPlan.getTestCases().stream().filter(tc -> "Blocked".equals(tc.getStatus())).count();
        long notExecuted = testPlan.getTestCases().stream().filter(tc -> "Not Executed".equals(tc.getStatus())).count();
        
        createInfoRow(sheet, rowNum++, "Total Test Cases:", String.valueOf(totalTests), dataStyle);
        createInfoRow(sheet, rowNum++, "Passed:", String.valueOf(passedTests), dataStyle);
        createInfoRow(sheet, rowNum++, "Failed:", String.valueOf(failedTests), dataStyle);
        createInfoRow(sheet, rowNum++, "Blocked:", String.valueOf(blockedTests), dataStyle);
        createInfoRow(sheet, rowNum++, "Not Executed:", String.valueOf(notExecuted), dataStyle);
        
        if (totalTests > 0) {
            double passRate = (double) passedTests / totalTests * 100;
            createInfoRow(sheet, rowNum++, "Pass Rate:", String.format("%.1f%%", passRate), dataStyle);
        }
        
        rowNum += 2;
        
        // Metrics to Collect
        createSectionHeader(sheet, rowNum++, "METRICS TO COLLECT", headerStyle);
        rowNum = createListSection(sheet, rowNum, testPlan.getMetricsToCollect(), dataStyle);
        
        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    // Helper methods for styling and formatting
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createSubHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        return style;
    }
    
    private void createSectionHeader(Sheet sheet, int rowNum, String text, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 5));
    }
    
    private void createSubSection(Sheet sheet, int rowNum, String text, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
    }
    
    private void createInfoRow(Sheet sheet, int rowNum, String label, String value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value != null ? value : "");
        valueCell.setCellStyle(style);
    }
    
    private void createMultiLineText(Sheet sheet, int rowNum, String text, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(text != null ? text : "");
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 5));
    }
    
    private int createListSection(Sheet sheet, int startRow, java.util.List<String> items, CellStyle style) {
        int rowNum = startRow;
        if (items != null && !items.isEmpty()) {
            for (String item : items) {
                Row row = sheet.createRow(rowNum++);
                Cell cell = row.createCell(0);
                cell.setCellValue("• " + item);
                cell.setCellStyle(style);
            }
        } else {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue("• Not specified");
            cell.setCellStyle(style);
        }
        return rowNum + 1;
    }
    
    private String formatTestSteps(java.util.List<TestStep> steps) {
        if (steps == null || steps.isEmpty()) {
            return "No steps defined";
        }
        
        StringBuilder sb = new StringBuilder();
        for (TestStep step : steps) {
            sb.append(step.getStepNumber()).append(". ")
              .append(step.getAction()).append(" -> ")
              .append(step.getExpectedResult()).append("\n");
        }
        return sb.toString();
    }
}