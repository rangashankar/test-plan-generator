package com.testplan.cli;

import com.testplan.model.*;
import com.testplan.parser.*;
import com.testplan.generator.*;
import com.testplan.exporter.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

/**
 * Command Line Interface for the Test Plan Generator
 * Provides an interactive experience for generating test plans
 */
public class TestPlanCLI {
    
    private Scanner scanner;
    private TestPlanGenerator testPlanGenerator;
    private boolean useAI = false;
    
    public TestPlanCLI() {
        this.scanner = new Scanner(System.in);
        this.testPlanGenerator = new TestPlanGenerator();
    }
    
    public static void main(String[] args) {
        TestPlanCLI cli = new TestPlanCLI();
        
        if (args.length > 0) {
            // Non-interactive mode with command line arguments
            cli.runNonInteractive(args);
        } else {
            // Interactive mode
            cli.runInteractive();
        }
    }
    
    public void runInteractive() {
        printWelcome();
        
        try {
            // Check for AI capability
            boolean aiAvailable = isAIAvailable();
            if (aiAvailable) {
                this.useAI = promptForAI();
            }
            
            // Get project information
            String projectName = promptForInput("Enter project name", "My Project");
            String version = promptForInput("Enter version", "1.0");
            
            // Get input file
            String inputFile = promptForFile("Enter path to requirements/narrative document", "sample-narrative.txt");
            
            // Get design file (optional)
            String designFile = promptForOptionalFile("Enter path to design document (optional, press Enter to skip)");
            
            // Get output format
            String outputFormat = promptForOutputFormat();
            String outputFile = promptForOutput("Enter output file name", 
                                              generateDefaultOutputName(projectName, outputFormat));
            
            // Generate test plan
            generateTestPlan(projectName, version, inputFile, designFile, outputFile, false);
            
        } catch (Exception e) {
            System.err.println("\n❌ Error: " + e.getMessage());
            System.exit(1);
        } finally {
            scanner.close();
        }
    }
    
