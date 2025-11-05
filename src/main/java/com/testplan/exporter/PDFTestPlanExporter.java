package com.testplan.exporter;

import com.testplan.model.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Professional PDF Test Plan Exporter
 * Creates IEEE 829 compliant test plans in PDF format
 */
public class PDFTestPlanExporter implements TestPlanExporter {
    
    private PdfFont titleFont;
    private PdfFont headerFont;
    private PdfFont subHeaderFont;
    private PdfFont normalFont;
    private PdfFont boldFont;
    
    @Override
    public String getFileExtension() {
        return "pdf";
    }
    
    @Override
    public void export(TestPlan testPlan, File outputFile) throws Exception {
        try {
            // Initialize fonts
            initializeFonts();
            
            // Create PDF document
            PdfWriter writer = new PdfWriter(new FileOutputStream(outputFile));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Set document properties
            pdfDoc.getDocumentInfo().setTitle("Test Plan - " + testPlan.getTitle());
            pdfDoc.getDocumentInfo().setAuthor(testPlan.getCreatedBy());
            pdfDoc.getDocumentInfo().setSubject("IEEE 829 Test Plan Document");
            
            // Create the document content
            createCoverPage(document, testPlan);
            document.add(new AreaBreak());
            
            createTableOfContents(document, testPlan);
            document.add(new AreaBreak());
            
            createTestPlanSections(document, testPlan);
            document.add(new AreaBreak());
            
            createTestCasesSection(document, testPlan);
            document.add(new AreaBreak());
            
            createRequirementsTraceability(document, testPlan);
            
            // Close document
            document.close();
            
        } catch (Exception e) {
            throw new Exception("Failed to export test plan to PDF: " + e.getMessage(), e);
        }
    }
    
    private void initializeFonts() throws Exception {
        titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        subHeaderFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    }
    
    private void createCoverPage(Document document, TestPlan testPlan) {
        // Title
        Paragraph title = new Paragraph("TEST PLAN DOCUMENT")
            .setFont(titleFont)
            .setFontSize(24)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(100)
            .setMarginBottom(50);
        document.add(title);
        
        // Project name
        Paragraph projectName = new Paragraph(testPlan.getTitle())
            .setFont(headerFont)
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30);
        document.add(projectName);
        
        // Document info table
        Table infoTable = new Table(2);
        infoTable.setWidth(UnitValue.createPercentValue(60));
        infoTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
        infoTable.setMarginTop(50);
        
        addInfoRow(infoTable, "Document ID:", testPlan.getId());
        addInfoRow(infoTable, "Version:", testPlan.getVersion());
        addInfoRow(infoTable, "Status:", testPlan.getStatus());
        addInfoRow(infoTable, "Created By:", testPlan.getCreatedBy());
        addInfoRow(infoTable, "Created Date:", 
                  testPlan.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        addInfoRow(infoTable, "Test Manager:", testPlan.getTestManager());
        addInfoRow(infoTable, "Approval Status:", testPlan.getApprovalStatus());
        
        document.add(infoTable);
        
        // Footer
        Paragraph footer = new Paragraph("IEEE 829 Standard Compliant")
            .setFont(normalFont)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(100)
            .setFontColor(ColorConstants.GRAY);
        document.add(footer);
    }
    
    private void createTableOfContents(Document document, TestPlan testPlan) {
        Paragraph tocTitle = new Paragraph("TABLE OF CONTENTS")
            .setFont(headerFont)
            .setFontSize(16)
            .setMarginBottom(20);
        document.add(tocTitle);
        
        String[] sections = {
            "1. Test Plan Identifier",
            "2. Introduction", 
            "3. Test Items",
            "4. Features Not to be Tested",
            "5. Approach",
            "6. Item Pass/Fail Criteria",
            "7. Test Deliverables",
            "8. Environmental Needs",
            "9. Responsibilities",
            "10. Schedule",
            "11. Risks and Contingencies",
            "12. Test Cases",
            "13. Requirements Traceability Matrix"
        };
        
        for (String section : sections) {
            Paragraph tocItem = new Paragraph(section)
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginBottom(5);
            document.add(tocItem);
        }
    }
    
