#!/bin/bash

# Staging Deployment Script for Insurance Backoffice System
# This script deploys the application to staging environment

set -e  # Exit on any error

echo "🚀 Starting staging deployment..."

# Check if required environment file exists
if [ ! -f ".env.staging" ]; then
    echo "❌ Error: .env.staging file not found!"
    echo "Please copy .env.staging.example to .env.staging and configure it."
    exit 1
fi

# Load environment variables
source .env.staging

echo "✅ Environment validation passed"

# Stop existing staging containers
if [ "$(docker ps -q -f name=insurance-backend-staging)" ]; then
    echo "🛑 Stopping existing staging containers..."
    docker-compose -f docker-compose.staging.yml --env-file .env.staging down
fi

# Build staging images
echo "🔨 Building staging images..."
docker-compose -f docker-compose.staging.yml --env-file .env.staging build

# Start services
echo "🚀 Starting staging services..."
docker-compose -f docker-compose.staging.yml --env-file .env.staging up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check service health
backend_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${BACKEND_PORT:-8081}/health/ready || echo "000")
frontend_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${FRONTEND_PORT:-3001}/health || echo "000")

if [ "$backend_health" = "200" ] && [ "$frontend_health" = "200" ]; then
    echo "✅ Staging deployment completed successfully!"
    echo "📊 Access points:"
    echo "   Frontend: http://localhost:${FRONTEND_PORT:-3001}"
    echo "   Backend API: http://localhost:${BACKEND_PORT:-8081}"
    echo "   Swagger UI: http://localhost:${BACKEND_PORT:-8081}/swagger-ui.html"
    echo "   Health Check: http://localhost:${BACKEND_PORT:-8081}/actuator/health"
else
    echo "❌ Staging deployment failed: Services are not healthy"
    echo "Backend health: $backend_health"
    echo "Frontend health: $frontend_health"
    exit 1
fi