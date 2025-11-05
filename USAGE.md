# 🚀 Test Plan Generator - Usage Guide

## Super Simple Usage (Recommended)

### 1. One-Time Setup
```bash
git clone <repository-url>
cd TestPlanGenerator
mvn compile
```

### 2. Generate Test Plans

**Ultra-Simple (Just drag and drop your document!):**
```bash
./testplan requirements.pdf //new change
./testplan design.txt "My Project"
```

That's it! You'll get a professional IEEE 829 compliant test plan in Excel format.

## All Usage Options

### 🎯 Option 1: Ultra-Simple Command Line
```bash
# Auto-generate project name from filename
./testplan requirements.pdf
./testplan user-stories.txt
./testplan technical-spec.md

# Custom project name
./testplan document.pdf "My Awesome Project"
./testplan requirements.txt "E-commerce Platform"
```

### 🖱️ Option 2: GUI Mode (Drag & Drop)
```bash
java -cp target/classes com.testplan.SimpleTestPlanApp
```
- Drag your document into the window
- Project name auto-fills
- Click "Generate Test Plan"
- Done!

### 🤖 Option 3: Interactive Mode (AI Support)
```bash
./generate-testplan
```
- Guided prompts
- AI document analysis (if AWS configured)
- File validation
- Format selection

### ⚙️ Option 4: Advanced Command Line
```bash
./testplan "Project Name" "Version" "Input File" "Design File" "Output File"

# Examples:
./testplan "E-commerce" "2.0" requirements.pdf design.txt ecommerce-testplan.xlsx
./testplan "Mobile App" "1.5" prfaq.md "" mobile-testplan.xlsx
```

## Supported Document Types

| Format | Extension | Description |
|--------|-----------|-------------|
| **PDF** | `.pdf` | Requirements, design docs, specifications |
| **Text** | `.txt`, `.md` | Structured requirements and design |
| **Narrative** | Any | Press releases, product descriptions, announcements |
| **AI Mode** | Any | Universal support via cloud AI services |

## What You Get

### 📄 Professional PDF Output (IEEE 829 Compliant)
1. **Cover Page** - Document control and approvals
2. **Table of Contents** - Complete navigation
3. **Test Plan Sections** - All 13 IEEE 829 sections
4. **Test Cases** - Detailed test cases with steps
5. **Requirements Traceability Matrix** - Compliance tracking

### 📊 Alternative Excel Output (7 Sheets)
Available with `.xlsx` extension - includes spreadsheet format with multiple sheets for data manipulation

### 🎯 Test Case Features
- **IEEE 829 Compliant** - Industry standard format
- **Complete Traceability** - Requirements to test cases
- **Execution Ready** - Detailed steps and expected results
- **Management Fields** - Priority, severity, status, timing
- **Quality Assurance** - Preconditions, test data, environment

## Platform Support

### 🐧 Linux/Mac
```bash
./testplan document.pdf
./generate-testplan
```

### 🪟 Windows
```cmd
testplan.bat document.pdf
testplan.bat requirements.txt "My Project"
```

### ☕ Java (Cross-platform)
```bash
java -cp target/classes com.testplan.cli.TestPlanCLI "Project" "1.0" "document.pdf"
java -cp target/classes com.testplan.SimpleTestPlanApp  # GUI mode
```

## AI-Powered Features (Optional)

### Setup AWS Bedrock
```bash
# Option 1: Environment variables
export AWS_ACCESS_KEY_ID=your_key
export AWS_SECRET_ACCESS_KEY=your_secret
export AWS_DEFAULT_REGION=us-east-1

# Option 2: AWS CLI
aws configure
```

### AI Benefits
- **📄 Universal Document Support** - Any format, any structure
- **🧠 Intelligent Analysis** - Context-aware requirement extraction
- **🎯 Advanced Test Cases** - Edge cases and comprehensive scenarios
- **🔍 Smart Parsing** - Handles unstructured documents

## Troubleshooting

### Common Issues

**"Java not found"**
```bash
# Install Java 11+
sudo apt install openjdk-11-jdk  # Ubuntu/Debian
brew install openjdk@11          # macOS
```

**"Maven not found"**
```bash
# Install Maven
sudo apt install maven           # Ubuntu/Debian  
brew install maven               # macOS
```

**"Permission denied"**
```bash
chmod +x testplan generate-testplan
```

**"File not found"**
- Check document path is correct
- Ensure file exists and is readable
- Try absolute path: `/full/path/to/document.pdf`

### Getting Help

**Check if everything is working:**
```bash
./testplan                       # Should show usage
java -version                    # Should show Java 11+
mvn -version                     # Should show Maven 3.6+
```

**Test with sample file:**
```bash
./testplan sample-narrative.txt "Sample Project"
```

## Examples

### Real-World Usage

**Software Project:**
```bash
./testplan software-requirements.pdf "Banking System"
# → Banking-System-TestPlan.pdf
```

**Mobile App:**
```bash
./testplan mobile-app-spec.md "Food Delivery App"  
# → Food-Delivery-App-TestPlan.pdf
```

**API Documentation:**
```bash
./testplan api-documentation.pdf "Payment Gateway"
# → Payment-Gateway-TestPlan.pdf
```

**PRFAQ Document:**
```bash
./testplan new-feature-prfaq.txt "Smart Recommendations"
# → Smart-Recommendations-TestPlan.pdf
```

## Tips for Best Results

### 📝 Document Preparation
- **Structure your documents** with clear sections
- **Use consistent formatting** for requirements
- **Include acceptance criteria** when possible
- **Specify priorities** (High/Medium/Low)

### 🎯 Project Naming
- Use descriptive names: "E-commerce Platform" vs "Project1"
- Avoid special characters: use spaces or hyphens
- Keep it concise but meaningful

### 📊 Output Optimization
- **PDF format** (default) for professional documents and sharing
- **Excel format** for data manipulation and spreadsheet analysis  
- **Text format** for version control and automation
- **Custom filenames** for organization

---

**Need more help?** Check the main README.md or create an issue in the repository.

**Ready to generate professional test plans?** Just run:
```bash
./testplan your-document.pdf
```

🎉 **That's it! Professional IEEE 829 test plans in seconds!**