    private void createTestPlanSections(Document document, TestPlan testPlan) {
        // 1. Test Plan Identifier
        addSection(document, "1. TEST PLAN IDENTIFIER");
        addParagraph(document, "Test Plan ID: " + testPlan.getId());
        addParagraph(document, "Version: " + testPlan.getVersion());
        addParagraph(document, "Status: " + testPlan.getStatus());
        addSpace(document);
        
        // 2. Introduction
        addSection(document, "2. INTRODUCTION");
        addSubSection(document, "2.1 Purpose");
        addParagraph(document, testPlan.getPurpose() != null ? 
                    testPlan.getPurpose() : 
                    "This document defines the scope, approach, resources, and schedule of testing activities for " + testPlan.getTitle() + ".");
        
        addSubSection(document, "2.2 Background");
        addParagraph(document, testPlan.getBackground() != null ? 
                    testPlan.getBackground() : testPlan.getDescription());
        addSpace(document);
        
        // 3. Test Items
        addSection(document, "3. TEST ITEMS");
        addList(document, testPlan.getTestItems(), "Items to be tested will be identified based on requirements analysis.");
        addSpace(document);
        
        // 4. Features Not to be Tested
        addSection(document, "4. FEATURES NOT TO BE TESTED");
        addList(document, testPlan.getFeaturesNotTested(), "All features not explicitly listed in test items are out of scope.");
        addSpace(document);
        
        // 5. Approach
        addSection(document, "5. APPROACH");
        addSubSection(document, "5.1 Test Levels");
        addList(document, testPlan.getTestLevels(), null);
        
        addSubSection(document, "5.2 Test Types");
        addList(document, testPlan.getTestTypes(), null);
        addSpace(document);
        
        // 6. Pass/Fail Criteria
        addSection(document, "6. ITEM PASS/FAIL CRITERIA");
        addSubSection(document, "6.1 Pass Criteria");
        addList(document, testPlan.getPassCriteria(), null);
        
        addSubSection(document, "6.2 Fail Criteria");
        addList(document, testPlan.getFailCriteria(), null);
        addSpace(document);
        
        // 7. Test Deliverables
        addSection(document, "7. TEST DELIVERABLES");
        addList(document, testPlan.getTestDeliverables(), null);
        addSpace(document);
        
        // 8. Environmental Needs
        addSection(document, "8. ENVIRONMENTAL NEEDS");
        addParagraph(document, "Test Environment: " + 
                    (testPlan.getTestEnvironment() != null ? testPlan.getTestEnvironment() : "Standard test environment"));
        addSpace(document);
        
        // 9. Responsibilities
        addSection(document, "9. RESPONSIBILITIES");
        addParagraph(document, "Test Manager: " + 
                    (testPlan.getTestManager() != null ? testPlan.getTestManager() : "TBD"));
        addParagraph(document, "Created By: " + testPlan.getCreatedBy());
        addSpace(document);
        
        // 10. Schedule
        addSection(document, "10. SCHEDULE");
        addParagraph(document, "Start Date: " + 
                    (testPlan.getStartDate() != null ? testPlan.getStartDate() : "TBD"));
        addParagraph(document, "End Date: " + 
                    (testPlan.getEndDate() != null ? testPlan.getEndDate() : "TBD"));
        addSpace(document);
        
        // 11. Risks and Contingencies
        addSection(document, "11. RISKS AND CONTINGENCIES");
        addList(document, testPlan.getRisks(), "Risk assessment to be conducted during test planning phase.");
    }
    
    private void createTestCasesSection(Document document, TestPlan testPlan) {
        addSection(document, "12. TEST CASES");
        
        if (testPlan.getTestCases().isEmpty()) {
            addParagraph(document, "No test cases defined.");
            return;
        }
        
        // Test cases summary
        addParagraph(document, "Total Test Cases: " + testPlan.getTestCases().size());
        
        long functionalTests = testPlan.getTestCases().stream()
            .filter(tc -> "Functional".equals(tc.getTestType())).count();
        long integrationTests = testPlan.getTestCases().stream()
            .filter(tc -> "Integration".equals(tc.getTestType())).count();
        long negativeTests = testPlan.getTestCases().stream()
            .filter(tc -> "Negative".equals(tc.getTestType())).count();
        
        addParagraph(document, "• Functional Tests: " + functionalTests);
        addParagraph(document, "• Integration Tests: " + integrationTests);
        addParagraph(document, "• Negative Tests: " + negativeTests);
        addSpace(document);
        
        // Individual test cases
        for (TestCase testCase : testPlan.getTestCases()) {
            addTestCase(document, testCase);
        }
    }
    
