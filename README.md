# Test Plan Generator

A Java-based application that automatically generates comprehensive test plans and test cases from requirement and design documents.

## Features

- **Multi-Format Document Parsing**: 
  - Structured requirements/design documents (text/markdown)
  - **Narrative documents** (Press releases, product descriptions, feature announcements)
  - **📄 PDF documents** (Native PDF text extraction)
  - Automatic parser selection based on document content
- **Intelligent Test Case Generation**: Creates multiple types of test cases:
  - Functional test cases from requirements/features
  - Integration test cases from design components
  - Boundary test cases for limit conditions
  - Negative test cases for error scenarios
- **PRFAQ-Specific Extraction**:
  - Extracts key features from press release narrative
  - Converts FAQ answers into testable requirements
  - Identifies performance metrics and accuracy requirements
  - Generates test cases for system capabilities
- **📋 IEEE 829 Standard Test Plans**: Generates professional test plans with:
  - **Complete IEEE 829 compliance** (Test Plan Identifier, Introduction, Test Items, etc.)
  - **Standardized test case format** (ID, Objective, Priority, Severity, Steps, etc.)
  - **Requirements traceability matrix**
  - **Test execution tracking sheets**
  - **Metrics and reporting dashboards**
  - **Professional multi-sheet Excel format**
- **Multiple Output Formats**: 
  - Professional Excel format with multiple sheets
  - Simple text format for version control

## Project Structure

```
src/main/java/com/testplan/
├── model/              # Data models
│   ├── Requirement.java
│   ├── DesignComponent.java
│   ├── TestCase.java
│   ├── TestStep.java
│   ├── TestPlan.java
│   └── TestStrategy.java
├── parser/             # Document parsers
│   ├── DocumentParser.java
│   └── TextDocumentParser.java
├── generator/          # Test generation logic
│   ├── TestCaseGenerator.java
│   └── TestPlanGenerator.java
├── exporter/           # Export functionality
│   ├── TestPlanExporter.java
│   └── ExcelTestPlanExporter.java
└── TestPlanGeneratorApp.java  # Main application
```

## Usage

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### 🚀 Super Simple Usage

**One-Line Setup:**
```bash
mvn compile  # Just once
```

**Ultra-Simple Usage:**
```bash
# Just provide your document - get professional PDF! 🎉
./testplan requirements.pdf
./testplan design.txt "My Project"
# → Generates: My-Project-TestPlan.pdf
```

### Multiple Ways to Use

**1. 🎯 Ultra-Simple (Recommended)**
```bash
./testplan document.pdf                    # → Professional PDF test plan
./testplan requirements.txt "My Project"   # → My-Project-TestPlan.pdf
```

**2. 🤖 Interactive Mode (AI Support)**
```bash
./generate-testplan                        # Guided setup with AI options
```

**3. ⚙️ Advanced Mode**
```bash
./testplan "Project" "1.0" requirements.txt design.pdf output.pdf
./testplan "Project" "1.0" requirements.txt "" output.xlsx  # Excel format
./testplan "Project" "1.0" requirements.txt "" output.txt   # Text format
```

### What You Get Instantly
- **�  Professional PDF document** with IEEE 829 compliance
- **📋 Complete test plan structure** with all standard sections
- **🔗 Requirements traceability** matrix
- **📈 Test cases with detailed steps** and execution tracking
- **🎯 Ready-to-use** professional documentation

> **💡 Pro Tip**: Just run `./testplan your-document.pdf` and you're done! 
> 
> **📖 Need more options?** See [USAGE.md](USAGE.md) for complete guide

**Interactive Mode**: Run `./testplan` without arguments for guided setup with file validation and format selection.

### 🤖 AI-Powered Mode (AWS Bedrock)
To enable AI-powered document analysis and test case generation:

1. **Setup AWS Credentials**:
   ```bash
   # Option 1: Environment variables
   export AWS_ACCESS_KEY_ID=your_access_key
   export AWS_SECRET_ACCESS_KEY=your_secret_key
   export AWS_DEFAULT_REGION=us-east-1
   
   # Option 2: AWS CLI profile
   aws configure
   ```

2. **Enable AI Service Access** (Optional):
   - Configure cloud AI service credentials for enhanced parsing
   - Ensure access to AI language models if using AI features
   - Verify appropriate service permissions

3. **Run with AI**:
   ```bash
   ./testplan  # Interactive mode will detect AWS and offer AI option
   ```

**AI Benefits**:
- 📄 **Universal Document Support**: Works with any document format
- 🧠 **Intelligent Analysis**: Understands context and nuance  
- 🎯 **Comprehensive Coverage**: Identifies edge cases and scenarios
- ⚡ **Advanced Test Cases**: Generates sophisticated test scenarios

#### 🔁 Self-Healing AI Parsing
- Automatic retry if the first AI response is empty or malformed
- JSON validator cleans up occasional model formatting issues
- Seamless fallback to the traditional parser when AI cannot extract data
- Console logs (♻️ / ✅) explain when a retry or fallback occurs so you always know what happened

