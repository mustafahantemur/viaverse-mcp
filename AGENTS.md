# AGENTS.md — Viaverse AI Development Rules

This file is the highest-priority instruction document for AI coding agents working on the Viaverse repository.

Viaverse is being rebuilt from zero as a production-grade, trust-first, marketplace-first local services platform with a social trust layer.

The greenfield implementation/prototype is **learning material only**. Do not treat it as the technical foundation.

---

## 1. Product Summary

Viaverse lets one real account enable multiple capabilities:

- Request work
- Do work individually
- Operate a business profile
- Participate in the local social/trust layer
- Use wallet, chat, reviews, badges, support, and safe payments

Account capability distinction:

- Requesting work and doing individual work are capabilities on the same personal account.
- Moving from requester/personal use to individual work should feel like a lightweight Airbnb-style "become a host" onboarding path.
- Business operation is a separate business resource/workspace flow attached to the same account, not the same lightweight switch as individual work.
- Business requires BusinessAccount/profile state, merchant onboarding, verification, staff/team roles, catalog, subscription/billing where applicable, and publish-ready policy before public visibility/lead routing.
- Do not model customer, individual worker, and business as three separate login identities.

The platform combines:

- Local services marketplace
- Smart matching and recommendations
- Safe payment lifecycle
- Worker/business reputation
- Two-sided reviews
- Business profiles and subscriptions
- Local feed / work proof
- AI-assisted search, request creation, support, and moderation
- SEO-ready public pages

Core product principle:

> One account. Multiple capabilities. Marketplace-first. Social trust layer. Safe payments. Smart matching. Scalable architecture.

---

## 2. Absolute Technical Decisions

These decisions are already made. Do not change them unless an ADR explicitly updates them.

### Client

- Android/iOS shared app: **Kotlin Multiplatform**
- Mobile UI: **Compose Multiplatform**
- Web app / landing / public SEO / admin: **React + TypeScript + Next.js**
- Mobile HTTP client: **Ktor Client**
- Mobile local database/cache: **SQLDelight**
- Mobile dependency injection: **Koin**
- Mobile navigation/lifecycle candidate: **Decompose**
- Image loading: **Coil 3**
- UI should use a custom **Viaverse Design System**

### Backend

- Backend language: **Java 25**
- Framework: **Spring Boot**
- Build system: **Gradle Kotlin DSL**
- External APIs: **REST first**
- Complex read aggregation: **GraphQL BFF allowed**, but not core domain contract
- Internal service-to-service synchronous calls: **gRPC**
- Async integration: **Kafka through Spring Cloud Stream**
- ORM: **Spring Data JPA + Hibernate**
- Migrations: **Flyway**
- Hibernate schema mode: **validate only**
- Dynamic transactional filters: **JPA Specification / Criteria Predicate**
- Search/discovery: **OpenSearch**
- High-volume read models: **Cassandra**
- Cache/rate limit/short-lived state: **Valkey/Redis**
- Media/object storage: **S3**
- Cloud: **AWS-native**

### Infrastructure

- DNS/CDN/WAF/DDoS: **Route 53 + CloudFront + AWS WAF + AWS Shield**
- Compute first phase: **ECS Fargate**
- Kubernetes later: **EKS only if justified**
- Kafka production: **Amazon MSK**
- PostgreSQL production: **Amazon RDS PostgreSQL**
- Cache production: **Amazon ElastiCache for Valkey**
- Search production: **Amazon OpenSearch Service**
- Email production: **Amazon SES**
- SMS Turkey: **Netgsm**
- Maps first provider: **Google Maps**, behind `MapProvider`

---

## 3. Forbidden Patterns

Do **not** introduce these patterns:

- Firebase as core backend
- Redpanda
- RabbitMQ at launch
- Direct Kafka clients inside application/use-case code
- Generic string query parser for database filtering
- Raw dynamic SQL string concatenation
- JPA entities in the domain layer
- Controller business logic
- Global god API file in client
- Runtime demo/fake credentials
- Random JWT subject
- Card PAN/CVV storage
- Pay-to-quote
- Lead-credit burn
- Cross-service database joins
- Hibernate `ddl-auto=update` in production
- Business logic inside Compose UI or React components
- Admin actions without audit log
- Private service requests indexed by SEO/search public pages
- Sensitive data in logs/events
- Custom cryptography for chat
- MinIO
- AGPL/GPL/LGPL/SSPL/copyleft infrastructure without ADR approval
- Local text-file application logging such as `app.log`, `debug.log`, `local.txt`, `errors.txt`
- Manual JWT construction/parsing/signing outside Spring Security

---

## 4. Architecture Rules

### 4.1 Service Ownership

Each service owns its data. A service must not mutate another service's source-of-truth database.

Data sharing must happen through:

- REST/gRPC APIs
- Kafka events
- Outbox pattern
- Read projections
- Search documents
- Explicit cached summaries

### 4.2 Backend Package Structure

Each Spring Boot service should follow a hexagonal structure:

