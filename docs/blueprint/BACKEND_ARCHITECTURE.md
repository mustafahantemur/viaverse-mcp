# BACKEND_ARCHITECTURE.md — Viaverse Backend Architecture

This document defines backend implementation rules for Viaverse Java/Spring Boot services.

---

## 1. Backend Goal

The backend must support:

- Account/capability identity
- Service requests
- Quotes
- Job lifecycle
- Payments
- Messaging
- Media
- Notifications
- Search/matching
- Trust/reviews/badges
- Business subscriptions
- Admin operations
- Moderation/disputes
- SEO/public data
- AI-assisted flows

The backend must be:

- Modular
- Testable
- Observable
- Secure
- Event-driven
- Payment-safe
- Privacy-aware
- Maintainable by future teams

---

## 2. Backend Stack

- Java
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Hibernate
- Flyway
- Spring Cloud Stream Kafka Binder
- Spring gRPC
- Resilience4j
- Bean Validation
- ProblemDetail + `@RestControllerAdvice`
- Micrometer / OpenTelemetry
- Testcontainers

---

## 3. Service Structure

Each backend service follows the same structure:

```text
app.viaverse.<context>
  api
    controller
    dto
    mapper
  application
    usecase
    command
    query
    port
    service
  domain
    model
    event
    policy
    error
    value
  infrastructure
    persistence
      entity
      repository
      adapter
      mapper
    messaging
      publisher
      consumer
      outbox
    grpc
    provider
    config
    security
```

---

## 4. Layer Responsibilities

### 4.1 API Layer

Contains:

- REST controllers
- Request DTOs
- Response DTOs
- API mappers
- Validation annotations
- ProblemDetail mapping entrypoints

Responsibilities:

- Parse request
- Validate DTO shape
- Delegate to application use case
- Return response DTO
- Never implement business logic
- Never use repositories directly

Forbidden in controllers:

- Business decisions
- Transaction orchestration
- JPA queries
- Provider SDK calls
- Kafka publishing
- Authorization shortcuts without policy layer

### 4.2 Application Layer

Contains:

- Use cases
- Commands
- Queries
- Application ports
- Transaction boundaries
- Authorization orchestration
- Outbox write orchestration

Responsibilities:

- Coordinate domain policies
- Load and save through ports
- Enforce resource ownership/capability checks
- Manage transactions
- Produce domain events/outbox records
- Call provider ports where needed
- Return application results

### 4.3 Domain Layer

Contains:

- Domain models
- Value objects
- Domain events
- Policies
- Domain errors

Responsibilities:

- Business invariants
- State transitions
- Domain validation
- Policy decisions independent of infrastructure

Forbidden in domain:

- Spring annotations
- JPA annotations
- Kafka imports
- HTTP DTOs
- Provider SDKs
- Database logic
- Clock/network calls without injected abstractions

### 4.4 Infrastructure Layer

Contains:

- JPA entities
- Spring Data repositories
- Persistence adapters
- Provider adapters
- Kafka publishers/consumers
- gRPC clients/servers
- Security configuration
- Spring configuration

Responsibilities:

- Implement application ports
- Map JPA entities to domain
- Call external providers
- Publish/consume messages
- Integrate with databases and infrastructure

---

## 5. Transaction Rules

- Transactions live at application/use-case level.
- Do not open transactions in controllers.
- Do not perform long external network calls inside DB transactions unless explicitly safe.
- Domain state update and outbox write should happen in the same transaction.
- Payment provider callbacks must be idempotent.
- Use optimistic locking where concurrent updates matter.

Example:

```text
AcceptQuoteUseCase
  -> authorize requester ownership
  -> load quote/request
  -> domain accepts quote
  -> create job
  -> write outbox event
  -> commit
```

---

## 6. Persistence Rules

### 6.1 JPA Entities

JPA entities live only under:

```text
infrastructure.persistence.entity
```

Rules:

- Do not expose entities through API.
- Do not use entities as domain model.
- Use mappers between entity and domain.
- Keep lazy loading controlled.
- Avoid bidirectional relationship mess unless needed.
- Use explicit queries/specifications for complex reads.

