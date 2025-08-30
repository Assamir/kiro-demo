# Quick Start Guide

Get the Insurance Backoffice System up and running in minutes.

## 🚀 Prerequisites

- Docker and Docker Compose installed
- Git (to clone the repository)
- 8GB+ RAM recommended
- Ports 3000, 8080, and 5432 available

## ⚡ Quick Setup

### 1. Clone and Start
```bash
git clone <repository-url>
cd insurance-backoffice-system
docker-compose up -d
```

### 2. Wait for Services
The system takes about 2-3 minutes to fully start. Check status:
```bash
docker-compose ps
```
All services should show "Up" status.

### 3. Verify Health
```bash
curl http://localhost:8080/actuator/health
```
Should return `{"status":"UP"}`.

### 4. Access the System
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html

## 🔐 Default Login Credentials

### Administrator Access
```
Email: admin@insurance.com
Password: admin123
```

### Operator Access
```
Email: mike.johnson@insurance.com
Password: password123
```

## 📊 What's Included

The system comes pre-loaded with:
- **443 sample policies** (OC, AC, NNW types)
- **31 clients** with realistic Polish data
- **43 vehicles** with proper specifications
- **8 user accounts** (3 admins, 5 operators)

## 🛠️ Common Commands

### View Logs
```bash
# All services
docker-compose logs
# Specific service
docker-compose logs backend
```

### Restart Services
```bash
# All services
docker-compose restart
# Specific service
docker-compose restart backend
```

### Stop System
```bash
docker-compose down
```

### Reset Database
```bash
docker-compose down -v
docker-compose up -d
```

## 🔍 Verification Checklist

- [ ] All containers are running (`docker-compose ps`)
- [ ] Backend health check passes
- [ ] Frontend loads at http://localhost:3000
- [ ] Can login with admin credentials
- [ ] Policies page loads data
- [ ] Can create a new policy

## 🆘 Troubleshooting

### Services Won't Start
```bash
# Check Docker is running
docker --version
# Check port availability
netstat -an | grep :3000
netstat -an | grep :8080
netstat -an | grep :5432
```

### Database Connection Issues
```bash
# Reset database
docker-compose down -v
docker-compose up -d postgres
# Wait 30 seconds
docker-compose up -d
```

### Frontend Not Loading
```bash
# Check frontend logs
docker-compose logs frontend
# Restart frontend
docker-compose restart frontend
```

## 📚 Next Steps

- Read the [User Manual](../user-guide/user-manual.md)
- Check [API Documentation](../api/endpoints.md)
- Review [System Architecture](../architecture/system-design.md)
- See [Development Setup](../development/setup.md) for code changes

---

**Need more help?** Check the [Troubleshooting Guide](../user-guide/troubleshooting.md)