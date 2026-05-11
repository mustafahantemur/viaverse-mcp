# Viaverse Local Infrastructure Rules

Local infrastructure supports development only. It must not change domain/application boundaries.

## Required Local Expectations

- PostgreSQL for transactional persistence.
- Flyway migrations create schema.
- Hibernate validates schema only.
- Valkey/Redis for rate limits, cache, and short-lived state.
- Kafka through Spring Cloud Stream where async integration is in scope.
- OpenSearch may run under search or observability profiles.
- SeaweedFS is allowed for local S3-compatible object storage.

OpenSearch must not be required for basic identity-service tests. Services should gracefully start if the log collection stack is not running unless the task explicitly targets observability.

## Flyway Before Boot

For every backend service with JPA:

- Verify Gradle dependencies include Spring Data JPA, PostgreSQL driver, Flyway core, and Flyway PostgreSQL support.
- Verify migrations exist under `src/main/resources/db/migration`.
- Verify migrations create all tables expected by JPA entities.
- Keep `spring.jpa.hibernate.ddl-auto=validate`.
- Do not use Hibernate update/create to make development boot easier.

## Local Env and Secrets

- Local dummy env values may be committed only when explicitly marked local-only and not real secrets.
- Production/staging secrets must never be committed.
- Production secrets come from environment, Kubernetes Secrets, External Secrets, or AWS Secrets Manager later.
- `.env.local` may be committed only if intentionally local-dummy and repo policy says so.
- Real secrets files must be ignored.
- Local debug OTP and seed users are allowed only in local/test.

## Object Storage

- Do not add MinIO.
- Use SeaweedFS for local S3-compatible storage.
- Application code uses generic object-storage ports/adapters, not provider-specific APIs.

## Observability Profiles

- Logs are emitted to stdout/stderr first.
- Collector pipeline may forward structured logs to OpenSearch.
- OpenSearch Dashboards may be used for local/dev visualization.
- Business code must not depend directly on OpenSearch for logging.
