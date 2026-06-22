# Viaverse Backend Service Template

This is the global default for every Viaverse backend service, including identity-service, marketplace-service, payment-service, messaging-service, media-service, notification-service, search-service, trust-gamification-service, ads-monetization-service, admin-bff, and future services.

## Baseline

- Java 25.
- Spring Boot.
- Gradle Kotlin DSL.
- REST first for external APIs.
- Spring Security.
- Spring Data JPA + Hibernate where relational persistence is used.
- Flyway for every PostgreSQL schema change.
- Hibernate `ddl-auto=validate`.
- No `ddl-auto=update` or `ddl-auto=create` for normal development.

Every service with JPA entities must include:

- Spring Data JPA.
- PostgreSQL driver.
- Flyway core.
- Flyway PostgreSQL support.
- Flyway Gradle plugin or repository convention where needed.

Migration files must create all tables/columns/indexes expected by JPA entities. Check Flyway migrations before `bootRun`, debugger launch, or service smoke tests.

## Package Structure

Prefer feature/use-case based packages:

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
      focused internal application services
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

Rules:

- Do not dump all entities or repositories into unclear generic packages when a service has multiple features.
- API DTOs live near the API layer.
- JPA entities live under `infrastructure.persistence.entity`.
- Spring Data repositories live under `infrastructure.persistence.repository`.
- Domain enums and states live under `domain.enums` or a clear domain package.
- Helpers/normalizers live in named packages.
- Controllers stay thin.
- Use cases stay focused.
- Avoid `Object` return types from application services.
- Avoid giant service classes mixing OTP creation, registration, session creation, refresh-token rotation, rate limiting, audit, DTO mapping, and validation policy.

## Global Quality Gate

Before accepting a backend service implementation, verify:

1. Clear package boundaries.
2. Thin controllers.
3. Focused use cases.
4. Errors use `AppErrorCode`, not hardcoded service messages.
5. Logs are safe structured stdout/stderr logs.
6. `audit_log` is separate from application logging.
7. Flyway migrations exist and match Hibernate validate.
8. Dependencies use correct Gradle conventions.
9. License rules are respected.
10. Tests are included for the vertical slice.

If the task creates a new service, apply this global template first, then layer service-specific rules on top.
