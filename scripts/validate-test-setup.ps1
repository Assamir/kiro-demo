# Test Setup Validation Script
# Validates that all required test files and configurations are in place

Write-Host "==========================================" -ForegroundColor Blue
Write-Host "Insurance Backoffice System - Test Setup Validation" -ForegroundColor Blue
Write-Host "==========================================" -ForegroundColor Blue

$ErrorActionPreference = "Continue"
$validationErrors = @()

function Test-FileExists {
    param([string]$FilePath, [string]$Description)
    
    if (Test-Path $FilePath) {
        Write-Host "✅ $Description" -ForegroundColor Green
        return $true
    } else {
        Write-Host "❌ $Description - File not found: $FilePath" -ForegroundColor Red
        $script:validationErrors += "Missing file: $FilePath"
        return $false
    }
}

function Test-DirectoryExists {
    param([string]$DirectoryPath, [string]$Description)
    
    if (Test-Path $DirectoryPath -PathType Container) {
        Write-Host "✅ $Description" -ForegroundColor Green
        return $true
    } else {
        Write-Host "❌ $Description - Directory not found: $DirectoryPath" -ForegroundColor Red
        $script:validationErrors += "Missing directory: $DirectoryPath"
        return $false
    }
}

Write-Host "`n1. Backend Integration Test Files" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/BaseIntegrationTest.java" "Base Integration Test"
Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/EndToEndWorkflowTest.java" "End-to-End Workflow Test"
Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/SystemPerformanceTest.java" "System Performance Test"
Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/ComprehensiveValidationTest.java" "Comprehensive Validation Test"
Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/AuthenticationIntegrationTest.java" "Authentication Integration Test"
Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/UserManagementIntegrationTest.java" "User Management Integration Test"
Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/PolicyManagementIntegrationTest.java" "Policy Management Integration Test"
Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/PdfGenerationIntegrationTest.java" "PDF Generation Integration Test"
Test-FileExists "backend/src/test/java/com/insurance/backoffice/integration/TestDataFixtures.java" "Test Data Fixtures"

Write-Host "`n2. Frontend Integration Test Files" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "frontend/src/integration/EndToEndWorkflow.test.tsx" "Frontend End-to-End Workflow Test"
Test-FileExists "frontend/src/integration/SystemValidation.test.tsx" "Frontend System Validation Test"
Test-FileExists "frontend/src/integration/UserManagementFlow.test.tsx" "Frontend User Management Flow Test"
Test-FileExists "frontend/src/integration/PolicyManagementFlow.test.tsx" "Frontend Policy Management Flow Test"
Test-FileExists "frontend/src/integration/AuthenticationFlow.test.tsx" "Frontend Authentication Flow Test"

Write-Host "`n3. Test Execution Scripts" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "scripts/run-final-integration-tests.ps1" "PowerShell Test Execution Script"
Test-FileExists "scripts/run-final-integration-tests.sh" "Bash Test Execution Script"
Test-FileExists "scripts/validate-test-setup.ps1" "Test Setup Validation Script"

Write-Host "`n4. Configuration Files" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "docker-compose.yml" "Docker Compose Configuration"
Test-FileExists "backend/build.gradle" "Backend Build Configuration"
Test-FileExists "frontend/package.json" "Frontend Package Configuration"
Test-FileExists "backend/src/test/resources/application-test.properties" "Test Application Properties"

Write-Host "`n5. Documentation Files" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "FINAL_INTEGRATION_TESTING.md" "Final Integration Testing Documentation"
Test-FileExists "INTEGRATION_TESTING.md" "Integration Testing Documentation"
Test-FileExists "README.md" "Project README"

Write-Host "`n6. Test Data and Fixtures" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-DirectoryExists "backend/src/main/resources/db/migration" "Database Migration Directory"
Test-FileExists "backend/src/main/resources/db/migration/V10__Insert_sample_clients.sql" "Sample Clients Data"
Test-FileExists "backend/src/main/resources/db/migration/V11__Insert_sample_vehicles.sql" "Sample Vehicles Data"
Test-FileExists "backend/src/main/resources/db/migration/V12__Insert_sample_policies.sql" "Sample Policies Data"

Write-Host "`n7. Core Application Files" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "backend/src/main/java/com/insurance/backoffice/InsuranceBackofficeApplication.java" "Backend Main Application"
Test-FileExists "frontend/src/App.tsx" "Frontend Main Application"
Test-FileExists "backend/src/main/java/com/insurance/backoffice/config/SecurityConfig.java" "Security Configuration"
Test-FileExists "backend/src/main/java/com/insurance/backoffice/interfaces/controller/AuthController.java" "Authentication Controller"

