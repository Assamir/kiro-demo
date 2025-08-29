# Production Deployment Script for Insurance Backoffice System (PowerShell)
# This script deploys the application to production environment

param(
    [switch]$SkipBackup = $false
)

$ErrorActionPreference = "Stop"

Write-Host "🚀 Starting production deployment..." -ForegroundColor Blue

# Check if required environment file exists
if (-not (Test-Path ".env.prod")) {
    Write-Host "❌ Error: .env.prod file not found!" -ForegroundColor Red
    Write-Host "Please copy .env.prod.example to .env.prod and configure it." -ForegroundColor Yellow
    exit 1
}

# Load environment variables from .env.prod
Get-Content ".env.prod" | ForEach-Object {
    if ($_ -match "^([^#][^=]+)=(.*)$") {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
    }
}

# Validate required environment variables
$requiredVars = @("POSTGRES_PASSWORD", "JWT_SECRET")
foreach ($var in $requiredVars) {
    if (-not [Environment]::GetEnvironmentVariable($var)) {
        Write-Host "❌ Error: Required environment variable $var is not set!" -ForegroundColor Red
        exit 1
    }
}

Write-Host "✅ Environment validation passed" -ForegroundColor Green

# Create backup of current deployment (if exists)
$backendContainer = docker ps -q -f name=insurance-backend-prod
if ($backendContainer) {
    Write-Host "📦 Creating backup of current deployment..." -ForegroundColor Yellow
    docker-compose -f docker-compose.prod.yml --env-file .env.prod stop
    
    # Export current database (optional)
    if (-not $SkipBackup) {
        Write-Host "💾 Creating database backup..." -ForegroundColor Yellow
        $backupFile = "backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').sql"
        docker exec insurance-postgres-prod pg_dump -U $env:POSTGRES_USER $env:POSTGRES_DB > $backupFile
        Write-Host "✅ Database backup created: $backupFile" -ForegroundColor Green
    }
}

# Pull latest images and build
Write-Host "🔨 Building production images..." -ForegroundColor Yellow
docker-compose -f docker-compose.prod.yml --env-file .env.prod build --no-cache

# Start services
Write-Host "🚀 Starting production services..." -ForegroundColor Blue
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

# Wait for services to be healthy
Write-Host "⏳ Waiting for services to be healthy..." -ForegroundColor Yellow
$timeout = 300  # 5 minutes timeout
$elapsed = 0
$interval = 10

$backendPort = [Environment]::GetEnvironmentVariable("BACKEND_PORT")
if (-not $backendPort) { $backendPort = "8080" }

$frontendPort = [Environment]::GetEnvironmentVariable("FRONTEND_PORT")
if (-not $frontendPort) { $frontendPort = "80" }

while ($elapsed -lt $timeout) {
    try {
        $backendHealth = Invoke-WebRequest -Uri "http://localhost:$backendPort/health/ready" -UseBasicParsing -TimeoutSec 5
        $frontendHealth = Invoke-WebRequest -Uri "http://localhost:$frontendPort/health" -UseBasicParsing -TimeoutSec 5
        
        if ($backendHealth.StatusCode -eq 200 -and $frontendHealth.StatusCode -eq 200) {
            Write-Host "✅ All services are healthy!" -ForegroundColor Green
            break
        }
    }
    catch {
        # Services not ready yet
    }
    
    Write-Host "⏳ Services still starting... ($elapsed`s/$timeout`s)" -ForegroundColor Yellow
    Start-Sleep $interval
    $elapsed += $interval
}

if ($elapsed -ge $timeout) {
    Write-Host "❌ Deployment failed: Services did not become healthy within timeout" -ForegroundColor Red
    Write-Host "📋 Service status:" -ForegroundColor Yellow
    docker-compose -f docker-compose.prod.yml --env-file .env.prod ps
    Write-Host "📋 Backend logs:" -ForegroundColor Yellow
    docker-compose -f docker-compose.prod.yml --env-file .env.prod logs --tail=50 backend
    exit 1
}

# Run post-deployment checks
Write-Host "🔍 Running post-deployment checks..." -ForegroundColor Blue

$apiUrl = "http://localhost:$backendPort"
$endpoints = @("/actuator/health", "/health/ready", "/health/live")

foreach ($endpoint in $endpoints) {
    try {
        $response = Invoke-WebRequest -Uri "$apiUrl$endpoint" -UseBasicParsing -TimeoutSec 10
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ $endpoint`: OK" -ForegroundColor Green
        } else {
            Write-Host "❌ $endpoint`: Failed (HTTP $($response.StatusCode))" -ForegroundColor Red
        }
    }
    catch {
        Write-Host "❌ $endpoint`: Failed (Error: $($_.Exception.Message))" -ForegroundColor Red
    }
}

# Check frontend
try {
    $frontendResponse = Invoke-WebRequest -Uri "http://localhost:$frontendPort" -UseBasicParsing -TimeoutSec 10
    if ($frontendResponse.StatusCode -eq 200) {
        Write-Host "✅ Frontend: OK" -ForegroundColor Green
    } else {
        Write-Host "❌ Frontend: Failed (HTTP $($frontendResponse.StatusCode))" -ForegroundColor Red
    }
}
catch {
    Write-Host "❌ Frontend: Failed (Error: $($_.Exception.Message))" -ForegroundColor Red
}

Write-Host "🎉 Production deployment completed successfully!" -ForegroundColor Green
Write-Host "📊 Access points:" -ForegroundColor Blue
Write-Host "   Frontend: http://localhost:$frontendPort" -ForegroundColor Cyan
Write-Host "   Backend API: http://localhost:$backendPort" -ForegroundColor Cyan
Write-Host "   Health Check: http://localhost:$backendPort/actuator/health" -ForegroundColor Cyan
Write-Host "   Metrics: http://localhost:$backendPort/actuator/metrics" -ForegroundColor Cyan

# Show running containers
Write-Host "📋 Running containers:" -ForegroundColor Blue
docker-compose -f docker-compose.prod.yml --env-file .env.prod ps