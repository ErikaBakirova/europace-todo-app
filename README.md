# Europace Todo App - Token-Based Microservices

A simple yet complete implementation of a token-authenticated todo application built with two Spring Boot microservices. This project demonstrates clean service boundaries, RESTful API design, and JWT-based authentication across microservices.

## Architecture Overview

The application consists of two independent microservices:

- **User Service** (Port 8081): Handles user registration, authentication, and token validation
- **Todo Service** (Port 8082): Manages todo items for authenticated users

Both services use H2 in-memory databases and communicate via JWT tokens for stateless authentication.

## Tech Stack

- **Java 17** with Spring Boot 3.4.8
- **Maven** for dependency management (mono-repo setup)
- **H2 Database** for in-memory data storage
- **JWT** for stateless authentication
- **Docker & Docker Compose** for containerization
- **JUnit 5** for testing
- **Swagger UI** for API documentation and testing

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker Desktop (optional, for containerized deployment)

### Running with Docker (Recommended)

1. Clone and navigate to the project:
```bash
git clone git clone https://github.com/ErikaBakirova/europace-todo-app.git
cd europace-todo-app
```

2. Build and start the services:
```bash
docker-compose up --build
```

The services will be available at:
- User Service: http://localhost:8081
- Todo Service: http://localhost:8082
- User Service Swagger UI: http://localhost:8081/swagger-ui/index.html
- Todo Service Swagger UI: http://localhost:8082/swagger-ui/index.html

To stop the services:
```bash
docker-compose down
```

### Running Locally (Development)

For local development without Docker:

```bash
# Build the project
mvn clean install

# Terminal 1 - Start User Service
cd user-service
mvn spring-boot:run

# Terminal 2 - Start Todo Service  
cd todo-service
mvn spring-boot:run
```

## API Testing with Swagger UI

The easiest way to test the API is using the built-in Swagger UI interface.

### Step-by-Step Testing Guide:

#### 1. Get a JWT Token
1. Open User Service Swagger UI: http://localhost:8081/swagger-ui/index.html
2. Use the **POST /register** endpoint to create a new user:
   ```json
   {
     "username": "testuser",
     "password": "password123"
   }
   ```
3. Copy the **token** value from the response (without quotes)

#### 2. Authenticate in Todo Service
1. Open Todo Service Swagger UI: http://localhost:8082/swagger-ui/index.html
2. Click the **🔓 lock icon** ("Authorize" button) at the top of the page
3. In the authorization popup, enter: **Bearer YOUR_TOKEN_HERE**
   - ⚠️ **Important**: Don't add "Bearer" prefix before your token
   - ✅ Example: `eyJhbGciOiJIUzI1NiJ9...`
4. Click **"Authorize"**
5. The lock should now show as 🔒 (closed/authorized)
6. *Note**: Do NOT manually enter the Authorization token in the parameter fields of individual endpoints. The global authorization (lock icon) handles this automatically for all requests.

#### 3. Test Todo Endpoints
Now you can use any todo endpoint:
- **POST /todos** - Create a new todo item
- **GET /todos** - Retrieve all your todos

## Alternative Testing with Postman

If you prefer Postman:

1. **Register/Login**: POST to `http://localhost:8081/register` with username/password
2. **Copy the token** from the response
3. **For todo requests**: Add header `Authorization: Bearer YOUR_TOKEN`
4. **Create todos**: POST to `http://localhost:8082/todos`
5. **Get todos**: GET `http://localhost:8082/todos`

## API Documentation

### User Service Endpoints

#### Register a New User
- **Endpoint**: `POST /register`
- **Request Body**:
```json
{
  "username": "john_doe",
  "password": "secure123"
}
```
- **Response (201 Created)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "userId": 1,
  "message": "Authentication successful"
}
```

#### Login
- **Endpoint**: `POST /login`
- **Request Body**:
```json
{
  "username": "john_doe",
  "password": "secure123"
}
```
- **Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "userId": 1,
  "message": "Authentication successful"
}
```

#### Verify Token
- **Endpoint**: `POST /token`
- **Request Body**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```
- **Response (200 OK)**:
```json
{
  "valid": true,
  "userId": 1,
  "username": "john_doe",
  "message": "Token valid"
}
```

### Todo Service Endpoints

#### Create a Todo
- **Endpoint**: `POST /todos`
- **Headers**: `Authorization: Bearer YOUR_TOKEN`
- **Request Body**:
```json
{
  "text": "Buy groceries"
}
```
- **Response (201 Created)**:
```json
{
  "id": 1,
  "text": "Buy groceries",
  "userId": 1
}
```

#### Get User's Todos
- **Endpoint**: `GET /todos`
- **Headers**: `Authorization: Bearer YOUR_TOKEN`
- **Response (200 OK)**:
```json
[
  {
    "id": 1,
    "text": "Buy groceries",
    "userId": 1
  },
  {
    "id": 2,
    "text": "Write documentation",
    "userId": 1
  }
]
```

## Error Handling

The API returns appropriate HTTP status codes:

- **400 Bad Request**: Invalid request format or missing required fields
- **401 Unauthorized**: Missing, invalid, or expired token

## Development

### Project Structure

```
europace-todo-app/
├── user-service/           # Authentication microservice
│   ├── src/main/java/com/europace/userservice/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access
│   │   ├── entity/         # JPA entities
│   │   └── dto/           # Data transfer objects
│   └── Dockerfile
├── todo-service/          # Todo management microservice  
│   ├── src/main/java/com/europace/todo/service/
│   │   ├── controller/    # REST controllers
│   │   ├── service/       # Business logic  
│   │   ├── repository/    # Data access
│   │   ├── entity/        # JPA entities
│   │   ├── dto/          # Data transfer objects
│   │   └── config/       # Swagger configuration
│   └── Dockerfile
├── docker-compose.yml     # Container orchestration
└── pom.xml               # Parent Maven configuration
```

### Running Tests

```bash
# Run all tests
mvn clean test

# Run tests for specific service
cd user-service && mvn test
cd todo-service && mvn test
```

### Building

```bash
# Build all services
mvn clean install

# Build specific service
cd user-service && mvn clean package
cd todo-service && mvn clean package
```

## Security Considerations

- Passwords are stored in plain text (as specified in requirements)
- JWT tokens have a 24-hour expiration time
- Services validate tokens on every request
- H2 in-memory databases are used for simplicity (data doesn't persist across restarts)

## Implementation Notes

This implementation prioritizes simplicity and clarity over production-grade features:

- Uses shared JWT secrets across services (in real applications, consider asymmetric keys)
- No password hashing (as per challenge requirements)  
- In-memory databases that reset on restart
- Basic validation and error handling
- Synchronous inter-service communication

## Stopping the Application

To stop the Docker containers:
```bash
docker-compose down
```

For local development, stop the processes with `Ctrl+C` in each terminal.
