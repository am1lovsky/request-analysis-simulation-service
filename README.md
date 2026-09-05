# Request Analysis & Fault Simulation Service

A Spring Boot service for **inspecting live HTTP traffic** and **simulating backend failure scenarios** — built to help developers and QA engineers reproduce and debug hard-to-catch integration issues (timeouts, malformed payloads, unexpected status codes, oversized responses) without touching a real downstream system.

> Final project (*Abschlussprojekt*) for the IHK exam as a **Fachinformatiker für Anwendungsentwicklung**.

---

## Contents

- [Overview](#overview)
- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [API](#api)
- [Getting started](#getting-started)
- [Running with Docker](#running-with-docker)
- [Web UI](#web-ui)
- [Project structure](#project-structure)

---

## Overview

The service has two complementary responsibilities:

1. **Request inspection** — any request sent to `/inspect/**` is echoed back as structured JSON (method, URL, headers, query parameters, body, client IP), making it easy to verify exactly what a client sent.
2. **Fault simulation** — the `/simulate` endpoint can be configured to respond with a specific HTTP status code, an artificial delay, an oversized body, or intentionally broken JSON, so that client-side error handling can be tested against realistic failure modes.

Every simulated call is persisted to MongoDB, giving a searchable history of past test scenarios for reproducibility and analysis.

## Features

- **HTTP request inspector** — full visibility into headers, query params, body and origin of any incoming request.
- **Configurable fault injection**
  - custom HTTP status codes
  - artificial response delay
  - broken / malformed JSON responses
  - inflated response body size
  - custom response body and message
- **Debug mode** (`?isDebug=true`) — returns response metadata alongside the simulated body instead of the raw simulated response.
- **Simulation history** — every run is persisted in MongoDB and retrievable via the API.
- **Lightweight built-in web UI** for configuring and triggering simulations without a REST client.

## Tech stack

| Layer          | Technology                                   |
|----------------|-----------------------------------------------|
| Language       | Java 21                                       |
| Framework      | Spring Boot 3.5 (Web, Validation, Data MongoDB) |
| Database       | MongoDB                                       |
| Build          | Maven (wrapper included, no local install required) |
| Config         | `.env` via `spring-dotenv`                    |
| Containerization | Docker / Docker Compose                     |
| Boilerplate reduction | Lombok                                 |

## Architecture

```
src/main/java/com/requestanalysis/requestanalysisservice
├── analysis/                 # Request inspection
│   ├── controller/           # /inspect/**
│   ├── factory/              # Builds RequestDetails from HttpServletRequest
│   └── model/                # RequestDetails DTO
└── simulate/                 # Fault simulation
    ├── controller/           # /simulate, /simulate/history, /configure
    ├── dto/                  # FaultRequestDto, FaultResponseMeta
    ├── generator/            # Builds the simulated HTTP response
    ├── model/                # Simulation (persisted document)
    ├── repository/           # SimulationRepository (Spring Data MongoDB)
    └── service/               # Configuration, delay handling, status
                                 resolution, history persistence
```

The simulation flow is configure-then-trigger: a client first `POST`s the desired failure scenario to `/configure`, then issues one or more requests to `/simulate`, which builds and returns the configured response while recording it to history.

## API

| Method                    | Endpoint          | Description                                                        |
|---------------------------|-------------------|----------------------------------------------------------------------|
| `ANY /inspect/**`         | Request inspector | Returns method, URL, headers, query params, body and client IP as JSON |
| `POST /configure`         | Fault simulation  | Configures the next simulation scenario (see body below)             |
| `GET, POST, PUT, DELETE /simulate` | Fault simulation | Executes the currently configured scenario and returns the simulated response |
| `GET /simulate/history`   | Fault simulation  | Returns the persisted history of all past simulations                |

**`POST /configure` body:**

```json
{
  "httpMethod": "POST",
  "statusCode": 500,
  "delay": 2000,
  "brokenJson": false,
  "body": "{\"custom\":\"payload\"}",
  "responseSize": 128,
  "baseMessage": "Simulated failure"
}
```

**`GET /simulate?isDebug=true`** returns the response body together with metadata (status code, method, delay, whether JSON was broken, response size, timestamp) instead of just the raw simulated response — useful when scripting automated tests.

## Getting started

### Prerequisites

- Java 21
- A MongoDB instance (local, Docker, or Atlas)

### Configuration

Copy the example environment file and fill in your MongoDB credentials:

```bash
cp src/main/resources/.env.example .env
```

```
MONGO_DATABASE=your-database
MONGO_USER=your-user
MONGO_PASSWORD=your-password
MONGO_CLUSTER=your-cluster-url
```

### Run locally

```bash
./mvnw spring-boot:run
```

The service starts on **http://localhost:9091**.

### Run tests

```bash
./mvnw test
```

## Running with Docker

The provided `Dockerfile` builds the application in a Maven container and runs it on a slim JRE image; `docker-compose.yml` wires it up with the `.env` file created above.

```bash
docker compose up --build
```

## Web UI

A minimal static UI is served at the application root (`src/main/resources/static`) for configuring and triggering fault simulations directly from the browser — handy for quick manual testing without a REST client such as Postman or curl.

## Project structure

```
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── src
    ├── main
    │   ├── java/...           # application code (see Architecture)
    │   └── resources
    │       ├── application.properties / application.yml
    │       ├── .env.example
    │       └── static/        # built-in web UI
    └── test
```

---

## Author

**Anatoliy Milovsky**