### 6.2 Repositories

Spring Data repositories live under infrastructure.

Application layer sees ports, not Spring repositories.

Example:

```java
public interface ServiceRequestRepositoryPort {
    Optional<ServiceRequest> findById(ServiceRequestId id);
    ServiceRequest save(ServiceRequest request);
}
```

Infrastructure implements:

```java
@Component
class JpaServiceRequestRepositoryAdapter implements ServiceRequestRepositoryPort {
    // uses Spring Data repository internally
}
```

---

## 7. Migration Rules

Use Flyway.

Rules:

- Every schema change has a versioned migration.
- Hibernate `ddl-auto=validate`.
- No production `ddl-auto=update`.
- Each service owns its migrations.
- Migrations reviewed like code.
- Destructive migrations require explicit plan.
- Backward-compatible migrations preferred.

Suggested structure:

```text
src/main/resources/db/migration/
  V001__init.sql
  V002__add_account_capabilities.sql
```

---

## 8. Query Rules

Transactional filters:

- Use JPA Specification / Criteria Predicate.
- QueryDSL optional later.
- Avoid raw string query building.

Forbidden:

- Generic string query parser
- User-input concatenated SQL
- Controller query building
- Hidden universal filter DSL without type safety

Search/discovery:

- Use OpenSearch through search-service.
- Do not overload PostgreSQL with full-text/geo marketplace discovery.

---

## 9. API Standards

### 9.1 REST

Use:

```text
/api/v1/<resource>
```

Examples:

```text
POST /api/v1/auth/otp/request
POST /api/v1/auth/otp/verify
GET  /api/v1/accounts/me
POST /api/v1/service-requests
GET  /api/v1/service-requests/{id}
POST /api/v1/service-requests/{id}/publish
POST /api/v1/quotes
POST /api/v1/quotes/{id}/accept
```

### 9.2 Response Rules

- Use DTOs.
- Use explicit status.
- Use cursor pagination for lists.
- Use ProblemDetail for errors.
- Include correlation ID in error responses.
- Do not return stack traces.

### 9.3 Idempotency

Required for:

- Payment session creation
- Payment callbacks
- Refund requests
- Quote acceptance if double-click possible
- Delivery approval
- Media upload confirmation
- Critical admin actions

Use `Idempotency-Key` where appropriate.

---

## 10. Error Handling

Use:

- Domain errors
- Application exceptions
- ProblemDetail mapping
- Field-level validation errors
- Stable error codes

Example error code style:

```text
AUTH_OTP_EXPIRED
AUTH_ACCOUNT_SUSPENDED
REQUEST_NOT_FOUND
QUOTE_ALREADY_EXPIRED
PAYMENT_CALLBACK_DUPLICATE
PAYMENT_PROVIDER_FAILED
FORBIDDEN_RESOURCE_ACCESS
```

Rules:

- Do not leak internal stack traces.
- Do not expose provider secrets.
- Do not expose raw payment provider payloads to clients.
- Log internal details safely with correlation ID.

---

## 11. Security Rules

- Spring Security for authentication.
- Capability-based authorization.
- Resource ownership checks.
- Admin RBAC.
- Step-up auth for sensitive flows.
- Method/use-case level authorization policies.
- Rate limits through Valkey.
- Audit logs for sensitive admin actions.

Backend must not rely on hidden UI buttons for security.

---

## 12. Event Publishing

Use outbox.

Outbox fields may include:

```text
id
aggregateType
aggregateId
eventType
eventVersion
payload
status
createdAt
publishedAt
correlationId
causationId
```

Rules:

- Write outbox in same transaction as state change.
- Publisher publishes through Spring Cloud Stream.
- Consumers idempotent.
- DLQ configured.
- Avoid sensitive payloads.

---

## 13. Event Consumption

Consumer rules:

- Idempotent by event ID.
- Validate event version.
- Handle duplicates.
- Handle out-of-order events where possible.
- Use retry/backoff.
- Send poison messages to DLQ.
- Record consumer failures.