    private void addTestCase(Document document, TestCase testCase) {
        // Test case header
        Table tcTable = new Table(2);
        tcTable.setWidth(UnitValue.createPercentValue(100));
        tcTable.setMarginBottom(10);
        
        tcTable.addCell(createCell("Test Case ID:", true));
        tcTable.addCell(createCell(testCase.getId(), false));
        
        tcTable.addCell(createCell("Title:", true));
        tcTable.addCell(createCell(testCase.getTitle(), false));
        
        tcTable.addCell(createCell("Priority:", true));
        tcTable.addCell(createCell(testCase.getPriority(), false));
        
        tcTable.addCell(createCell("Test Type:", true));
        tcTable.addCell(createCell(testCase.getTestType(), false));
        
        tcTable.addCell(createCell("Description:", true));
        tcTable.addCell(createCell(testCase.getDescription(), false));
        
        // Preconditions
        if (!testCase.getPreconditions().isEmpty()) {
            tcTable.addCell(createCell("Preconditions:", true));
            tcTable.addCell(createCell(String.join("; ", testCase.getPreconditions()), false));
        }
        
        // Test steps
        if (!testCase.getTestSteps().isEmpty()) {
            tcTable.addCell(createCell("Test Steps:", true));
            StringBuilder steps = new StringBuilder();
            for (TestStep step : testCase.getTestSteps()) {
                steps.append(step.getStepNumber()).append(". ")
                     .append(step.getAction()).append(" → ")
                     .append(step.getExpectedResult()).append("\n");
            }
            tcTable.addCell(createCell(steps.toString(), false));
        }
        
        tcTable.addCell(createCell("Expected Result:", true));
        tcTable.addCell(createCell(testCase.getExpectedResult(), false));
        
        document.add(tcTable);
        addSpace(document);
    }
    
    private void createRequirementsTraceability(Document document, TestPlan testPlan) {
        addSection(document, "13. REQUIREMENTS TRACEABILITY MATRIX");
        
        // Create traceability table
        Table traceTable = new Table(4);
        traceTable.setWidth(UnitValue.createPercentValue(100));
        
        // Headers
        traceTable.addHeaderCell(createHeaderCell("Requirement ID"));
        traceTable.addHeaderCell(createHeaderCell("Test Case ID"));
        traceTable.addHeaderCell(createHeaderCell("Test Case Title"));
        traceTable.addHeaderCell(createHeaderCell("Coverage Status"));
        
        // Data rows
        for (TestCase testCase : testPlan.getTestCases()) {
            if (!testCase.getRelatedRequirements().isEmpty()) {
                for (String reqId : testCase.getRelatedRequirements()) {
                    traceTable.addCell(createCell(reqId, false));
                    traceTable.addCell(createCell(testCase.getId(), false));
                    traceTable.addCell(createCell(testCase.getTitle(), false));
                    traceTable.addCell(createCell("Covered", false));
                }
            } else {
                traceTable.addCell(createCell("N/A", false));
                traceTable.addCell(createCell(testCase.getId(), false));
                traceTable.addCell(createCell(testCase.getTitle(), false));
                traceTable.addCell(createCell("Covered", false));
            }
        }
        
        document.add(traceTable);
    }
    
    // Helper methods
    private void addSection(Document document, String title) {
        Paragraph section = new Paragraph(title)
            .setFont(headerFont)
            .setFontSize(14)
            .setMarginTop(20)
            .setMarginBottom(10)
            .setFontColor(ColorConstants.DARK_GRAY);
        document.add(section);
    }
    
    private void addSubSection(Document document, String title) {
        Paragraph subSection = new Paragraph(title)
            .setFont(subHeaderFont)
            .setFontSize(12)
            .setMarginTop(10)
            .setMarginBottom(5);
        document.add(subSection);
    }
    
    private void addParagraph(Document document, String text) {
        if (text != null && !text.trim().isEmpty()) {
            Paragraph paragraph = new Paragraph(text)
                .setFont(normalFont)
                .setFontSize(10)
                .setMarginBottom(5);
            document.add(paragraph);
        }
    }
    
    private void addList(Document document, java.util.List<String> items, String defaultText) {
        if (items != null && !items.isEmpty()) {
            List list = new List();
            for (String item : items) {
                ListItem listItem = new ListItem(item);
                listItem.setFont(normalFont).setFontSize(10);
                list.add(listItem);
            }
            document.add(list);
        } else if (defaultText != null) {
            addParagraph(document, defaultText);
        }
    }
    
    private void addSpace(Document document) {
        document.add(new Paragraph(" ").setMarginBottom(10));
    }
    
    private void addInfoRow(Table table, String label, String value) {
        table.addCell(createCell(label, true));
        table.addCell(createCell(value != null ? value : "", false));
    }
    
    private Cell createCell(String content, boolean bold) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(content)
            .setFont(bold ? boldFont : normalFont)
            .setFontSize(10);
        cell.add(p);
        cell.setPadding(5);
        return cell;
    }
    
    private Cell createHeaderCell(String content) {
        Cell cell = createCell(content, true);
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        return cell;
    }
}