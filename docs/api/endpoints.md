# API Documentation

Complete REST API reference for the Insurance Backoffice System.

## 🔗 Base URL

- **Development**: `http://localhost:8080`
- **Production**: `https://your-domain.com`

## 🔐 Authentication

All API endpoints (except health checks) require JWT authentication.

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@insurance.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "email": "admin@insurance.com",
    "fullName": "Admin User",
    "role": "ADMIN"
  }
}
```

### Using the Token
Include the JWT token in the Authorization header:
```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 📋 Policy Management

### Get All Policies
```http
GET /api/policies
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": 1,
    "policyNumber": "OC-2024-001001",
    "clientName": "Jan Kowalski",
    "vehicleRegistration": "WA12345",
    "insuranceType": "OC",
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "premium": 1200.00,
    "status": "ACTIVE"
  }
]
```

### Get Policy by ID
```http
GET /api/policies/{id}
Authorization: Bearer {token}
```

### Create Policy
```http
POST /api/policies
Authorization: Bearer {token}
Content-Type: application/json

{
  "clientId": 1,
  "vehicleId": 1,
  "insuranceType": "OC",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "discountSurcharge": 0.00
}
```

### Update Policy
```http
PUT /api/policies/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "clientId": 1,
  "vehicleId": 1,
  "insuranceType": "OC",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "discountSurcharge": 100.00
}
```

### Cancel Policy
```http
POST /api/policies/{id}/cancel
Authorization: Bearer {token}
```

### Generate Policy PDF
```http
POST /api/policies/{id}/pdf
Authorization: Bearer {token}
```
**Response:** PDF file download

## 👥 Client Management

### Get All Clients
```http
GET /api/clients
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": 1,
    "fullName": "Jan Kowalski",
    "pesel": "85010112345",
    "email": "jan.kowalski@email.com",
    "phoneNumber": "+48123456789"
  }
]
```

### Get Client by ID
```http
GET /api/clients/{id}
Authorization: Bearer {token}
```

### Create Client
```http
POST /api/clients
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Jan Kowalski",
  "pesel": "85010112345",
  "address": "ul. Przykładowa 123, 00-001 Warszawa",
  "email": "jan.kowalski@email.com",
  "phoneNumber": "+48123456789"
}
```

### Update Client
```http
PUT /api/clients/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Jan Kowalski",
  "pesel": "85010112345",
  "address": "ul. Nowa 456, 00-002 Warszawa",
  "email": "jan.kowalski@newemail.com",
  "phoneNumber": "+48987654321"
}
```

## 🚗 Vehicle Management

### Get All Vehicles
```http
GET /api/vehicles
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": 1,
    "make": "Toyota",
    "model": "Corolla",
    "registrationNumber": "WA12345",
    "vin": "1HGBH41JXMN109186",
    "yearOfManufacture": 2020,
    "engineCapacity": 1600,
    "power": 120
  }
]
```

### Get Vehicle by ID
```http
GET /api/vehicles/{id}
Authorization: Bearer {token}
```

### Create Vehicle
```http
POST /api/vehicles
Authorization: Bearer {token}
Content-Type: application/json

{
  "make": "Toyota",
  "model": "Corolla",
  "registrationNumber": "WA12345",
  "vin": "1HGBH41JXMN109186",
  "yearOfManufacture": 2020,
  "engineCapacity": 1600,
  "power": 120
}
```

### Update Vehicle
```http
PUT /api/vehicles/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "make": "Toyota",
  "model": "Corolla Hybrid",
  "registrationNumber": "WA12345",
  "vin": "1HGBH41JXMN109186",
  "yearOfManufacture": 2020,
  "engineCapacity": 1800,
  "power": 140
}
```

## 👤 User Management

### Get All Users (Admin Only)
```http
GET /api/users
Authorization: Bearer {token}
```

### Create User (Admin Only)
```http
POST /api/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "email": "new.user@insurance.com",
  "password": "password123",
  "fullName": "New User",
  "role": "OPERATOR"
}
```

### Update User (Admin Only)
```http
PUT /api/users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "email": "updated.user@insurance.com",
  "fullName": "Updated User",
  "role": "OPERATOR"
}
```

### Delete User (Admin Only)
```http
DELETE /api/users/{id}
Authorization: Bearer {token}
```

## 📊 Rating Tables

### Get Rating Tables
```http
GET /api/rating-tables
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": 1,
    "insuranceType": "OC",
    "vehicleType": "PASSENGER_CAR",
    "engineCapacityMin": 0,
    "engineCapacityMax": 1400,
    "basePremium": 800.00,
    "riskMultiplier": 1.0
  }
]
```

## 🏥 Health Checks

### Application Health
```http
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

### Detailed Health Info
```http
GET /actuator/health/detailed
Authorization: Bearer {token}
```

## 📝 Request/Response Formats

### Insurance Types
- `OC` - Obligatory Civil Liability
- `AC` - Auto Casco
- `NNW` - Accidents of Uninsured Drivers

### Policy Status
- `ACTIVE` - Policy is active
- `CANCELED` - Policy has been canceled
- `EXPIRED` - Policy has expired

### User Roles
- `ADMIN` - Full system access
- `OPERATOR` - Limited access (no user management)

## ❌ Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/policies"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is missing or invalid",
  "path": "/api/policies"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied - Admin role required",
  "path": "/api/users"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Policy not found with id: 999",
  "path": "/api/policies/999"
}
```

## 🔧 Rate Limiting

- **Rate Limit**: 100 requests per minute per IP
- **Burst Limit**: 20 requests per second
- **Headers**: 
  - `X-RateLimit-Remaining`
  - `X-RateLimit-Reset`

## 📚 Interactive Documentation

Visit the Swagger UI for interactive API documentation:
- **URL**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## 🧪 Testing Examples

### Using cURL
```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@insurance.com","password":"admin123"}' \
  | jq -r '.token')

# Get policies
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/policies
```

### Using Postman
1. Import the OpenAPI specification
2. Set up environment variables for base URL and token
3. Use the pre-request script to automatically get auth token

---

**Related Documentation:**
- [System Architecture](../architecture/system-design.md)
- [Database Schema](../database/schema.md)
- [User Guide](../user-guide/user-manual.md)