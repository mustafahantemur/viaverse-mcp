# TECH_STACK_DECISIONS.md — Viaverse Technology Decisions

This document records the current technology decisions for Viaverse.

These decisions should not be changed casually. Any major change requires an ADR under `docs/decisions/`.

---

## 1. Decision Principles

Viaverse should avoid technology soup.

The stack must be:

- Open-source friendly
- Strong community support
- Production-grade
- Scalable
- Maintainable
- Familiar enough for future developers
- Not vendor-locked at the domain level
- Clear: one technology per job

Principle:

> Use the right tool for each responsibility, not multiple tools for the same responsibility.

---

## 2. Current Stack Overview

```text
Mobile app: Kotlin Multiplatform + Compose Multiplatform
Web / SEO / Admin: React + TypeScript + Next.js
Backend: Java + Spring Boot
Transactional database: PostgreSQL
High-volume read models: Cassandra
Search/discovery: OpenSearch
Cache/rate limit/hot state: Valkey / Redis
Event broker: Kafka / Amazon MSK
Event abstraction: Spring Cloud Stream
Internal sync communication: gRPC
Media/object storage: S3
Cloud: AWS
Payments: iyzico primary, Stripe adapter included, Masterpass adapter-ready
Maps: Google Maps first, abstracted
Email: Amazon SES
SMS: Netgsm
```

---

## 3. Client Decisions

### 3.1 Kotlin Multiplatform

Decision:

> Use Kotlin Multiplatform for Android/iOS shared client code.

Why:

- Shared business/client logic
- Android-first development without discarding iOS future
- Strong Kotlin ecosystem
- Suitable for long-term mobile codebase
- Avoids maintaining separate native Android/iOS business logic

Scope:

- Shared domain/client models
- API client
- Local cache
- Use cases
- ViewModels where feasible
- Design system primitives where feasible
- Android/iOS shared UI with Compose Multiplatform

Risks:

- iOS build/release requires Mac or cloud Mac build access.
- Some libraries may require platform-specific wrappers.
- Team must be comfortable with KMP.

Mitigation:

- Keep platform-specific code behind interfaces.
- Document platform wrappers.
- Avoid unnecessary KMP experimental dependencies in critical paths.

---

### 3.2 Compose Multiplatform

Decision:

> Use Compose Multiplatform for mobile UI.

Why:

- Kotlin-native UI
- Shared design system
- Better consistency across Android/iOS
- Fits KMP strategy

Rules:

- Use `ViaverseTheme`.
- Use design system components.
- Do not scatter raw colors.
- Do not put business logic inside composables.
- Keep previews separate from runtime repositories.

---

### 3.3 React + Next.js

Decision:

> Use React + TypeScript + Next.js for web app, landing, public SEO pages, and admin.

Why:

- Strong ecosystem
- SEO support through SSR/SSG
- Great fit for public city/category pages
- Great fit for admin console
- Easier to hire for
- Good component/table ecosystem

Scope:

- Landing page
- Public SEO pages
- Provider/business public profiles
- Admin console
- Web app surfaces where needed

Rules:

- Public pages use SSR/SSG.
- Private admin pages require auth and RBAC.
- Do not duplicate backend business logic.
- Use typed API contracts.
- Use localized SEO metadata.

---

## 4. Backend Decisions

### 4.1 Java

Decision:

> Use Java for backend services.

Why:

- Strong open-source ecosystem
- Mature server-side tooling
- Spring Boot ecosystem
- Easier community/hiring compared to niche stacks
- Strong production history
- Good fit for microservices, messaging, security and enterprise integrations

Note:

The founder has .NET/C# familiarity, but Java is selected for ecosystem/community/open-source alignment.

---

### 4.2 Spring Boot

Decision:

> Use Spring Boot for backend services.

Why:

- Mature
- Strong security ecosystem
- Strong data ecosystem
- Kafka integration
- Observability support
- gRPC support
- Good documentation
- Large talent pool

Core Spring modules:

- Spring Web MVC
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring Boot Actuator
- Spring Cloud Stream
- Spring gRPC
- Spring Cache if needed

---

### 4.3 Hibernate + Spring Data JPA

Decision:

> Use Hibernate through Spring Data JPA for ORM.

Why:

- Mature ORM
- Good transaction support
- Good Spring integration
- Entity mapping and persistence standard
- Works well with PostgreSQL

Rules:

- JPA entities are infrastructure, not domain.
- Domain model must not depend on JPA annotations.
- Use repositories/adapters to map between persistence and domain.
- Use `ddl-auto=validate`.
- Do not use `ddl-auto=update` in production.
- Use transactional boundaries at application/use-case layer.

---

### 4.4 Flyway

Decision:

> Use Flyway for database migrations.

Why:

