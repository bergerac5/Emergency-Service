# ERADS

ERADS is a Spring Boot microservices project for emergency response coordination and ambulance dispatch workflows. The current workspace contains a working discovery layer, a gateway, and the core emergency domain service already wired with PostgreSQL persistence and Eureka registration.

## Current Workspace

This repository includes the following services:

- `discovery-server`: Eureka service registry on port `8761`
- `api-gateway`: Spring Cloud Gateway on port `8080`
- `emergency-service`: emergency domain service on port `8081`

## Tech Stack

- Java 25
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3
- PostgreSQL
- Spring Data JPA
- Flyway
- Eureka Discovery Client
- Spring Cloud Gateway (WebMVC)
- Spring Actuator
- Maven

## Runtime Architecture

```text
ERADS/
├── discovery-server/
│   ├── src/
│   ├── pom.xml
│   └── mvnw
├── api-gateway/
│   ├── src/
│   ├── pom.xml
│   └── mvnw
├── emergency-service/
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   └── READme.md
└── ERADS_Professional_Project_Document.pdf
```

## Service Details

### 1. Discovery Server

- Port: `8761`
- Role: Eureka registry and service discovery hub
- Configuration: `eureka.client.register-with-eureka=false` and `fetch-registry=false`

Access URL:

```text
http://localhost:8761
```

### 2. API Gateway

- Port: `8080`
- Role: route entry point for external traffic
- Current route:

```yaml
- id: emergency-service
  uri: lb://emergency-service
  predicates:
    - Path=/api/emergencies/**
  filters:
    - StripPrefix=1
```

This means the gateway exposes the emergency endpoints under the public path:

```text
http://localhost:8080/api/emergencies
```

### 3. Emergency Service

- Port: `8081`
- Role: main backend service for emergency reporting, lookup, pagination, and status updates
- Persistence: PostgreSQL
- Database migration: Flyway enabled
- Health endpoint: Actuator

Access URL:

```text
http://localhost:8081
```

Health check:

```text
http://localhost:8081/actuator/health
```

## Environment Configuration

The service reads database variables from environment or `.env` properties. Current config uses:

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=erads_emergency_service_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

The app config is in:

```text
emergency-service/src/main/resources/application.yaml
```

## Active Emergency API

The emergency service currently exposes these routes through the controller at `/emergencies`:

| Method | Route | Purpose |
| --- | --- | --- |
| `POST` | `/emergencies` | Create a new emergency |
| `POST` | `/emergencies/{id}` | Fetch an emergency by ID |
| `POST` | `/emergencies/track/{accessCode}` | Fetch an emergency by access code |
| `POST` | `/emergencies/retiveAll` | List emergencies with pagination and optional status filter |
| `PATCH` | `/emergencies/{id}/status` | Update an emergency status |

Because the gateway strips the `/api` prefix, the public routes are:

```text
POST http://localhost:8080/api/emergencies
POST http://localhost:8080/api/emergencies/{id}
POST http://localhost:8080/api/emergencies/track/{accessCode}
POST http://localhost:8080/api/emergencies/retiveAll
PATCH http://localhost:8080/api/emergencies/{id}/status
```

## Prerequisites

Before starting the project, make sure you have:

- JDK 25 installed
- Maven installed or use the included Maven wrappers
- PostgreSQL running locally
- A database named `erads_emergency_service_db` available
- The required environment variables set

## Run the Project

Start the services in this order:

### 1. Start Discovery Server

```bash
cd discovery-server
./mvnw spring-boot:run
```

### 2. Start API Gateway

```bash
cd api-gateway
./mvnw spring-boot:run
```

### 3. Start Emergency Service

Linux/macOS:

```bash
cd emergency-service
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=erads_emergency_service_db
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
cd emergency-service
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="erads_emergency_service_db"
$env:DB_USERNAME="your_db_username"
$env:DB_PASSWORD="your_db_password"
./mvnw.cmd spring-boot:run
```

## Build the Project

To build the project for a service module:

```bash
cd emergency-service
./mvnw clean install
```

You can run the same command in each module to compile and package that service independently.

## Current Notes

- The application is configured for Spring Boot 4.1.1 and Java 25.
- Eureka is enabled and the gateway registers with the discovery server.
- Emergency routes are active and delivered through the gateway.
- Flyway is enabled for database migrations.
- The project is already beyond the template stage and is structured around the live emergency domain workflow.

## Next Improvements

Potential next steps for the project include:

- JWT-based authentication and authorization
- API rate limiting and circuit-breaker protection
- Docker and Docker Compose setup
- CI/CD pipeline configuration
- Additional ambulance and dispatch domain modules
- Better API naming consistency and documentation cleanup

## License

This workspace does not yet define a formal project license.