```text
app.viaverse.<context>
  api
    controller
    dto
  application
    usecase
    command
    query
    port
  domain
    model
    event
    policy
    error
  infrastructure
    persistence
      entity
      repository
      adapter
      mapper
    messaging
    grpc
    config
    security
```

Rules:

- `domain` contains pure business concepts.
- JPA entities live under `infrastructure.persistence.entity`.
- Controllers are thin and call application use cases.
- Application use cases orchestrate transactions, policies, ports, and events.
- External integrations are adapters behind ports.
- Domain must not depend on Spring, JPA, Kafka, HTTP, or provider SDKs.

### 4.3 Event Rules

Kafka is used through Spring Cloud Stream only.

Use the outbox pattern for domain events that must not be lost:

```text
Use case transaction
  -> update PostgreSQL state
  -> write OutboxEvent in same transaction
Outbox publisher
  -> publishes event through Spring Cloud Stream
Kafka/MSK
  -> consumers update projections, notifications, search, trust, analytics
```

Event rules:

- Events are immutable facts.
- Consumers are idempotent.
- Events must be versioned.
- Use dead-letter topics.
- Do not put secrets, card data, tokens, raw private messages, or unnecessary PII in event payloads.
- Include `correlationId` and `causationId`.

---

## 5. Identity and Security Rules

- JWT `sub` must always be the persisted `Account.id`.
- Never generate a random user ID and put it into JWT `sub`.
- Authenticated requests must validate that the account still exists and is active.
- Refresh tokens are opaque, hashed, rotated, and revocable.
- OTP codes are hashed, short-lived, and rate-limited.
- Rate limits apply to OTP, login, search, request creation, quote sending, chat, media upload, and suspicious actions.
- Step-up auth may be required for payment, admin, verification, and security-sensitive flows.
- Deny authorization by default.

Authorization must consider:

- Active account
- Enabled capability
- Resource ownership
- Business team role
- Admin capability
- Job/payment state
- Risk level
- Requested action

---

## 6. Payment Rules

Viaverse must never store raw card data.

- No PAN
- No CVV
- No direct card storage
- Saved cards are provider tokens only

Payment provider abstraction:

```text
PaymentProvider
  IyzicoPaymentAdapter
  StripePaymentAdapter
  MasterpassAdapter
```

Rules:

- Turkey launch primary provider: iyzico.
- Stripe adapter exists for global expansion but may remain disabled.
- Masterpass adapter-ready.
- Apple Pay / Google Pay through provider-supported wallet flows.
- Provider callbacks must be idempotent.
- Payout is blocked during disputes.
- Commission applies only after completed paid work.

Forbidden:

- Pay-to-quote
- Lead-credit burn
- Charging workers before value is created

---

## 7. Chat and Moderation Rules

Launch chat:

- Server-stored, offer/job-linked messaging
- Strong authorization and safety policy
- PII/contact sharing detection before allowed job state
- Report/block
- Moderation signals
- Support escalation

Future secure mode:

- E2EE-ready boundary
- No custom crypto
- Prefer Matrix/Vodozemac evaluation path
- Avoid libsignal as default
- Abuse report path must remain possible through controlled excerpts/report packages

Before accepted job/payment:

- Detect phone/email/address sharing
- Warn or restrict depending on policy
- Create risk signals on repeated bypass

After accepted job:

- Allow job-relevant contact/address sharing

---

## 8. Client Rules

### 8.1 KMP / Compose

- Use `ViaverseTheme` everywhere.
- Do not scatter raw colors in screens.
- Do not build giant composables.
- Feature modules own presentation state.
- Composables should be mostly stateless where possible.
- Business logic lives in ViewModel/use case, not UI.
- Runtime screens must use real repositories/API, not demo data.
- Preview-only fixtures must stay clearly separated.
- Secure tokens stored through platform secure storage wrapper.
- Navigation should be typed where possible.
- Accessibility labels required.

### 8.2 React / Next.js

- Landing/public SEO pages use SSR/SSG.
- Admin is authenticated and RBAC-protected.
- Do not duplicate backend business logic in frontend.
- Use typed API clients/contracts.
- SEO metadata is structured and localized.
- Public pages avoid private data.
- Admin actions require reason where applicable.
- Design tokens should be shared where possible.

---

## 9. AI-Assisted Development Rules

AI coding tools can help, but only under strict constraints.

For each task:

1. Read this file first.
2. Read only the relevant docs.
3. Create a short implementation plan.
4. Respect architecture boundaries.
5. Keep the change small.
6. Add or update tests.
7. Update docs when decisions change.
8. Never silently change product/architecture decisions.
9. Never introduce forbidden patterns.
10. Report what changed and how it was validated.

AI must not:

- Rewrite architecture without ADR.
- Add new infrastructure technology casually.
- Bypass tests.
- Hide TODOs in critical flows.
- Use mock runtime data for production features.
- Store secrets in code.
- Add provider-specific logic directly into domain/application layers.

---

## 10. Definition of Done

A task is not done unless:

