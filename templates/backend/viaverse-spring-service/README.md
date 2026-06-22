# Viaverse Spring Service Template

Use this as guidance when scaffolding any backend service. Do not copy application features from this repository.

## Required Baseline

- Java 25.
- Spring Boot.
- Gradle Kotlin DSL.
- Spring Web MVC.
- Spring Security.
- ProblemDetail + `@RestControllerAdvice`.
- Spring Boot Actuator/Micrometer.

If the service has JPA entities, include:

- Spring Data JPA.
- PostgreSQL driver.
- Flyway core.
- Flyway PostgreSQL support.
- Flyway Gradle plugin/convention where needed.

Keep:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Do not use `update` or `create` for normal development.

## Package Template

```text
app.viaverse.<service>.<feature>
  api
    <Feature>Controller.java
    dto
      <Action>Request.java
      <Action>Response.java
  application
    usecase
      <Action>UseCase.java
    service
  domain
    model
    enums
    value
    policy
  infrastructure
    persistence
      entity
      repository
    external
    messaging
    security
    ratelimit
    adapter

app.viaverse.<service>.shared
  error
  normalization
  audit
  config
  mapper
```

## Built-In Guardrails

- Controllers are thin.
- Use cases are focused.
- No giant service classes.
- Errors use `AppErrorCode`.
- Application logs go to structured stdout/stderr.
- `audit_log` is not application logging.
- No local file app logs.
- No manual JWT implementation.
- No MinIO or non-permissive/copyleft infrastructure unless ADR-approved.
- Flyway migrations match JPA entities before boot/debug.
- Add tests for the vertical slice.