- Versioned migrations
- Repeatable, auditable schema evolution
- Production-safe
- Works well with Spring Boot and PostgreSQL

Rules:

- Every schema change uses Flyway.
- Hibernate only validates schema.
- Migrations reviewed like code.
- Backward-compatible migrations preferred.
- Each service owns its own migrations.

---

### 4.5 Dynamic Queries

Decision:

> Use JPA Specification / Criteria Predicate for transactional dynamic filters.

Why:

- Type safer than raw string concatenation
- Compatible with Spring Data JPA
- Suitable for admin filters and marketplace filters where relational data is needed

Forbidden:

- Generic string query parser
- Raw SQL concatenation from user input
- Controller-built query strings

Optional later:

- QueryDSL if complex type-safe query needs justify it

Search/discovery queries should go to OpenSearch, not force PostgreSQL to behave like a search engine.

---

### 4.6 REST First

Decision:

> Use REST for external client APIs first.

Why:

- Simple
- Well understood
- Mobile-friendly
- OpenAPI documentation
- Easy to debug
- Stable versioned endpoints

Rules:

- Versioned endpoints: `/api/v1/...`
- ProblemDetail errors
- Cursor pagination
- Idempotency keys for critical commands
- Clear DTOs
- No entity exposure

---

### 4.7 GraphQL BFF

Decision:

> GraphQL is allowed as a BFF aggregation layer, not as core service contract.

Use for:

- Complex app home screens
- Profile aggregation
- Admin dashboard aggregation
- Public page aggregation if useful

Do not:

- Put domain business logic in GraphQL resolvers
- Let GraphQL bypass service boundaries
- Use GraphQL as a reason to create tight coupling

---

### 4.8 gRPC

Decision:

> Use gRPC for internal synchronous service-to-service calls.

Use for:

- Account summary reads
- Capability checks
- Job/quote validation
- Payment-service to marketplace validation
- Admin aggregation where immediate response is needed

Rules:

- External clients do not call gRPC directly.
- Proto contracts live under `packages/proto-contracts`.
- Version compatibility must be checked in CI.

---

### 4.9 Spring Cloud Stream + Kafka

Decision:

> Use Kafka as the only event broker, accessed through Spring Cloud Stream.

Why:

- Scalable event backbone
- Good fit for event-driven architecture
- Strong Spring integration
- Avoids direct broker coupling
- Similar abstraction goal to MassTransit in .NET world

Rules:

- No direct Kafka clients in application/use-case code.
- Publish domain events through outbox and Spring Cloud Stream.
- Consumers idempotent.
- Event versioning required.
- DLQ required.
- Correlation IDs required.

---

## 5. Data Technology Decisions

### 5.1 PostgreSQL

Decision:

> PostgreSQL is the transactional source of truth.

Use for:

- Accounts
- Capabilities
- Sessions
- Service requests
- Quotes
- Jobs
- Payments
- Refunds
- Business profiles
- Subscriptions
- Admin operations
- Moderation cases
- Configuration data

Rules:

- One schema/database per service boundary where possible.
- No cross-service joins.
- Flyway migrations.
- Hibernate validate.

---

### 5.2 Cassandra

Decision:

> Cassandra is included from the beginning for high-volume read models.

Use for:

- Feed timelines
- Notification inbox
- Activity streams
- Gamification logs
- Leaderboards/counters
- High-volume append/read projections

Do not use for:

- Account source of truth
- Payment source of truth
- Quote transaction source of truth
- Authorization records
- Relational transaction workflows

Why:

- High write/read throughput
- Time-series/feed/read model fit
- Scales horizontally

Risk:

- Operational complexity

Mitigation:

- Use only for query patterns that justify Cassandra.
- Keep PostgreSQL as source of truth for transactional state.
- Document partition keys carefully.

---

### 5.3 OpenSearch

Decision:

> Use OpenSearch for search, discovery, geo search and search projections.

Use for:

- Provider search
- Business search
- Category search
- Service request discovery
- Feed search
- Saved search matching
- Public profile discovery
- Geo search
- Log search if needed

Rules:

- OpenSearch documents are projections.
- PostgreSQL remains source of truth.
- Indexes updated through events.
- Private service requests are not indexed publicly.

---

### 5.4 Valkey / Redis

Decision:

> Use Valkey/Redis for cache, rate limiting and short-lived state.

Use for:

- OTP rate limiting
- Login throttling
- API rate limits
- Frequency caps
- Short-lived locks
- Hot availability
- Session revocation cache
- Idempotency hot cache
- Notification dedupe

Production:

- Amazon ElastiCache for Valkey

---

### 5.5 S3

Decision:

> Use S3 for object storage.

Use for:

- Images
- Videos
- Thumbnails
- Work proof media
- Business portfolio media
- Chat attachments
- Data exports
- Static assets if needed

