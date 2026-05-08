# ADR-0003 — Java Spring Boot Backend

## Status

Accepted

## Context

Viaverse needs a mature backend stack with strong community, hiring potential, security/data tooling and enterprise reliability.

## Decision

Use Java + Spring Boot for backend services.

Backend services follow hexagonal architecture and bounded-context ownership.

## Consequences

- Java/Spring Boot is the default backend runtime.
- Domain code must remain independent from Spring/JPA/provider SDKs.
- Flyway owns DB migrations.
- Spring Cloud Stream is used for Kafka abstraction.