Write-Host "`n8. Service and Repository Files" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "backend/src/main/java/com/insurance/backoffice/application/service/PolicyService.java" "Policy Service"
Test-FileExists "backend/src/main/java/com/insurance/backoffice/application/service/UserService.java" "User Service"
Test-FileExists "backend/src/main/java/com/insurance/backoffice/application/service/PdfService.java" "PDF Service"
Test-FileExists "backend/src/main/java/com/insurance/backoffice/infrastructure/repository/PolicyRepository.java" "Policy Repository"

Write-Host "`n9. Docker Configuration Validation" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "backend/Dockerfile" "Backend Dockerfile"
Test-FileExists "frontend/Dockerfile" "Frontend Dockerfile"
Test-FileExists "backend/Dockerfile.prod" "Backend Production Dockerfile"
Test-FileExists "frontend/Dockerfile.prod" "Frontend Production Dockerfile"

Write-Host "`n10. Environment and Deployment Files" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-FileExists "docker-compose.prod.yml" "Production Docker Compose"
Test-FileExists "docker-compose.staging.yml" "Staging Docker Compose"
Test-FileExists ".env.prod.example" "Production Environment Example"
Test-FileExists ".env.staging.example" "Staging Environment Example"

# Check for required directories
Write-Host "`n11. Directory Structure Validation" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

Test-DirectoryExists "backend/src/main/java/com/insurance/backoffice" "Backend Source Directory"
Test-DirectoryExists "backend/src/test/java/com/insurance/backoffice" "Backend Test Directory"
Test-DirectoryExists "frontend/src" "Frontend Source Directory"
Test-DirectoryExists "frontend/src/components" "Frontend Components Directory"
Test-DirectoryExists "frontend/src/services" "Frontend Services Directory"

# Validate test file content (basic check)
Write-Host "`n12. Test File Content Validation" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Yellow

$endToEndTestContent = Get-Content "backend/src/test/java/com/insurance/backoffice/integration/EndToEndWorkflowTest.java" -Raw -ErrorAction SilentlyContinue
if ($endToEndTestContent -and $endToEndTestContent.Contains("@Test") -and $endToEndTestContent.Contains("completeAdminWorkflow")) {
    Write-Host "✅ End-to-End Workflow Test contains required test methods" -ForegroundColor Green
} else {
    Write-Host "❌ End-to-End Workflow Test missing required content" -ForegroundColor Red
    $validationErrors += "End-to-End Workflow Test missing required test methods"
}

$performanceTestContent = Get-Content "backend/src/test/java/com/insurance/backoffice/integration/SystemPerformanceTest.java" -Raw -ErrorAction SilentlyContinue
if ($performanceTestContent -and $performanceTestContent.Contains("@Test") -and $performanceTestContent.Contains("performanceTest")) {
    Write-Host "✅ System Performance Test contains required test methods" -ForegroundColor Green
} else {
    Write-Host "❌ System Performance Test missing required content" -ForegroundColor Red
    $validationErrors += "System Performance Test missing required test methods"
}

$frontendTestContent = Get-Content "frontend/src/integration/SystemValidation.test.tsx" -Raw -ErrorAction SilentlyContinue
if ($frontendTestContent -and $frontendTestContent.Contains("test(") -and $frontendTestContent.Contains("role-based access control")) {
    Write-Host "✅ Frontend System Validation Test contains required test methods" -ForegroundColor Green
} else {
    Write-Host "❌ Frontend System Validation Test missing required content" -ForegroundColor Red
    $validationErrors += "Frontend System Validation Test missing required test methods"
}

# Final Summary
Write-Host "`n==========================================" -ForegroundColor Blue
Write-Host "VALIDATION SUMMARY" -ForegroundColor Blue
Write-Host "==========================================" -ForegroundColor Blue

if ($validationErrors.Count -eq 0) {
    Write-Host "🎉 ALL VALIDATION CHECKS PASSED!" -ForegroundColor Green
    Write-Host "✅ Test setup is complete and ready for execution" -ForegroundColor Green
    Write-Host "✅ All required files are present" -ForegroundColor Green
    Write-Host "✅ Test structure is properly configured" -ForegroundColor Green
    Write-Host "`nYou can now run the final integration tests using:" -ForegroundColor Cyan
    Write-Host "  .\scripts\run-final-integration-tests.ps1" -ForegroundColor White
} else {
    Write-Host "❌ VALIDATION FAILED - Issues found:" -ForegroundColor Red
    foreach ($error in $validationErrors) {
        Write-Host "  • $error" -ForegroundColor Red
    }
    Write-Host "`nPlease resolve the above issues before running integration tests." -ForegroundColor Yellow
    exit 1
}

Write-Host "==========================================" -ForegroundColor Blue