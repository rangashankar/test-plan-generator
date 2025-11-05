@echo off
REM Ultra-simple test plan generator for Windows
REM Usage: testplan.bat document.pdf [project-name]

if "%1"=="" (
    echo 🚀 Test Plan Generator
    echo Usage: testplan.bat ^<document^> [project-name]
    echo Example: testplan.bat requirements.pdf
    echo Example: testplan.bat design.txt "My Project"
    exit /b 1
)

set DOCUMENT=%1
if "%2"=="" (
    for %%f in ("%DOCUMENT%") do set PROJECT=%%~nf
) else (
    set PROJECT=%2
)

set OUTPUT=%PROJECT: =-%%-TestPlan.pdf

echo 📄 Document: %DOCUMENT%
echo 📝 Project: %PROJECT%
echo 📊 Output: %OUTPUT%
echo.

mvn exec:java -Dexec.mainClass="com.testplan.cli.TestPlanCLI" -Dexec.args="\"%PROJECT%\" \"1.0\" \"%DOCUMENT%\" \"\" \"%OUTPUT%\"" -q

if %errorlevel% equ 0 (
    echo.
    echo ✅ Done! Open %OUTPUT% to view your test plan
)