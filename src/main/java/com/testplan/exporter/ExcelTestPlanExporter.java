package com.testplan.exporter;

import com.testplan.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ExcelTestPlanExporter implements TestPlanExporter {
    
    @Override
    public void export(TestPlan testPlan, File outputFile) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // Create test plan overview sheet
            createOverviewSheet(workbook, testPlan);
            
            // Create test cases sheet
            createTestCasesSheet(workbook, testPlan);
            
            // Create test strategy sheet
            createTestStrategySheet(workbook, testPlan);
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
            
        } catch (IOException e) {
            throw new Exception("Failed to export test plan to Excel: " + e.getMessage(), e);
        }
    }
    
    private void createOverviewSheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Test Plan Overview");
        
        // Create header style
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // Test Plan Information
        createHeaderRow(sheet, rowNum++, "Test Plan Information", headerStyle);
        createDataRow(sheet, rowNum++, "ID:", testPlan.getId(), dataStyle);
        createDataRow(sheet, rowNum++, "Title:", testPlan.getTitle(), dataStyle);
        createDataRow(sheet, rowNum++, "Version:", testPlan.getVersion(), dataStyle);
        createDataRow(sheet, rowNum++, "Created By:", testPlan.getCreatedBy(), dataStyle);
        createDataRow(sheet, rowNum++, "Created Date:", 
                     testPlan.getCreatedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), dataStyle);
        createDataRow(sheet, rowNum++, "Description:", testPlan.getDescription(), dataStyle);
        
        rowNum++; // Empty row
        
        // Objectives
        createHeaderRow(sheet, rowNum++, "Test Objectives", headerStyle);
        for (String objective : testPlan.getObjectives()) {
            createDataRow(sheet, rowNum++, "•", objective, dataStyle);
        }
        
        rowNum++; // Empty row
        
        // Scope
        createHeaderRow(sheet, rowNum++, "Test Scope", headerStyle);
        for (String scope : testPlan.getScope()) {
            createDataRow(sheet, rowNum++, "•", scope, dataStyle);
        }
        
        rowNum++; // Empty row
        
        // Out of Scope
        createHeaderRow(sheet, rowNum++, "Out of Scope", headerStyle);
        for (String outOfScope : testPlan.getOutOfScope()) {
            createDataRow(sheet, rowNum++, "•", outOfScope, dataStyle);
        }
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void createTestCasesSheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Test Cases");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Test Case ID", "Title", "Description", "Type", "Priority", 
                           "Category", "Preconditions", "Test Steps", "Expected Result", 
                           "Related Requirements", "Related Components"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Add test case data
        int rowNum = 1;
        for (TestCase testCase : testPlan.getTestCases()) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(testCase.getId());
            row.createCell(1).setCellValue(testCase.getTitle());
            row.createCell(2).setCellValue(testCase.getDescription());
            row.createCell(3).setCellValue(testCase.getTestType());
            row.createCell(4).setCellValue(testCase.getPriority());
            row.createCell(5).setCellValue(testCase.getCategory());
            row.createCell(6).setCellValue(String.join("; ", testCase.getPreconditions()));
            row.createCell(7).setCellValue(formatTestSteps(testCase.getTestSteps()));
            row.createCell(8).setCellValue(testCase.getExpectedResult());
            row.createCell(9).setCellValue(String.join(", ", testCase.getRelatedRequirements()));
            row.createCell(10).setCellValue(String.join(", ", testCase.getRelatedComponents()));
            
            // Apply data style to all cells
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    cell.setCellStyle(dataStyle);
                }
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createTestStrategySheet(Workbook workbook, TestPlan testPlan) {
        Sheet sheet = workbook.createSheet("Test Strategy");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        TestStrategy strategy = testPlan.getTestStrategy();
        if (strategy == null) return;
        
        int rowNum = 0;
        
        // Test Types
        createHeaderRow(sheet, rowNum++, "Test Types", headerStyle);
        for (String testType : strategy.getTestTypes()) {
            createDataRow(sheet, rowNum++, "•", testType, dataStyle);
        }
        
        rowNum++; // Empty row
        
        // Test Levels
        createHeaderRow(sheet, rowNum++, "Test Levels", headerStyle);
        for (String testLevel : strategy.getTestLevels()) {
            createDataRow(sheet, rowNum++, "•", testLevel, dataStyle);
        }
        
        rowNum++; // Empty row
        
        // Approach
        createHeaderRow(sheet, rowNum++, "Test Approach", headerStyle);
        createDataRow(sheet, rowNum++, "", strategy.getApproach(), dataStyle);
        
        rowNum++; // Empty row
        
        // Tools
        createHeaderRow(sheet, rowNum++, "Test Tools", headerStyle);
        for (String tool : strategy.getTools()) {
            createDataRow(sheet, rowNum++, "•", tool, dataStyle);
        }
        
        rowNum++; // Empty row
        
        // Environments
        createHeaderRow(sheet, rowNum++, "Test Environments", headerStyle);
        for (String environment : strategy.getEnvironments()) {
            createDataRow(sheet, rowNum++, "•", environment, dataStyle);
        }
        
        rowNum++; // Empty row
        
        // Risk Assessment
        createHeaderRow(sheet, rowNum++, "Risk Assessment", headerStyle);
        createDataRow(sheet, rowNum++, "", strategy.getRiskAssessment(), dataStyle);
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    private void createHeaderRow(Sheet sheet, int rowNum, String header, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(header);
        cell.setCellStyle(style);
    }
    
    private void createDataRow(Sheet sheet, int rowNum, String label, String value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value != null ? value : "");
        valueCell.setCellStyle(style);
    }
    
    private String formatTestSteps(java.util.List<TestStep> testSteps) {
        StringBuilder sb = new StringBuilder();
        for (TestStep step : testSteps) {
            sb.append(step.getStepNumber()).append(". ")
              .append(step.getAction()).append(" -> ")
              .append(step.getExpectedResult()).append("\n");
        }
        return sb.toString().trim();
    }
    
    @Override
    public String getFileExtension() {
        return "xlsx";
    }
}