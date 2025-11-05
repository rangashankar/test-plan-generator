# Installation Guide

## Prerequisites Installation

### 1. Install Homebrew (if not already installed)
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### 2. Install Java 11
```bash
brew install openjdk@11
echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zprofile
export PATH="/usr/local/opt/openjdk@11/bin:$PATH"
```

### 3. Install Maven
```bash
brew install maven
```

### 4. Verify Installation
```bash
java -version    # Should show OpenJDK 11
mvn -version     # Should show Maven 3.x
```

## Project Setup

### 1. Clone or Download the Project
```bash
# If you have the project files, navigate to the directory
cd TestPlanGenerator
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Test the Installation
```bash
./run-testplan-generator.sh "Test Project" "1.0" sample-requirements.txt sample-design.txt test-output.xlsx
```

If successful, you should see:
- ✅ Test plan generated successfully!
- 📄 Output file: test-output.xlsx
- 📊 File size: ~8KB

## Troubleshooting

### Java Issues
- **Error: "java: command not found"**
  - Make sure Java is in your PATH: `export PATH="/usr/local/opt/openjdk@11/bin:$PATH"`
  - Restart your terminal

### Maven Issues  
- **Error: "mvn: command not found"**
  - Install Maven: `brew install maven`
  - Restart your terminal

### Compilation Issues
- **Error: "package does not exist"**
  - Run: `mvn clean compile` to download dependencies
  - Check internet connection for Maven repository access

### Permission Issues
- **Error: "Permission denied"**
  - Make script executable: `chmod +x run-testplan-generator.sh`

## Next Steps

Once installed successfully:
1. Create your own requirements and design documents
2. Run the generator with your files
3. Customize the templates in the source code if needed
4. Integrate into your CI/CD pipeline for automated test plan generation