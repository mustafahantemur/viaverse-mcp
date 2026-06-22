# ARCHITECTURE.md — Viaverse System Architecture

This document defines the target architecture for Viaverse.

It describes the platform topology, service boundaries, communication styles, data ownership, infrastructure direction, provider abstractions, and non-negotiable architecture rules.

---

## 1. Architecture Goal

Viaverse is built as a serious platform from day one, not as a throwaway production-shaped launch slice.

The architecture must support:

- Local services marketplace
- Social trust layer
- Safe payments
- Worker/business profiles
- Chat and media
- Search and matching
- AI-assisted flows
- SEO/public discovery
- Business subscriptions
- Ads/boost later
- Admin operations
- Moderation and disputes
- Observability and security

Architecture principle:

> Large-scale from day one, but not chaotic. Every technology has one clear purpose.

---

## 2. Architecture Style

Viaverse uses a bounded-context, service-oriented architecture with hexagonal service internals.

The system should not start as a messy monolith. It can start with fewer deployable services if necessary, but boundaries must be explicit from day one.

Target style:

```text
Clients
  -> API Gateway / BFF
  -> Backend services
  -> Service-owned databases
  -> Kafka event backbone
  -> Projections/read models/search indexes
```

Internal service design:

```text
api -> application -> domain -> infrastructure
```

---

## 3. High-Level Platform Topology

```text
User Devices
  ├── Android/iOS KMP Compose App
  ├── Next.js Web / Landing / SEO
  └── Next.js Admin

AWS Edge
  ├── Route 53
  ├── CloudFront
  ├── AWS WAF
  └── AWS Shield

Entry Layer
  ├── API Gateway / App BFF
  ├── REST APIs
  ├── GraphQL BFF for complex read aggregation where useful
  └── Admin BFF

Backend Services
  ├── identity-service
  ├── marketplace-service
  ├── payment-service
  ├── messaging-service
  ├── media-service
  ├── notification-service
  ├── search-service
  ├── trust-gamification-service
  ├── ads-monetization-service
  └── admin-bff

Data Layer
  ├── PostgreSQL
  ├── Cassandra
  ├── OpenSearch
  ├── Valkey / Redis
  └── S3

Event Layer
  └── Kafka / Amazon MSK through Spring Cloud Stream

External Providers
  ├── iyzico
  ├── Stripe
  ├── Masterpass
  ├── Google Maps
  ├── Netgsm
  ├── Amazon SES
  └── AI providers
```

Local S3-compatible development storage should run through SeaweedFS behind a generic object-storage adapter. Domain and application layers must not depend on provider-specific APIs or raw S3 SDK details.

---

## 4. Communication Model

### 4.1 External Client Communication

External clients use REST first.

```text
Mobile/Web/Admin
  -> HTTPS
  -> API Gateway / BFF
  -> REST APIs
```

Rules:

- Versioned REST endpoints
- OpenAPI documentation
- ProblemDetail error model
- Cursor pagination
- Idempotency keys for critical commands
- No JPA entities exposed as API DTOs

### 4.2 GraphQL BFF

GraphQL is allowed for complex read aggregation.

Use cases:

- Home feed aggregation
- Profile aggregation
- Admin dashboards
- Business dashboard views
- Public SEO data composition where useful

Rules:

- GraphQL is not a core service contract.
- GraphQL resolvers do not own business logic.
- BFF calls owning services.
- BFF does not bypass permissions.

### 4.3 Internal Synchronous Communication

Internal sync calls use gRPC or internal REST.

Use for:

- Immediate validation
- Account/capability summary
- Job/quote validation
- Payment workflow validation
- Admin aggregation
- Permission-critical reads

Rules:

- Keep sync calls minimal.
- Avoid distributed transaction chains.
- Use timeouts and circuit breakers.
- Prefer events for propagation.

### 4.4 Internal Asynchronous Communication

Async communication uses Kafka via Spring Cloud Stream.

Use for:

- Domain event propagation
- Search indexing
- Notification creation
- Trust score recalculation
- Badge updates
- Feed/read model projections
- Analytics
- Audit pipelines
- Payment lifecycle propagation

Rules:

- Publish events through outbox.
- Consumers are idempotent.
- Events are versioned.
- DLQ required.
- Include `correlationId` and `causationId`.

---

## 5. Core Services

### 5.1 identity-service

Owns:

- Account
- Account capabilities
- Linked identities
- OTP
- Sessions
- Refresh token families
- Consent records
- Security settings
- Capability status

Does not own:

- Jobs
- Quotes
- Payments
- Messages
- Reviews
- Business analytics

