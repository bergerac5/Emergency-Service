# ERADS

ERADS (Emergency Response & Ambulance Dispatch System) is a Spring Boot microservices-based platform designed to support emergency response coordination, ambulance dispatch management, and service communication across distributed backend components.

## Overview

This workspace contains the following services for the ERADS system:

- `discovery-server`: Eureka service registry for ERADS service discovery
- `api-gateway`: Spring Cloud Gateway for routing emergency-related requests to backend services
- `emergency-service`: core ERADS backend service responsible for emergency operations, persistence, and event handling

## Tech Stack

- Java 25
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3
- PostgreSQL
- Spring Data JPA
- Eureka Discovery Client
- Spring Actuator
- Maven

## Project Structure

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

## Services

### 1. Discovery Server

Runs on port `8761`.

- Acts as Eureka server
- Handles service registration and discovery
- Central point for inter-service communication

Access URL:

```text
http://localhost:8761
```

### 2. API Gateway

Runs on port `8080`.

- Routes external requests to downstream services
- Centralizes request entry
- Can handle security, rate limiting, and resilience later in the architecture

Access URL:

```text
http://localhost:8080
```

### 3. Emergency Service

Runs on port `8081`.

- Main domain service for emergency operations
- Uses PostgreSQL as its data store
- Exposes actuator endpoints for health monitoring

Access URL:

```text
http://localhost:8081
```

Health check:

```text
http://localhost:8081/actuator/health
```

## Prerequisites

Before running the services, make sure you have:

- JDK 25 installed
- Maven installed or use the included Maven wrapper scripts
- PostgreSQL database running locally
- Environment variables configured for the database connection

## Environment Variables

The `emergency-service` uses these environment variables:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=erads_emergency_service_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

## Run the Project

Start the services in the following order:

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

```bash
cd emergency-service
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=erads_emergency_service_db
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
./mvnw spring-boot:run
```

On Windows PowerShell:

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

To build all services:

```bash
cd emergency-service
./mvnw clean install
```

You can run the same Maven command in each service module to compile and package that service independently.

## Notes

- The database URL is configured in the `application.yaml` file for the emergency service.
- Eureka discovery is enabled across the services.
- The service architecture is ready for expansion with additional microservices, authentication, and more domain logic.

## Future Improvements

Potential enhancements for the project include:

- JWT-based authentication and authorization
- API rate limiting and circuit breakers
- Docker and Docker Compose setup
- CI/CD pipeline configuration
- Additional emergency domain modules and event-driven integrations

## License

This project is currently configured as a development workspace and does not yet specify a formal license