Do not assume exactly-once behavior.

---

## 14. gRPC Rules

Use gRPC for internal typed calls.

Rules:

- Proto contracts live in `packages/proto-contracts`.
- Do not expose gRPC directly to mobile/web.
- Set deadlines/timeouts.
- Handle unavailable services gracefully.
- Avoid sync call chains.
- Do not use gRPC to bypass data ownership.

---

## 15. Service Details

### 15.1 identity-service

Core use cases:

- Request OTP
- Verify OTP
- OAuth sign-in/link
- Create account
- Create session
- Refresh token
- Revoke session
- Enable capability
- Suspend capability
- Manage consent
- Manage linked identities

Critical rules:

- JWT sub = persisted Account.id.
- OTP hashed.
- Refresh tokens hashed and rotated.
- Account status checked.
- Capability status checked.

### 15.2 marketplace-service

Core use cases:

- Create request draft
- Publish request
- Update budget/time/location/media
- Send quote
- Revise quote
- Accept quote
- Create job
- Schedule job
- Mark delivered
- Approve delivery
- Cancel job
- Open dispute

Critical rules:

- Requester owns request.
- Worker/business owns quote.
- Quote acceptance creates job.
- Payment and job states must stay consistent.
- Dispute blocks payout.

### 15.3 payment-service

Core use cases:

- Create payment session
- Handle provider callback
- Store payment state
- Request refund
- Approve refund
- Queue payout
- Release payout
- Calculate commission
- Reconcile provider state

Critical rules:

- No PAN/CVV.
- Provider tokens only.
- Idempotent callbacks.
- Admin payment actions audited.

### 15.4 messaging-service

Core use cases:

- Create conversation
- Send message
- Read messages
- Attach media
- Report message
- Detect contact-sharing attempt
- Escalate support

Critical rules:

- Participant authorization.
- Job/quote context checked.
- PII/contact sharing policy enforced.
- Future E2EE boundary preserved.

### 15.5 media-service

Core use cases:

- Create upload session
- Confirm upload
- Validate metadata
- Trigger moderation
- Generate thumbnail
- Transcode video
- Approve/reject media
- Generate CDN URL

Critical rules:

- Presigned upload.
- EXIF stripping.
- NSFW/malware moderation where possible.
- Private media access controlled.

### 15.6 notification-service

Core use cases:

- Create notification
- Apply preferences
- Apply frequency caps
- Send push/email/SMS
- Write inbox item
- Mark read

Critical rules:

- Auth/security notifications may override marketing preferences where legally allowed.
- SMS reserved for auth/security/high-priority.
- Frequency caps prevent spam.

### 15.7 search-service

Core use cases:

- Index provider/business/request/feed docs
- Search providers
- Search businesses
- Search categories
- Search feed
- Saved search matching
- Candidate generation for matching

Critical rules:

- Search documents are projections.
- Private requests not publicly indexed.
- Index updates consume events.

### 15.8 trust-gamification-service

Core use cases:

- Submit review
- Calculate trust score
- Award badge
- Revoke badge
- Update locality score
- Track activity

Critical rules:

- Reviews only after completed jobs.
- Trust score explainable.
- Badges based on real behavior.

### 15.9 ads-monetization-service

Core use cases:

- Start business subscription trial
- Activate subscription
- Cancel subscription
- Configure plan
- Create boost campaign
- Record sponsored impression/click

Critical rules:

- No pay-to-quote.
- Boost cannot bypass trust/safety.
- Sponsored content labeled.

---

## 16. Testing Strategy

### Unit Tests

- Domain policies
- Value objects
- State transitions
- Use case logic

### Integration Tests

- Repositories
- Flyway migrations
- Kafka consumers
- Provider adapters with mocks
- gRPC clients/servers
- Security filters

### E2E / Contract Tests

- Login
- Request -> quote -> accept -> payment -> job -> review
- Worker onboarding
- Business onboarding
- Dispute
- Admin payment/moderation

### Architecture Tests

Use ArchUnit or equivalent to enforce:

