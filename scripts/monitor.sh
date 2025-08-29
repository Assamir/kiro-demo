#!/bin/bash

# Monitoring Script for Insurance Backoffice System
# This script provides monitoring and health check utilities

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default environment
ENVIRONMENT=${1:-prod}

if [ "$ENVIRONMENT" = "prod" ]; then
    COMPOSE_FILE="docker-compose.prod.yml"
    ENV_FILE=".env.prod"
    BACKEND_PORT=${BACKEND_PORT:-8080}
    FRONTEND_PORT=${FRONTEND_PORT:-80}
elif [ "$ENVIRONMENT" = "staging" ]; then
    COMPOSE_FILE="docker-compose.staging.yml"
    ENV_FILE=".env.staging"
    BACKEND_PORT=${BACKEND_PORT:-8081}
    FRONTEND_PORT=${FRONTEND_PORT:-3001}
else
    echo "Usage: $0 [prod|staging]"
    exit 1
fi

echo -e "${BLUE}🔍 Insurance Backoffice System - Monitoring Dashboard${NC}"
echo -e "${BLUE}Environment: $ENVIRONMENT${NC}"
echo "=================================================="

# Function to check HTTP endpoint
check_endpoint() {
    local url=$1
    local name=$2
    local response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null || echo "000")
    
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ $name: OK (HTTP $response)${NC}"
        return 0
    else
        echo -e "${RED}❌ $name: Failed (HTTP $response)${NC}"
        return 1
    fi
}

# Function to get container status
get_container_status() {
    local container_name=$1
    local status=$(docker inspect --format='{{.State.Status}}' "$container_name" 2>/dev/null || echo "not found")
    local health=$(docker inspect --format='{{.State.Health.Status}}' "$container_name" 2>/dev/null || echo "no health check")
    
    if [ "$status" = "running" ]; then
        if [ "$health" = "healthy" ]; then
            echo -e "${GREEN}✅ $container_name: Running (Healthy)${NC}"
        elif [ "$health" = "unhealthy" ]; then
            echo -e "${RED}❌ $container_name: Running (Unhealthy)${NC}"
        else
            echo -e "${YELLOW}⚠️  $container_name: Running (No health check)${NC}"
        fi
    else
        echo -e "${RED}❌ $container_name: $status${NC}"
    fi
}

# Check container status
echo -e "\n${BLUE}📦 Container Status:${NC}"
if [ "$ENVIRONMENT" = "prod" ]; then
    get_container_status "insurance-postgres-prod"
    get_container_status "insurance-backend-prod"
    get_container_status "insurance-frontend-prod"
else
    get_container_status "insurance-postgres-staging"
    get_container_status "insurance-backend-staging"
    get_container_status "insurance-frontend-staging"
fi

# Check service endpoints
echo -e "\n${BLUE}🌐 Service Health Checks:${NC}"
check_endpoint "http://localhost:$BACKEND_PORT/health/live" "Backend Liveness"
check_endpoint "http://localhost:$BACKEND_PORT/health/ready" "Backend Readiness"
check_endpoint "http://localhost:$BACKEND_PORT/actuator/health" "Backend Actuator Health"
check_endpoint "http://localhost:$FRONTEND_PORT/health" "Frontend Health"
check_endpoint "http://localhost:$FRONTEND_PORT" "Frontend Application"

# Show resource usage
echo -e "\n${BLUE}📊 Resource Usage:${NC}"
if command -v docker stats >/dev/null 2>&1; then
    if [ "$ENVIRONMENT" = "prod" ]; then
        docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}" \
            insurance-postgres-prod insurance-backend-prod insurance-frontend-prod 2>/dev/null || echo "No containers running"
    else
        docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}" \
            insurance-postgres-staging insurance-backend-staging insurance-frontend-staging 2>/dev/null || echo "No containers running"
    fi
fi

# Show recent logs (last 10 lines)
echo -e "\n${BLUE}📋 Recent Backend Logs:${NC}"
if [ -f "$ENV_FILE" ]; then
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" logs --tail=10 backend 2>/dev/null || echo "No logs available"
else
    echo "Environment file $ENV_FILE not found"
fi

# Show application metrics (if available)
echo -e "\n${BLUE}📈 Application Metrics:${NC}"
if curl -s "http://localhost:$BACKEND_PORT/actuator/metrics" >/dev/null 2>&1; then
    echo "Available at: http://localhost:$BACKEND_PORT/actuator/metrics"
    
    # Show some key metrics
    jvm_memory=$(curl -s "http://localhost:$BACKEND_PORT/actuator/metrics/jvm.memory.used" 2>/dev/null | grep -o '"value":[0-9]*' | cut -d':' -f2 || echo "N/A")
    if [ "$jvm_memory" != "N/A" ]; then
        jvm_memory_mb=$((jvm_memory / 1024 / 1024))
        echo "JVM Memory Used: ${jvm_memory_mb}MB"
    fi
    
    http_requests=$(curl -s "http://localhost:$BACKEND_PORT/actuator/metrics/http.server.requests" 2>/dev/null | grep -o '"count":[0-9]*' | cut -d':' -f2 || echo "N/A")
    if [ "$http_requests" != "N/A" ]; then
        echo "HTTP Requests Total: $http_requests"
    fi
else
    echo "Metrics endpoint not available"
fi

echo -e "\n${BLUE}🔗 Useful URLs:${NC}"
echo "Frontend: http://localhost:$FRONTEND_PORT"
echo "Backend API: http://localhost:$BACKEND_PORT"
echo "Health Check: http://localhost:$BACKEND_PORT/actuator/health"
echo "Metrics: http://localhost:$BACKEND_PORT/actuator/metrics"
if [ "$ENVIRONMENT" = "staging" ]; then
    echo "Swagger UI: http://localhost:$BACKEND_PORT/swagger-ui.html"
fi

echo -e "\n${BLUE}💡 Commands:${NC}"
echo "View logs: docker-compose -f $COMPOSE_FILE --env-file $ENV_FILE logs -f [service]"
echo "Restart service: docker-compose -f $COMPOSE_FILE --env-file $ENV_FILE restart [service]"
echo "Scale service: docker-compose -f $COMPOSE_FILE --env-file $ENV_FILE up -d --scale backend=2"