### 5.2 marketplace-service

Owns:

- Service requests
- Budget mode/range
- Quotes
- Quote revisions
- Jobs
- Delivery states
- Cancellation
- Dispute entry
- Marketplace lifecycle policies

### 5.3 payment-service

Owns:

- Payment sessions
- Payment provider callbacks
- Payment authorization/capture state
- Refunds
- Payout instructions
- Commission ledger
- Provider token references
- Payment audit state

Does not store:

- PAN
- CVV
- Raw card data

### 5.4 messaging-service

Owns:

- Conversations
- Messages
- Conversation participants
- Attachment references
- Message reports
- Chat safety signals
- Offer/job-linked messaging state

Future-ready for E2EE but initially server-stored with strong policy controls.

### 5.5 media-service

Owns:

- Upload sessions
- Media assets
- S3 keys
- Thumbnails
- Transcode jobs
- Moderation status
- CDN references

### 5.6 notification-service

Owns:

- Notification inbox
- Notification preferences
- Templates
- Push tokens
- Delivery attempts
- Frequency caps

Channels:

- In-app
- Push
- Email
- SMS

### 5.7 search-service

Owns:

- Search documents
- OpenSearch indexing
- Provider/business/request/feed search
- Saved searches
- Geo discovery
- Search query logs
- Candidate generation support for matching

### 5.8 trust-gamification-service

Owns:

- Review aggregates
- Trust scores
- Badge definitions
- Awarded badges
- Badge eligibility
- Locality score
- Worker/requester/business reputation projections
- Gamification activity logs

### 5.9 ads-monetization-service

Owns:

- Business subscriptions
- Subscription plans
- Free trials
- Boost campaigns
- Sponsored placements
- Ad impressions/clicks
- Monetization configuration

Rules:

- No pay-to-quote.
- Boost cannot bypass trust/safety gates.

### 5.10 admin-bff

Owns no core domain data.

Responsibilities:

- Aggregate admin views
- Enforce admin RBAC
- Call owning services
- Record audit context
- Present operational workflows

---

## 6. Data Ownership Rule

A service owns its source-of-truth data.

A service may have:

- Local projections
- Cached summaries
- Search documents
- Read models

But it must not mutate another service's database.

Data sharing methods:

```text
API call
gRPC call
Kafka event
Read projection
Search document
Explicit cache
```

Forbidden:

```text
Cross-service SQL joins
Shared mutable database tables
Direct repository calls across service boundaries
Hidden coupling through global utility data access
```

---

## 7. Database Strategy

### 7.1 PostgreSQL

Source of truth for transactional data.

Used by:

- identity-service
- marketplace-service
- payment-service
- messaging-service metadata
- media metadata
- trust definitions
- ads subscriptions
- admin configs

Rules:

- Flyway migrations
- Hibernate validate
- No uncontrolled schema generation
- No cross-service joins

### 7.2 Cassandra

High-volume read model and activity storage.

Use for:

- Feed timelines
- Notification inbox
- Activity streams
- Gamification activity log
- Leaderboards/counters
- High-volume append/read projections

Do not use for:

- Payments source of truth
- Account source of truth
- Authorization source of truth
- Quote/job transactional lifecycle source of truth

### 7.3 OpenSearch

Use for:

- Full-text search
- Geo search
- Provider/business discovery
- Category search
- Feed search
- Saved search matching
- Candidate generation for matching
- Public SEO discovery projections

Search documents are projections, not source of truth.

### 7.4 Valkey / Redis

Use for:

- OTP state
- Rate limits
- Short-lived locks
- Frequency caps
- Session revocation cache
- Idempotency hot cache
- Hot availability
- Notification dedupe

### 7.5 S3

Use for:

- Images
- Videos
- Thumbnails
- Attachments
- Work proof
- Business portfolios
- Export files

Access through media-service and provider abstraction.

---

## 8. Event Architecture

Viaverse uses domain events as facts.

Example events:

```text
AccountCreated
AccountCapabilityEnabled
ServiceRequestPublished
MatchingRequested
QuoteSent
QuoteAccepted
JobCreated
PaymentSessionCreated
PaymentAuthorized
WorkDelivered
DeliveryApproved
JobCompleted
ReviewSubmitted
TrustScoreRecalculated
BadgeAwarded
MediaUploaded
MediaApproved
NotificationCreated
DisputeOpened
RefundIssued
BusinessSubscriptionTrialStarted
BoostCampaignCreated
```

Rules:

- Events describe something that happened.
- Events are immutable.
- Events use past tense.
- Events are versioned.
- Events do not contain secrets or raw sensitive payloads.
- Consumers must be idempotent.
- Outbox required for important transactional events.