- No JPA in domain.
- No controller to repository access.
- No domain dependency on infrastructure.
- No provider SDK in application/domain.
- Package boundaries.

---

## 17. Observability

Each service must expose:

- Health
- Readiness
- Metrics
- Structured logs
- Trace IDs
- Correlation IDs

Important metrics:

- HTTP latency
- Error rate
- DB latency
- Kafka lag
- Outbox backlog
- Payment callback failure
- Search latency
- Notification send failure
- Media processing failure
- Authorization denial counts
- Rate limit counts

---

## 18. Configuration

Rules:

- No secrets in code.
- Use environment variables/config server pattern.
- Production secrets from AWS Secrets Manager.
- EKS later through External Secrets Operator.
- Local `.env` files ignored by git.
- Provide `.env.example`.

Config groups:

- DB
- Kafka
- Redis/Valkey
- OpenSearch
- S3
- Payment providers
- OAuth providers
- SMS/email providers
- JWT keys
- AI providers

---

## 19. Backend Quality Gates

A backend task is complete only when:

- Use case implemented in application layer.
- Domain policy exists where needed.
- Persistence adapter maps entity/domain cleanly.
- Authorization checked.
- Transaction boundary correct.
- Outbox event added if needed.
- Tests added.
- ProblemDetail errors mapped.
- No sensitive logs.
- Migrations included.
- CI passes.


---

# Canonical Addendum — Typed Domain Rules / No Hardcoded Business Strings

## Status

Accepted and mandatory.

## Greenfield Language Rule

Viaverse is a greenfield implementation. Prototype screenshots are visual/product references only.

Forbidden wording in implementation instructions:

```text
old MVP
previous MVP
legacy MVP
eski MVP
mevcut MVP
reuse MVP structure
continue from MVP
```

Correct wording:

```text
greenfield implementation
from-scratch Viaverse build
prototype screenshots are visual/product reference only
production-shaped launch slice
```

## No Hardcoded Business Role / Status / Type Strings

Business concepts must not be implemented with raw string comparisons.

Forbidden:

```kotlin
if (userType == "customer") { }
if (role == "provider") { }
if (status == "ACTIVE") { }
if (paymentProvider == "iyzico") { }
```

```java
if ("customer".equals(userType)) { }
if ("ACTIVE".equals(status)) { }
```

```ts
if (user.type === "customer") { }
if (job.status === "completed") { }
```

Required:

```kotlin
enum class AccountCapability {
    REQUEST_WORK,
    DO_WORK,
    OPERATE_BUSINESS
}

enum class JobStatus {
    DRAFT,
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    DISPUTED
}
```

```java
public enum BusinessStatus {
    DRAFT,
    PENDING_VERIFICATION,
    VERIFIED,
    PUBLISH_READY,
    PUBLISHED,
    SUSPENDED,
    ARCHIVED
}

@Enumerated(EnumType.STRING)
private BusinessStatus status;
```

```ts
export enum ConversationParticipantRole {
  Requester = "REQUESTER",
  Provider = "PROVIDER",
  BusinessStaff = "BUSINESS_STAFF",
  Support = "SUPPORT"
}
```

## Adapter Boundary Exception

Raw strings are allowed only at adapter boundaries:

- external provider raw status
- HTTP query raw input before validation/mapping
- translation keys
- slugs
- user-generated content

Raw provider values must be mapped immediately:

```java
PaymentProviderStatus mapped = iyzicoStatusMapper.map(rawStatus);
```

Provider raw strings must not leak into domain or application use cases.

## Policy Over If-Soup

Wrong:

```java
if ("provider".equals(role) && "OPEN".equals(status)) {
    // business rule
}
```

Correct:

```java
if (jobPolicy.canSubmitOffer(actor, job)) {
    // business rule
}
```

## Architecture Test Requirement

The codebase must include checks for:

- domain does not depend on Spring/JPA/Hibernate/provider SDKs
- controllers do not depend directly on repositories
- application layer does not depend on adapter implementations
- DTOs do not leak into domain
- forbidden hardcoded business string comparisons
- enum ordinal persistence is not used
