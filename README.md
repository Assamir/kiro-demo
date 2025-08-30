# Insurance Backoffice System

A comprehensive web-based application for managing car insurance policies for passenger vehicles.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- 8GB+ RAM recommended
- Ports 3000, 8080, and 5432 available

### Get Running in 5 Minutes
```bash
# Clone and start
git clone <repository-url>
cd insurance-backoffice-system
docker-compose up -d

# Wait for services to start (2-3 minutes)
docker-compose ps

# Access the system
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# API Docs: http://localhost:8080/swagger-ui.html
```

### Default Login Credentials
- **Admin**: admin@insurance.com / admin123
- **Operator**: mike.johnson@insurance.com / password123

## 📚 Documentation

Complete documentation is available in the [docs](docs/) folder:

### 🏗️ Architecture & Design
- [System Requirements](docs/architecture/requirements.md) - Functional and non-functional requirements
- [System Design](docs/architecture/system-design.md) - Technical architecture and design decisions
- [Database Schema](docs/database/schema.md) - Database structure and relationships
- [API Documentation](docs/api/endpoints.md) - REST API reference

### 🚀 Setup & Deployment
- [Quick Start Guide](docs/deployment/quick-start.md) - Get up and running quickly
- [Docker Setup](docs/deployment/docker-setup.md) - Container deployment guide
- [Production Setup](docs/deployment/production-setup.md) - Production deployment guide
- [Environment Configuration](docs/deployment/environment.md) - Configuration settings
- [Users and Passwords](docs/deployment/users-and-passwords.md) - System credentials

### 👨‍💻 Development
- [Development Setup](docs/development/setup.md) - Local development environment
- [Code Structure](docs/development/code-structure.md) - Project organization and conventions
- [Testing Guide](docs/development/testing.md) - Testing strategies and tools
- [Contributing Guidelines](docs/development/contributing.md) - How to contribute to the project

### 📖 User Guide
- [User Manual](docs/user-guide/user-manual.md) - How to use the system
- [Admin Guide](docs/user-guide/admin-guide.md) - Administrative functions
- [Troubleshooting](docs/user-guide/troubleshooting.md) - Common issues and solutions

### 🗄️ Database
- [Schema Documentation](docs/database/schema.md) - Database structure
- [Migration Guide](docs/database/migrations.md) - Database migration procedures
- [Sample Data](docs/database/sample-data.md) - Test data information

## 🏗️ Technology Stack

### Backend
- **Java 17** with Spring Boot 3.2
- **PostgreSQL 15** database
- **Spring Security** with JWT authentication
- **Flyway** for database migrations
- **SpringDoc OpenAPI** for API documentation

### Frontend
- **React 18** with TypeScript
- **Material-UI (MUI)** component library
- **React Router** for navigation
- **Axios** for API communication

### Infrastructure
- **Docker & Docker Compose** for containerization
- **Gradle** for build automation

## 📊 System Status

- **Frontend**: React TypeScript application running on port 3000
- **Backend**: Spring Boot Java application running on port 8080
- **Database**: PostgreSQL running on port 5432
- **Sample Data**: 443 policies, 31 clients, 43 vehicles, 8 users

## 🔗 Quick Links

- **Live System**: http://localhost:3000
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **Complete Documentation**: [docs/README.md](docs/README.md)

## 🆘 Need Help?

- **Quick Issues**: Check [Troubleshooting Guide](docs/user-guide/troubleshooting.md)
- **User Questions**: See [User Manual](docs/user-guide/user-manual.md)
- **Development**: Review [Development Setup](docs/development/setup.md)
- **API Reference**: Visit [API Documentation](docs/api/endpoints.md)

## 🤝 Contributing

We welcome contributions! Please read our [Contributing Guidelines](docs/development/contributing.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.