#!/bin/bash

# Production Deployment Script for Insurance Backoffice System
# This script deploys the application to production environment

set -e  # Exit on any error

echo "🚀 Starting production deployment..."

# Check if required environment file exists
if [ ! -f ".env.prod" ]; then
    echo "❌ Error: .env.prod file not found!"
    echo "Please copy .env.prod.example to .env.prod and configure it."
    exit 1
fi

# Load environment variables
source .env.prod

# Validate required environment variables
required_vars=("POSTGRES_PASSWORD" "JWT_SECRET")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "❌ Error: Required environment variable $var is not set!"
        exit 1
    fi
done

echo "✅ Environment validation passed"

# Create backup of current deployment (if exists)
if [ "$(docker ps -q -f name=insurance-backend-prod)" ]; then
    echo "📦 Creating backup of current deployment..."
    docker-compose -f docker-compose.prod.yml --env-file .env.prod stop
    
    # Export current database (optional)
    if [ "${BACKUP_DB:-true}" = "true" ]; then
        echo "💾 Creating database backup..."
        docker exec insurance-postgres-prod pg_dump -U $POSTGRES_USER $POSTGRES_DB > "backup_$(date +%Y%m%d_%H%M%S).sql"
        echo "✅ Database backup created"
    fi
fi

# Pull latest images and build
echo "🔨 Building production images..."
docker-compose -f docker-compose.prod.yml --env-file .env.prod build --no-cache

# Start services
echo "🚀 Starting production services..."
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be healthy..."
timeout=300  # 5 minutes timeout
elapsed=0
interval=10

while [ $elapsed -lt $timeout ]; do
    if docker-compose -f docker-compose.prod.yml --env-file .env.prod ps | grep -q "healthy"; then
        backend_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${BACKEND_PORT:-8080}/health/ready || echo "000")
        frontend_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${FRONTEND_PORT:-80}/health || echo "000")
        
        if [ "$backend_health" = "200" ] && [ "$frontend_health" = "200" ]; then
            echo "✅ All services are healthy!"
            break
        fi
    fi
    
    echo "⏳ Services still starting... (${elapsed}s/${timeout}s)"
    sleep $interval
    elapsed=$((elapsed + interval))
done

if [ $elapsed -ge $timeout ]; then
    echo "❌ Deployment failed: Services did not become healthy within timeout"
    echo "📋 Service status:"
    docker-compose -f docker-compose.prod.yml --env-file .env.prod ps
    echo "📋 Backend logs:"
    docker-compose -f docker-compose.prod.yml --env-file .env.prod logs --tail=50 backend
    exit 1
fi

# Run post-deployment checks
echo "🔍 Running post-deployment checks..."

# Check API endpoints
api_url="http://localhost:${BACKEND_PORT:-8080}"
endpoints=("/actuator/health" "/health/ready" "/health/live")

for endpoint in "${endpoints[@]}"; do
    response=$(curl -s -o /dev/null -w "%{http_code}" "$api_url$endpoint" || echo "000")
    if [ "$response" = "200" ]; then
        echo "✅ $endpoint: OK"
    else
        echo "❌ $endpoint: Failed (HTTP $response)"
    fi
done

# Check frontend
frontend_url="http://localhost:${FRONTEND_PORT:-80}"
frontend_response=$(curl -s -o /dev/null -w "%{http_code}" "$frontend_url" || echo "000")
if [ "$frontend_response" = "200" ]; then
    echo "✅ Frontend: OK"
else
    echo "❌ Frontend: Failed (HTTP $frontend_response)"
fi

echo "🎉 Production deployment completed successfully!"
echo "📊 Access points:"
echo "   Frontend: http://localhost:${FRONTEND_PORT:-80}"
echo "   Backend API: http://localhost:${BACKEND_PORT:-8080}"
echo "   Health Check: http://localhost:${BACKEND_PORT:-8080}/actuator/health"
echo "   Metrics: http://localhost:${BACKEND_PORT:-8080}/actuator/metrics"

# Show running containers
echo "📋 Running containers:"
docker-compose -f docker-compose.prod.yml --env-file .env.prod ps