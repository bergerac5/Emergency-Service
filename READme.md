# Emergency Response and Ambulance Dispatch System (ERADS)

ERADS is a Java 25 Spring Boot microservices workspace for emergency reporting, incident tracking, status management, and event-driven dispatch workflows.

The current implementation includes Eureka service discovery, an API gateway, an emergency domain service, PostgreSQL persistence, Flyway migrations, Kafka event delivery, and a transactional outbox.

## Workspace Modules

| Module | Purpose | Port |
| --- | --- | --- |
| `discovery-server` | Eureka service registry | `8761` |
| `api-gateway` | Public route entry point | `8080` |
| `emergency-service` | Emergency reporting, tracking, and status updates | `8081` |
| `dispatch-service` | Dispatch domain service | Configured by the module |
| `eards-events` | Shared event types and event envelopes | Maven library |
| `kafka` | Local Kafka, ZooKeeper, and Kafka UI environment | `9092`, `8089` |

## Technology Stack

- Java 25
- Spring Boot 4.1.3
- Spring Cloud 2025.1.3
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- Spring Actuator
- PostgreSQL
- Flyway
- Eureka
- Spring Cloud Gateway
- Apache Kafka
- MapStruct 1.6.3
- Lombok
- Maven

## Architecture

```text
Client
  |
  | HTTP
  v
API Gateway :8080
  |
  +--> Eureka :8761
  |
  +--> Emergency Service :8081
          |
          +--> PostgreSQL
          |
          +--> Outbox table
                  |
                  | relay every 2 seconds
                  v
               Kafka: emergency.events
```

## Emergency Service Features

- Create emergency reports
- Generate a unique 8-character tracking code
- Retrieve emergencies by UUID or access code
- Filter and paginate emergency records
- Validate status transitions
- Protect concurrent updates with optimistic locking
- Store `EmergencyCreated` events in the transactional outbox
- Relay outbox events to Kafka
- Publish to the `emergency.events` topic with three partitions
- Expose health and operational endpoints through Actuator
- Return structured validation and error responses

## API Routes

The emergency controller is mounted at `/emergencies`.

| Method | Service route | Public gateway route | Purpose |
| --- | --- | --- | --- |
| `POST` | `/emergencies` | `/api/emergencies` | Create an emergency |
| `POST` | `/emergencies/{id}` | `/api/emergencies/{id}` | Get an emergency by UUID |
| `POST` | `/emergencies/track/{accessCode}` | `/api/emergencies/track/{accessCode}` | Track an emergency |
| `POST` | `/emergencies/retiveAll` | `/api/emergencies/retiveAll` | List and filter emergencies |
| `PATCH` | `/emergencies/update/status` | `/api/emergencies/update/status` | Update emergency status |

The gateway matches `/api/emergencies/**` and removes the `/api` prefix before forwarding requests.

### Create an Emergency

```http
POST /api/emergencies
Content-Type: application/json

{
  "type": "MEDICAL",
  "priority": "HIGH",
  "description": "Patient needs urgent medical assistance",
  "latitude": -1.9536,
  "longitude": 30.0606
}
```

Validation rules:

- `type` is required
- `priority` is required
- `description` is required and limited to 255 characters
- `latitude` must be between `-90` and `90`
- `longitude` must be between `-180` and `180`

### Track an Emergency

```http
POST /api/emergencies/track/ABCD2345
```

Access codes use uppercase letters and digits while excluding ambiguous characters.

### List Emergencies

```http
POST /api/emergencies/retiveAll?status=REPORTED&page=0&size=20
```

- `status` is optional
- `page` defaults to `0`
- `size` defaults to `20`
- `size` is limited to `100`
- Results are sorted by `createdAt` in descending order

### Update Emergency Status

```http
PATCH /api/emergencies/update/status
Content-Type: application/json

{
  "id": "00000000-0000-0000-0000-000000000000",
  "status": "DISPATCHED"
}
```

An invalid transition returns HTTP `422`. Concurrent modification conflicts return HTTP `409`.

## Enum Values

Emergency types:

```text
FIRE, MEDICAL, POLICE, NATURAL_DISASTER, OTHER, UNKNOWN
```

Emergency priorities:

```text
LOW, MEDIUM, HIGH, CRITICAL, UNKNOWN
```

Emergency statuses:

```text
REPORTED
WAITING_FOR_DISPATCH
DISPATCHING
DISPATCHED
AMBULANCE_EN_ROUTE
ON_SCENE
COMPLETED
CANCELLED
FAILED
UNKNOWN
```

## Database and Migrations

The emergency service connects to PostgreSQL with these environment variables:

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=erads_emergency_service_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

Spring loads configuration from `src/main/resources/application.yaml` and optionally imports a local `.env` file.

Flyway migrations create and update:

- `emergencies`
- `outbox_events`
- `processed_events`
- the unique `access_code` column
- emergency constraints and indexes
- the JPA optimistic-lock `version` column

## Kafka Events

Emergency creation stores an event envelope and its payload in `outbox_events` in the same database transaction as the emergency record.

- Topic: `emergency.events`
- Partitions: `3`
- Replicas: `1`
- Aggregate key: emergency UUID
- Relay interval: `2` seconds
- Relay batch size: `50`
- Delivery timeout: `5` seconds

The relay adds `eventId` and `eventType` headers to Kafka records.

Local Kafka configuration is defined in `kafka/docker-compose.yml`.

## Prerequisites

- JDK 25
- Maven or the included Maven Wrapper
- PostgreSQL
- Docker and Docker Compose
- A database named `erads_emergency_service_db`

## Local Setup

Run commands from the `ERADS` workspace directory.

### 1. Start PostgreSQL

Create the emergency database and configure an application database account.

### 2. Start Kafka

```powershell
cd kafka
docker compose up -d
```

Kafka is available at `localhost:9092`, and Kafka UI at `http://localhost:8089`.

### 3. Install the Shared Event Library

The emergency service depends on `eards-events`, so install it locally before starting the service.

```powershell
cd ..\eards-events
.\mvnw.cmd clean install
```

### 4. Start Eureka

```powershell
cd ..\discovery-server
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8761`.

### 5. Start the Emergency Service

Set database variables in the current PowerShell session:

```powershell
cd ..\emergency-service
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="erads_emergency_service_db"
$env:DB_USERNAME="your_db_username"
$env:DB_PASSWORD="your_db_password"
.\mvnw.cmd spring-boot:run
```

Direct health check:

```text
http://localhost:8081/actuator/health
```

### 6. Start the API Gateway

```powershell
cd ..\api-gateway
.\mvnw.cmd spring-boot:run
```

Gateway entry point:

```text
http://localhost:8080
```

## Build and Test

Build the shared event library:

```powershell
cd eards-events
.\mvnw.cmd clean install
```

Test and package the emergency service:

```powershell
cd emergency-service
.\mvnw.cmd clean test
.\mvnw.cmd clean package
```

## Current State

- Eureka, API gateway, and emergency service are implemented
- Emergency creation, lookup, tracking, listing, and status updates are active
- PostgreSQL and Flyway persistence is configured
- Access codes and optimistic locking are implemented
- Kafka topic configuration and outbox publishing are implemented
- Status transitions are validated
- Unit and concurrency tests are present
- Authentication and dispatch integration remain future work

## License

No license has been defined for this project.
