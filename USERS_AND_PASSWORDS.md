# Insurance Backoffice System - Users and Passwords

This document contains all user accounts and their passwords for the Insurance Backoffice System.

## 🔐 User Accounts

### ADMIN Users

| ID | Email | Password | Role | Description |
|----|-------|----------|------|-------------|
| 1 | admin@insurance.com | admin123 | ADMIN | Primary system administrator |
| 2 | sarah.manager@insurance.com | manager123 | ADMIN | Secondary administrator |
| 7 | admin@company.com | admin123 | ADMIN | Company administrator |

### OPERATOR Users

| ID | Email | Password | Role | Description |
|----|-------|----------|------|-------------|
| 3 | mike.johnson@insurance.com | operator123 | OPERATOR | Insurance operator |
| 4 | lisa.williams@insurance.com | operator123 | OPERATOR | Insurance operator |
| 5 | david.brown@insurance.com | operator123 | OPERATOR | Insurance operator |
| 6 | emma.davis@insurance.com | operator123 | OPERATOR | Insurance operator |
| 8 | operator@company.com | operator123 | OPERATOR | General operator account |

## 🌐 System Access

### Frontend Application
- **URL:** http://localhost:3000
- **Login:** Use any of the email/password combinations above

### Backend API
- **URL:** http://localhost:8080
- **Health Check:** http://localhost:8080/actuator/health
- **API Documentation:** Available after login

### Database Access
- **Host:** localhost:5432
- **Database:** insurance_db
- **Username:** insurance_user
- **Password:** insurance_password

## 🔑 Authentication Notes

1. **Password Policy:** All passwords are currently set to simple values for development/testing
2. **JWT Tokens:** The system uses JWT for authentication with 24-hour expiration
3. **Role-Based Access:** 
   - **ADMIN** users have full system access
   - **OPERATOR** users have limited access to policy management

## 📊 Sample Data

The system includes comprehensive sample data:
- **448 policies** across different insurance types (OC, AC, NNW)
- **31 clients** with Polish names and realistic data
- **43 vehicles** with proper VIN numbers
- **Policy date range:** From April 2022 to May 2025
- **Premium range:** 151.00 - 4,850.75 PLN

## 🚀 Quick Start

1. **Start the system:**
   ```bash
   docker-compose up -d
   ```

2. **Access the frontend:**
   - Open http://localhost:3000
   - Login with any user from the table above

3. **Test API access:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@insurance.com","password":"admin123"}'
   ```

## ⚠️ Security Warnings

- **Development Only:** These credentials are for development/testing purposes only
- **Change Passwords:** In production, all passwords should be changed to secure values
- **Environment Variables:** Consider using environment variables for sensitive data
- **Database Security:** Ensure database access is properly secured in production

## 📝 Notes

- All users were created through database migrations
- Passwords are hashed using BCrypt
- The system supports role-based authorization
- JWT tokens are required for API access after login

---

**Last Updated:** August 29, 2025  
**System Version:** 1.0.0  
**Environment:** Development