    public void runNonInteractive(String[] args) {
        boolean validateOnly = false;
        List<String> filtered = new ArrayList<>();
        for (String arg : args) {
            if ("--validate-only".equalsIgnoreCase(arg) || "--validate".equalsIgnoreCase(arg) || "-V".equalsIgnoreCase(arg)) {
                validateOnly = true;
            } else {
                filtered.add(arg);
            }
        }

        if (filtered.size() < 3) {
            printUsage();
            System.exit(1);
        }
        
        String projectName = filtered.get(0);
        String version = filtered.get(1);
        String inputFile = filtered.get(2);
        String designFile = filtered.size() > 3 && !filtered.get(3).trim().isEmpty() ? filtered.get(3) : "";
        String outputFile = filtered.size() > 4 ? filtered.get(4) : generateDefaultOutputName(projectName, "pdf");
        
        System.out.println("🚀 Test Plan Generator");
        System.out.println("   Project: " + projectName);
        System.out.println("   Version: " + version);
        System.out.println("   Input: " + inputFile);
        if (!designFile.isEmpty()) {
            System.out.println("   Design: " + designFile);
        }
        if (!validateOnly) {
            System.out.println("   Output: " + outputFile);
        } else {
            System.out.println("   Mode: validation only (no export)");
        }
        System.out.println();
        
        try {
            generateTestPlan(projectName, version, inputFile, designFile, outputFile, validateOnly);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void printWelcome() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    Test Plan Generator                       ║");
        System.out.println("║              Transform Documents into Test Plans             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("🚀 Supports:");
        System.out.println("   • Structured Requirements Documents");
        System.out.println("   • Narrative Documents (Press Releases, Product Descriptions)");
        System.out.println("   • 📄 PDF Documents (Native parsing)");
        System.out.println("   • 🤖 AI-Powered Document Analysis");
        System.out.println("   • PDF, Excel and Text Output Formats");
        System.out.println();
    }
    
    private void printUsage() {
        System.out.println("Usage: java TestPlanCLI <project-name> <version> <input-file> [design-file] [output-file]");
        System.out.println("   Add --validate-only to run parsing/generation health checks without exporting.");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java TestPlanCLI \"My Project\" \"1.0\" requirements.txt");
        System.out.println("  java TestPlanCLI \"Smart Cart\" \"2.0\" prfaq.txt \"\" output.xlsx");
        System.out.println();
        System.out.println("Or run without arguments for interactive mode.");
    }
    
    private String promptForInput(String prompt, String defaultValue) {
        System.out.print("📝 " + prompt);
        if (defaultValue != null && !defaultValue.isEmpty()) {
            System.out.print(" [" + defaultValue + "]");
        }
        System.out.print(": ");
        
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
    
    private String promptForFile(String prompt, String defaultValue) {
        while (true) {
            String filePath = promptForInput(prompt, defaultValue);
            File file = new File(filePath);
            
            if (file.exists() && file.isFile()) {
                // Detect document type
                DocumentParser parser = DocumentParserFactory.createParser(file);
                String parserType = parser.getClass().getSimpleName();
                System.out.println("   ✅ Found " + parserType.replace("DocumentParser", "") + " document: " + filePath);
                return filePath;
            } else {
                System.out.println("   ❌ File not found: " + filePath);
                if (defaultValue != null && !filePath.equals(defaultValue)) {
                    System.out.println("   💡 Try the default sample file or check the path");
                }
            }
        }
    }
    
    private String promptForOptionalFile(String prompt) {
        System.out.print("📄 " + prompt + ": ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            return "";
        }
        
        File file = new File(input);
        if (file.exists() && file.isFile()) {
            System.out.println("   ✅ Found design document: " + input);
            return input;
        } else {
            System.out.println("   ⚠️  Design file not found, continuing without it");
            return "";
        }
    }
    
    private String promptForOutputFormat() {
        System.out.println("📊 Select output format:");
        System.out.println("   1. PDF (.pdf) - Professional IEEE 829 compliant document");
        System.out.println("   2. Excel (.xlsx) - Spreadsheet format with multiple sheets");
        System.out.println("   3. Text (.txt) - Simple format for version control");
        System.out.print("   Enter choice [1]: ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "2": return "xlsx";
            case "3": return "txt";
            default: return "pdf";
        }
    }
    
    private String promptForOutput(String prompt, String defaultValue) {
        return promptForInput(prompt, defaultValue);
    }
    
    private String generateDefaultOutputName(String projectName, String format) {
        String safeName = projectName.toLowerCase()
                                    .replaceAll("[^a-z0-9\\s]", "")
                                    .replaceAll("\\s+", "-");
        return safeName + "-testplan." + format;
    }
    
    private void generateTestPlan(String projectName, String version, String inputFile, 
                                String designFile, String outputFile, boolean validateOnly) throws Exception {
        
        System.out.println("\n🔄 Generating test plan...");
        
        // Parse input document
        File inputFileObj = new File(inputFile);
        DocumentParser parser = DocumentParserFactory.createParser(inputFileObj, useAI);
        
        System.out.println("   🔍 Using parser: " + parser.getClass().getSimpleName());
        
        List<Requirement> requirements = parser.parseRequirements(inputFileObj);
        System.out.println("   📋 Extracted " + requirements.size() + " requirements");
        
        // Parse design document if provided
        List<DesignComponent> components = new java.util.ArrayList<>();
        if (designFile != null && !designFile.trim().isEmpty()) {
            File designFileObj = new File(designFile);
            if (designFileObj.exists()) {
                DocumentParser designParser = DocumentParserFactory.createParser(designFileObj, useAI);
                components = designParser.parseDesignComponents(designFileObj);
                System.out.println("   🏗️  Extracted " + components.size() + " design components");
            }
        } else {
            // Try to extract design components from input file
            components = parser.parseDesignComponents(inputFileObj);
            if (!components.isEmpty()) {
                System.out.println("   🏗️  Extracted " + components.size() + " design components from input file");
                // Debug: show component details
                for (DesignComponent comp : components) {
                    System.out.println("      • " + comp.getName() + " (Type: " + comp.getType() + ", Dependencies: " + comp.getDependencies().size() + ")");
                }
            } else {
                System.out.println("   ⚠️  No design components found in input file");
            }
        }
        
        if (requirements.isEmpty() && !components.isEmpty()) {
            System.out.println("   ℹ️  No explicit requirements detected. Deriving baseline requirements from design components...");
            requirements = testPlanGenerator.deriveRequirementsFromComponents(components);
            System.out.println("   📋 Derived " + requirements.size() + " requirements from design artifacts");
        }
        
        // Generate test plan

        TestPlan testPlan;
        if (useAI) {
            AITestPlanGenerator aiGenerator = new AITestPlanGenerator(true);
            testPlan = aiGenerator.generateTestPlan(projectName, version, requirements, components);
        } else {
            testPlan = testPlanGenerator.generateTestPlan(projectName, version, requirements, components);
        }
        System.out.println("   🧪 Generated " + testPlan.getTestCases().size() + " test cases");
        
        if (validateOnly) {
            printValidationSummary(testPlan);
            return;
        }

        String finalOutput = safeExport(testPlan, outputFile);
        writeManifest(testPlan, finalOutput);
        
        // Print success summary
        printSuccessSummary(testPlan, finalOutput);
    }

    private String safeExport(TestPlan testPlan, String outputFile) throws Exception {
        String normalizedOutput = outputFile.toLowerCase();
        TestPlanExporter primary;
        TestPlanExporter fallback;
        TestPlanExporter lastResort = new SimpleTextExporter();

        if (normalizedOutput.endsWith(".xlsx")) {
            primary = new StandardExcelExporter();
            fallback = new PDFTestPlanExporter();
        } else if (normalizedOutput.endsWith(".txt")) {
            primary = new SimpleTextExporter();
            fallback = new StandardExcelExporter();
        } else {
            primary = new PDFTestPlanExporter();
            fallback = new StandardExcelExporter();
        }

        File output = new File(outputFile);
        ensureWritable(output);

        try {
            primary.export(testPlan, output);
            return output.getPath();
        } catch (Exception primaryError) {
            cleanupPartial(output);
            System.err.println("   ⚠️  Primary export failed (" + primary.getClass().getSimpleName() + "): " + primaryError.getMessage());
            try {
                File fallbackFile = tweakExtension(outputFile, fallback.getFileExtension());
                fallback.export(testPlan, fallbackFile);
                System.out.println("   ♻️  Fallback export succeeded: " + fallbackFile.getName());
                return fallbackFile.getPath();
            } catch (Exception fallbackError) {
                cleanupPartial(output);
                File lastFile = tweakExtension(outputFile, lastResort.getFileExtension());
                lastResort.export(testPlan, lastFile);
                System.out.println("   ♻️  Last-resort text export succeeded: " + lastFile.getName());
                return lastFile.getPath();
            }
        }
    }

    private void ensureWritable(File output) throws IOException {
        File parent = output.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create output directory: " + parent);
        }
        if (parent != null && !parent.canWrite()) {
            throw new IOException("Output directory not writable: " + parent);
        }
    }

    private void cleanupPartial(File file) {
        if (file != null && file.exists()) {
            if (!file.delete()) {
                System.err.println("   ⚠️  Could not remove partial file: " + file.getName());
            }
        }
    }

    private File tweakExtension(String original, String newExt) {
        int dot = original.lastIndexOf('.');
        String base = dot > 0 ? original.substring(0, dot) : original;
        return new File(base + "." + newExt);
    }
    
    private void printSuccessSummary(TestPlan testPlan, String outputFile) {
        System.out.println("\n✅ Test Plan Generated Successfully!");
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        SUMMARY                               ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Project: %-51s ║%n", testPlan.getTitle());
        System.out.printf("║ Version: %-51s ║%n", testPlan.getVersion());
        System.out.printf("║ Test Cases: %-47d ║%n", testPlan.getTestCases().size());
        
        // Count test cases by type
        long functionalTests = testPlan.getTestCases().stream()
            .filter(tc -> "Functional".equals(tc.getTestType())).count();
        long integrationTests = testPlan.getTestCases().stream()
            .filter(tc -> "Integration".equals(tc.getTestType())).count();
        long boundaryTests = testPlan.getTestCases().stream()
            .filter(tc -> "Boundary".equals(tc.getTestType())).count();
        long negativeTests = testPlan.getTestCases().stream()
            .filter(tc -> "Negative".equals(tc.getTestType())).count();
        
        System.out.printf("║   • Functional: %-43d ║%n", functionalTests);
        System.out.printf("║   • Integration: %-42d ║%n", integrationTests);
        System.out.printf("║   • Boundary: %-45d ║%n", boundaryTests);
        System.out.printf("║   • Negative: %-45d ║%n", negativeTests);
        System.out.printf("║ Output File: %-46s ║%n", outputFile);
        
        File outputFileObj = new File(outputFile);
        if (outputFileObj.exists()) {
            long fileSize = outputFileObj.length();
            String sizeStr = fileSize > 1024 ? (fileSize / 1024) + " KB" : fileSize + " bytes";
            System.out.printf("║ File Size: %-48s ║%n", sizeStr);
        }
        
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("\n🎉 Ready to use! Open " + outputFile + " to view your test plan.");
    }

    private void printValidationSummary(TestPlan testPlan) {
        System.out.println("\n✅ Validation complete (no export requested)");
        System.out.println("   Requirements: " + testPlan.getTestItems().size());
        System.out.println("   Test Cases: " + testPlan.getTestCases().size());
        long aiGenerated = testPlan.getTestCases().stream().filter(tc -> tc.getId() != null && tc.getId().contains("TC_")).count();
        System.out.println("   Cases generated: " + aiGenerated);
    }
    
    private boolean isAIAvailable() {
        try {
            String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
            String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
            String profile = System.getenv("AWS_PROFILE");
            return (accessKey != null && secretKey != null) || profile != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean promptForAI() {
        System.out.println("🤖 AI-Powered Analysis Available!");
        System.out.println("   Cloud AI services can provide intelligent document analysis and test case generation.");
        System.out.println("   Benefits:");
        System.out.println("   • Handles any document format");
        System.out.println("   • Intelligent requirement extraction");
        System.out.println("   • Comprehensive test case generation");
        System.out.println("   • Edge case identification");
        System.out.print("   Use AI analysis? [y/N]: ");
        
        String choice = scanner.nextLine().trim().toLowerCase();
        boolean useAI = choice.equals("y") || choice.equals("yes");
        
        if (useAI) {
            System.out.println("   ✅ AI analysis enabled - Using cloud AI services");
        } else {
            System.out.println("   📝 Using traditional parsing methods");
        }
        
        return useAI;
    }

    private void writeManifest(TestPlan testPlan, String outputPath) {
        try {
            Path output = Path.of(outputPath);
            if (!Files.exists(output)) {
                return;
            }
            String checksum = computeSha256(output);
            long size = Files.size(output);
            String manifestName = outputPath + ".manifest.txt";
            StringBuilder sb = new StringBuilder();
            sb.append("file: ").append(output.getFileName()).append('\n');
            sb.append("size_bytes: ").append(size).append('\n');
            sb.append("sha256: ").append(checksum).append('\n');
            sb.append("test_cases: ").append(testPlan.getTestCases().size()).append('\n');
            Files.writeString(Path.of(manifestName), sb.toString(), StandardCharsets.UTF_8);
            System.out.println("   🧾 Manifest created: " + manifestName);
        } catch (Exception e) {
            System.err.println("   ⚠️  Could not write manifest: " + e.getMessage());
        }
    }

    private String computeSha256(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] data = Files.readAllBytes(file);
        byte[] hash = digest.digest(data);
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
}