CDN:

- CloudFront

Rules:

- Use presigned upload URLs.
- Strip EXIF.
- Scan/moderate media.
- Do not expose private attachments through public CDN URLs.

---

## 6. Cloud Decisions

### 6.1 AWS Native

Decision:

> Use AWS-native infrastructure.

Selected services:

- Route 53
- CloudFront
- AWS WAF
- AWS Shield
- ECS Fargate first
- EKS later if justified
- RDS PostgreSQL
- Amazon MSK
- Amazon OpenSearch Service
- ElastiCache for Valkey
- S3
- Secrets Manager
- Amazon SES

Cloudflare:

- Not used.

Firebase:

- Not used as core backend.

---

### 6.2 ECS First, EKS Later

Decision:

> Start with ECS Fargate. Move to EKS only if complexity justifies it.

Why ECS first:

- Lower operational overhead
- Faster launch
- Good enough for early service deployment
- AWS-native

Why EKS later:

- Strong Kubernetes ecosystem
- External Secrets Operator
- Advanced scaling and deployment patterns
- Multi-service operational maturity

Rule:

> Do not start with Kubernetes just for prestige.

---

## 7. Provider Decisions

### 7.1 Payments

- Turkey primary: iyzico
- Global adapter: Stripe
- Masterpass: adapter-ready
- Apple Pay / Google Pay: through provider-supported wallet flows

All behind:

```text
PaymentProvider
```

---

### 7.2 Maps

- First provider: Google Maps
- Behind:

```text
MapProvider
```

Use for:

- Geocoding
- Reverse geocoding
- Distance
- Map display where needed
- Location autocomplete

Rules:

- Cache geocoding where allowed.
- Monitor cost.
- Rate limit map-heavy operations.

---

### 7.3 SMS

- Turkey SMS provider: Netgsm
- Behind:

```text
SmsProvider
```

Use for:

- Phone OTP
- Critical security notifications if needed

---

### 7.4 Email

- Production: Amazon SES
- Local development: Mailpit
- Behind:

```text
EmailProvider
```

Use for:

- Email OTP
- Receipts
- Security notifications
- Important lifecycle emails
- Marketing only with consent

---

### 7.5 AI

AI provider is not finalized.

All AI usage must go through:

```text
AiProvider
```

Use cases:

- Request writing
- Category suggestion
- Budget guidance
- Search assistant
- Support assistant
- Moderation assist
- SEO/content assist

Rules:

- AI suggestions editable.
- No irreversible AI-only decisions.
- Do not send unnecessary sensitive data.
- Log AI output as suggestions, not facts.

---

## 8. Observability Decisions

Planned stack:

- Spring Boot Actuator
- Micrometer
- OpenTelemetry
- Prometheus
- Alertmanager
- Jaeger
- OpenSearch logs
- Fluent Bit
- Perses dashboards

Required signals:

- Request latency
- Error rate
- Service health
- Kafka lag
- Outbox backlog
- Payment callback failures
- Search latency
- Notification delivery
- Media processing failures
- Dispute rate
- Abuse/risk signals

Rules:

- Structured JSON logs
- Correlation ID on every request/event
- No PII/secrets/card data in logs
- Audit logs for sensitive actions

---

## 9. Security Decisions

- Spring Security
- Capability-based authorization
- Resource ownership checks
- Admin RBAC
- Step-up auth for sensitive flows
- Rate limiting through Valkey
- AWS WAF
- AWS Shield
- Secrets Manager
- External Secrets Operator later if using EKS
- ProblemDetail error responses

---

## 10. Deferred Decisions

These are intentionally deferred:

- Full GraphQL rollout
- Full E2EE chat implementation
- EKS migration
- Full ads platform
- Advanced ML ranking
- Multi-region deployment
- Data warehouse choice
- Advanced A/B experimentation platform

Deferral rule:

> We defer implementation, not architectural boundaries.

---

## 11. ADRs To Create

Initial ADRs should include:

```text
ADR-0001-client-kmp-compose.md
ADR-0002-backend-java-spring-boot.md
ADR-0003-kafka-only-spring-cloud-stream.md
ADR-0004-flyway-hibernate-validate.md
ADR-0005-account-capability-model.md
ADR-0006-jwt-session-model.md
ADR-0007-query-policy.md
ADR-0008-cassandra-read-models.md
ADR-0009-aws-native-edge.md
ADR-0010-payment-provider-abstraction.md
ADR-0011-open-search-discovery.md
ADR-0012-no-firebase-core.md
```

---

## 12. Change Policy

A technology decision can change only when:

- The current decision creates clear unacceptable risk
- A better option is evaluated
- Migration cost is understood
- ADR is written
- Product/architecture impact is reviewed

No AI agent should change these decisions without explicit human approval.


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
