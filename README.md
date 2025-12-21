# Task Manager API — Backend

Part of the [dsti-devops suite](https://github.com/sacumesh/dsti-devops). Backend service for the Task Dashboard.

![Backend — Task Manager API](./images/image1.png)

## Technology Stack

- Language: Java
- Framework: Spring Boot
- Database: MariaDB (production), H2 (tests)
- Build: Maven
- Containerization: Docker

## Features

- REST API with CRUD for tasks
- OpenAPI/Swagger documentation
- Spring Boot Actuator (health/metrics)
- Unit tests (JUnit, Spring Boot Test, H2)
- Docker-ready for CI/CD and deployment

---

## API Endpoints

| Method | Endpoint                 | Description        |
|--------|--------------------------|--------------------|
| POST   | `/api/tasks`             | Create a task      |
| GET    | `/api/tasks`             | List all tasks     |
| GET    | `/api/tasks/{id}`        | Get task by ID     |
| PUT    | `/api/tasks`             | Update a task      |
| DELETE | `/api/tasks/{id}`        | Delete a task      |
| GET    | `/actuator/health`       | Health check       |
| GET    | `/swagger-ui/index.html` | API docs (Swagger) |
| GET    | `/actuator/prometheus`   | Prometheus metrics |
| GET    | `/api/version`           | API Version        |

---

## Running the Application

### Prerequisites

- Java 11 or 17
- Maven
- MariaDB
- Docker

### Database Setup (MariaDB)

This project uses Docker Compose to provision and configure the MariaDB database automatically.

To start the MariaDB service and initialize the database, run:

```bash
docker compose -f docker-compose-mariadb.yaml up -d
```

### Configuration (Environment Variables)

The values of the environment variables provided below are the defaults provided in the
```docker-compose-mariadb.yaml```. Make sure to source them using an ```.env``` or export them in the terminal before running the application. 

```bash
export DATABASE_HOST=localhost
export DATABASE_PORT=3306
export DATABASE_NAME=tasks
export DATABASE_USER=user
export DATABASE_PASSWORD=test
export SERVER_PORT=8080  # optional (default: 8080)
```

### Run the Application

Using Maven:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Build and run JAR:

```bash
chmod +x mvnw
./mvnw clean package
java -jar target/*.jar
```

API base URL:

```
http://localhost:8080
```

---

## Tests

- Type: Unit tests
- Framework: JUnit + Spring Boot Test
- Test DB: H2 (in-memory)

Run:
- Unix/macOS
```bash
chmod +x mvnw
./mvnw test
```

- Windows
```bash
mvnw.cmd test
```

---

## CI/CD (GitHub Actions)

- Branches: `develop`, `main`

Triggers:

- Push/Pull Request: run unit tests, build Docker image (no push)
- Manual (`workflow_dispatch`):
  - input Docker tag (e.g., `v1.2.0`)
  - login via secrets
  - build and push to Docker Hub

Workflow references:

- https://github.com/sacumesh/devops-task-manager/tree/main/.github/workflows

Docker Hub image:

- https://hub.docker.com/layers/sacumesh/devops-task-manager/1.0.0

---

## Docker

Build locally:

```bash
docker build --no-cache -t <your-namespace>/<your-repo>:<tag> .
```

---