### � IEcEE 829 Standard Compliance

The tool generates professional test documentation following industry standards:

**Test Plan Structure (IEEE 829)**:
1. Test Plan Identifier
2. Introduction (Purpose, Background, Scope)
3. Test Items
4. Features Not to be Tested
5. Approach (Test Levels, Types, Strategy)
6. Item Pass/Fail Criteria
7. Test Deliverables
8. Environmental Needs
9. Responsibilities
10. Staffing and Training Needs
11. Schedule
12. Risks and Contingencies
13. Approvals

**Test Case Format**:
- **Standard Fields**: ID, Title, Objective, Priority, Severity, Test Type, Test Level
- **Execution Details**: Preconditions, Test Steps, Expected Results, Test Data
- **Management**: Author, Created Date, Status, Estimated Time
- **Traceability**: Related Requirements, Components, Defects

**Excel Output Includes**:
- Cover Page with document control
- Complete IEEE 829 test plan
- Standardized test cases sheet
- Requirements traceability matrix
- Test execution tracking
- Metrics and reporting dashboard

### 🔧 Architecture
The tool now includes both traditional rule-based parsing and AI-powered analysis:

- **Traditional Mode**: Fast, reliable parsing for structured documents
- **AI Mode**: Intelligent analysis using AWS Bedrock Claude 3 Sonnet
- **Hybrid Approach**: Combines both methods for maximum coverage
- **Fallback Strategy**: AI failures gracefully fall back to traditional parsing

## Supported Document Formats

### 1. Structured Requirements/Design Documents
```
REQUIREMENT: REQ-001
Title of the Requirement
Priority: High|Medium|Low
Category: Functional|Security|Performance

Description of the requirement...

Acceptance Criteria:
- Criterion 1
- Criterion 2
- Criterion 3
```

```
DESIGN: COMP-001
Component Name
Type: UI|API|Database|Service
Description: Component description...

Interfaces:
- Interface 1
- Interface 2

Dependencies:
- Dependency 1
- Dependency 2

Business Rules:
- Rule 1
- Rule 2
```

### 2. Narrative Documents
The tool automatically detects and parses narrative documents (press releases, product descriptions, announcements):

```
PRESS RELEASE

Company Announces New Feature

[Narrative description of the feature and its capabilities]

Key features include:
• Feature 1: Description of capability
• Feature 2: Description of capability
• Feature 3: Description of capability

FREQUENTLY ASKED QUESTIONS

Q: How does the feature work?
A: The feature uses [technology] to provide [capability]...

Q: What are the accuracy requirements?
A: The system achieves 95% accuracy in [specific area]...
```

**PRFAQ Parsing Capabilities:**
- Extracts key features from bullet points
- Converts FAQ answers into testable requirements  
- Identifies performance metrics (accuracy percentages)
- Generates test cases for system integrations
- Handles narrative-style requirement descriptions

## Sample Files

The project includes sample files to demonstrate usage:
- `sample-requirements.txt`: Example structured requirements document
- `sample-design.txt`: Example structured design document  
- `sample-narrative.txt`: Example narrative document

## Generated Test Plan

The generated Excel file contains three sheets:
1. **Test Plan Overview**: Project information, objectives, scope
2. **Test Cases**: Detailed test cases with steps and traceability
3. **Test Strategy**: Testing approach, tools, and environments

## Dependencies

- Java 11+
- Apache Maven
- Apache POI (Excel generation)
- Jackson (JSON processing)
- Apache Commons Lang

## Extending the Generator

### Adding New Document Parsers
Implement the `DocumentParser` interface to support additional document formats (Word, PDF, etc.).

### Adding New Export Formats
Implement the `TestPlanExporter` interface to support additional output formats (JSON, HTML, etc.).

### Customizing Test Case Generation
Modify the `TestCaseGenerator` class to add new test case types or generation logic.

## Example Output

### Structured Requirements (sample files)
```bash
./testplan "Sample Project" "1.0" sample-requirements.txt sample-design.txt
```
Generates **11 test cases**:
- 3 Functional, 3 Integration, 2 Boundary, 3 Negative test cases

### Narrative Documents
```bash
./testplan "Smart Assistant" "1.0" sample-narrative.txt
```
Generates **26 test cases** from narrative content:
- 11 Functional (from key features)
- 4 Integration (from system integrations) 
- 11 Negative (error scenarios)

### Output Features
- **Complete traceability** linking test cases to requirements and components
- **Professional Excel format** with multiple sheets:
  - Test Plan Overview (objectives, scope, strategy)
  - Test Cases (detailed test steps and expected results)
  - Test Strategy (approach, tools, environments)
- **Text format option** for version control and simple viewing
- **Automatic parser detection** (structured vs PRFAQ format)

This tool transforms hours of manual test planning into minutes of automated generation, ensuring comprehensive coverage and professional formatting.