---

## 9. Provider Abstractions

External providers must not leak into domain logic.

Provider interfaces:

```text
PaymentProvider
MapProvider
NotificationProvider
EmailProvider
SmsProvider
ObjectStorageProvider
CdnProvider
SearchProvider
AiProvider
ModerationProvider
PushProvider
```

Initial adapters:

```text
IyzicoPaymentAdapter
StripePaymentAdapter
MasterpassAdapter
GoogleMapsAdapter
NetgsmSmsAdapter
AmazonSesEmailAdapter
S3ObjectStorageAdapter
CloudFrontCdnAdapter
OpenSearchAdapter
```

Rules:

- Domain does not know provider SDKs.
- Application layer depends on ports.
- Infrastructure implements adapters.
- Provider swaps should not rewrite use cases.

---

## 10. AWS Architecture

First production direction:

```text
Route 53
CloudFront
AWS WAF
AWS Shield
ALB / API Gateway
ECS Fargate
RDS PostgreSQL
Amazon MSK
Amazon OpenSearch Service
ElastiCache for Valkey
S3
Secrets Manager
Amazon SES
CloudWatch / OpenTelemetry pipeline
```

Later:

```text
EKS
External Secrets Operator
Multi-region strategy
Advanced autoscaling
Dedicated analytics warehouse
```

Decision:

- No Firebase core backend.
- No Cloudflare.
- AWS-native edge and infrastructure.

---

## 11. Observability Architecture

Required:

- Spring Boot Actuator
- Micrometer
- OpenTelemetry
- Prometheus
- Alertmanager
- Jaeger
- OpenSearch logs
- Structured JSON logging
- Correlation IDs

Track:

- Request latency
- Error rates
- Service health
- Kafka lag
- Outbox backlog
- Payment callback failures
- Search latency
- Notification delivery failures
- Media processing failures
- Dispute rates
- Abuse/risk signals

Logging rules:

- No secrets.
- No raw card data.
- No tokens.
- No unnecessary PII.
- Include correlationId.

---

## 12. Resilience

Use:

- Timeouts
- Retries
- Circuit breakers
- Bulkheads
- Rate limits
- Idempotency keys
- Outbox
- DLQ
- Backpressure-aware consumers

Tooling:

- Resilience4j
- Spring Retry only where appropriate
- Kafka retry/DLQ patterns
- Valkey rate limits

Rules:

- Payment callbacks are idempotent.
- Duplicate events must not corrupt state.
- Notification failure must not roll back job completion.
- Search failure must not break payment.
- Media processing can be eventually consistent.

---

## 13. Security Architecture

Security includes:

- Spring Security
- JWT access tokens
- Opaque refresh tokens
- Capability-based authorization
- Resource ownership checks
- Business team roles
- Admin RBAC
- Audit logs
- Rate limiting
- WAF
- Shield
- Secrets Manager
- Optional step-up auth

Rules:

- Deny by default.
- Sensitive admin action requires reason.
- Admin access is audited.
- Private data access is scoped.
- Payment data is isolated.
- No raw card storage.

---

## 14. Deployment Strategy

### Phase 1

- ECS Fargate
- RDS PostgreSQL
- MSK or cost-controlled Kafka option compatible with Kafka contract
- OpenSearch
- ElastiCache Valkey
- S3 + CloudFront
- WAF + Shield
- GitHub Actions

### Phase 2

- EKS if service count and ops maturity justify it
- External Secrets Operator
- More advanced autoscaling
- Dedicated read-model tuning
- Production-grade dashboards

### Phase 3

- Multi-region
- Global expansion
- Localized infrastructure policies
- Stripe/global provider enablement

---

## 15. Non-Negotiable Architecture Rules

- One clear technology per responsibility.
- No duplicate broker.
- No Redpanda.
- No RabbitMQ at launch.
- No Firebase core backend.
- No generic query parser.
- No JPA entities in domain.
- No controller business logic.
- No cross-service database joins.
- No direct Kafka clients in use-case code.
- No provider SDKs in domain/application logic.
- No raw card storage.
- No private service requests in public SEO indexes.
- No admin mutations without audit log.

---

## 16. Architecture Quality Gates

A new architectural change must answer:

- Which bounded context owns this?
- Which service owns the source-of-truth data?
- Is sync communication necessary?
- Can this be event-driven?
- Does this expose sensitive data?
- Does it need audit logging?
- Does it need idempotency?
- Does it affect payments?
- Does it affect SEO/public data?
- Does it require an ADR?

If the answer is unclear, do not implement until clarified.


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
