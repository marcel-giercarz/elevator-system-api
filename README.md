
# Elevator System API

A REST API built with Spring Boot that simulates a multi-elevator dispatch system, including a SCAN-based dispatching algorithm and full call lifecycle management.

## Tech stack
- Java 21, Spring Boot (4.0.3)
- PostgreSQL
- Swagger / OpenAPI
- Docker

## How it works

Calling an elevator triggers the dispatcher, which picks the best elevator based on three priorities:
1. An elevator already moving in the right direction that passes through the requested floor
2. The closest idle elevator
3. The least busy elevator (shortest queue).

Stop queue ordering is handled by the SCAN algorithm.

Each call goes through three states: `PENDING` (elevator on its way to pick up) -> `IN_PROGRESS` (passenger selected destination) -> `COMPLETED` (arrived at target floor). Movement is simulated step by step via a dedicated endpoint, which makes it easy to observe the algorithm.

## Endpoints

| Method | Endpoint                       | Description                                  |
|:-------|:-------------------------------|:---------------------------------------------|
| `GET`  | `/api/elevators`               | List all elevators                           |
| `GET`  | `/api/elevators/{id}`          | Get elevator by ID                           |
| `POST` | `/api/elevators`               | Add a new elevator                           |
| `POST` | `/api/elevators/call`          | Call an elevator to a floor                  |
| `POST` | `/api/elevators/floor-request` | Select a target floor                        |
| `POST` | `/api/elevators/step`          | Simulate one movement step for all elevators |
| `GET`  | `/api/elevators/calls`         | Get call history                             |

## Documentation

API Documentation is available, when app is running at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Installation

### Building project locally

Runs with an H2 in-memory database.

```bash
    git clone https://github.com/marcel-giercarz/elevator-system-api.git
    cd elevator-system-api
    ./mvnw spring-boot:run
```

### Building project with Docker

Runs with PostgreSQL.

```bash
    git clone https://github.com/marcel-giercarz/elevator-system-api.git
    cd elevator-system-api
```
After that create **.env** based on .env.example and fill in your database credentials, then:
```
    docker compose up --build 
```

The API will be available at http://localhost:8080.

## Tests

Unit tests cover dispatcher logic, SCAN queue sorting, floor request handling, and arrival state transitions.

To run tests use:
```bash
  ./mvnw test
```
---