- Feature works through real backend or clearly marked fixture-only development mode.
- Authorization and ownership checks are implemented.
- Relevant tests are added.
- Events/outbox are added if state change matters to other contexts.
- OpenAPI/proto contracts are updated if API changes.
- Errors use standard ProblemDetail.
- Telemetry/logging is added for important flow.
- No sensitive data is logged or emitted in events.
- UI uses design system components.
- Docs are updated if decisions or behavior changed.
- CI passes.

---

## 11. Definition of Ready

A task is ready when it has:

- Problem statement
- User value
- Actor(s)
- Scope
- Non-goals
- Relevant docs/ADRs
- Data owner
- API/event impact
- Security/privacy impact
- Acceptance criteria
- Validation commands

---

## 12. Validation Commands

Exact commands may evolve, but expected validation groups are:

```bash
# Backend
./mvnw clean verify

# Frontend / web / admin
npm run typecheck
npm run lint
npm run build

# Mobile KMP
./gradlew check

# Full repository
npm run verify:all
```

If commands differ in the actual repository, update this file and `README.md`.

---

## 13. Source of Truth

The source of truth is:

1. `AGENTS.md`
2. `README.md`
3. `docs/decisions/*.md` ADRs
4. `PRODUCT_MODEL.md`
5. `TECH_STACK_DECISIONS.md`
6. Architecture and domain docs
7. Code

If code and docs disagree, do not guess. Update the docs through a deliberate decision or fix the code to match documented decisions.


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

---

# Canonical Addendum — Global Backend Service / Observability / License / Auth Rules

## Status

Accepted and mandatory for every Viaverse backend service.

These rules are global, not identity-service-specific. They apply to identity-service, marketplace-service, payment-service, messaging-service, media-service, notification-service, search-service, trust-gamification-service, ads-monetization-service, admin-bff, and future services.

## Build and Persistence Baseline

- Backend services use Java 25, Spring Boot, and Gradle Kotlin DSL.
- PostgreSQL schema changes are managed by Flyway.
- Hibernate `ddl-auto` remains `validate`.
- Do not use Hibernate `update` or `create` for normal development.
- Every service with JPA entities must include Spring Data JPA, PostgreSQL driver, Flyway core, Flyway PostgreSQL support, and Flyway Gradle plugin/convention where needed.
- Migration files must create all tables expected by JPA entities.
- Flyway migrations must be checked before `bootRun` or debug.

## Package Structure

Use feature/use-case based packages:

```text
app.viaverse.<service>.<feature>
  api
    dto
  application
    usecase
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

Controllers stay thin. Use cases stay focused. Avoid giant services. DTOs, JPA entities, repositories, enums, normalizers, rate limiting, and audit helpers must live in clear packages.

## License Policy

Prefer MIT, Apache-2.0, BSD, and ISC.

Avoid AGPL, GPL, LGPL, SSPL, strong copyleft, network copyleft, and source-available infrastructure dependencies unless an ADR explicitly approves them.

MinIO must not be used. SeaweedFS is allowed for local S3-compatible object storage because it is Apache-2.0. Application code must use generic object-storage abstractions, not MinIO-specific or SeaweedFS-specific APIs.

## Observability and Logging

- Application logs go to stdout/stderr as structured JSON.
- Spring Boot services should use ECS structured console logs unless ADR changes it.
- Do not write app logs to local text files.
- Do not write app logs to `audit_log`.
- `audit_log` is for typed security, legal, and account-critical events only.
- OpenSearch is the preferred central log/search store because it is Apache-2.0 aligned with Viaverse's permissive-license policy.
- Graylog is not default because Graylog Open is SSPL.
- Log collection should use OpenTelemetry Collector or Fluent Bit.
- Services must be OpenTelemetry-ready and preserve correlationId/requestId plus traceId/spanId when available.

Never log tokens, Authorization headers, passwords, OTP values, API keys, client secrets, raw phone numbers, raw emails without masking/clear reason, or KVKK/legal sensitive personal data.

## Auth/JWT

- Do not manually build/sign/parse JWT.
- Use Spring Security OAuth2 Resource Server for Bearer authentication.
- Use Spring Security `JwtEncoder`/`JwtDecoder` with Nimbus-backed implementation.
- JWT claims include `iss`, `sub`, `sid`, `iat`, and `exp`.
- `sub` is persisted `Account.id`.
- Refresh tokens are opaque/random, hashed, rotated, and revocable.
- OTP values are hashed; debug fixed OTP is local/test only and must not bypass rate limits, attempts, expiry, used-state, or session security.
- Rate limiting belongs in focused abuse-protection classes, not giant auth services.

## Errors

Do not hardcode user-facing error messages inside service logic. Use typed `AppErrorCode` values. Exceptions carry code plus default message/parameters. `GlobalExceptionHandler` maps exceptions consistently to ProblemDetail. Field validation errors are structured and localizable.

## Global Service Quality Gate

Before any backend service implementation is accepted, verify package boundaries, thin controllers, focused use cases, typed errors, safe structured logs, audit separation, matching Flyway migrations, correct Gradle conventions, license compliance, and tests for the vertical slice.
