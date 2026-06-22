# CODING_RULES.md — Viaverse Coding Rules

This document defines engineering rules for Viaverse.

All code should follow the product, architecture, security, data, payment, privacy, event, and client decisions documented in the repository.

Primary source-of-truth order:

1. `AGENTS.md`
2. ADRs under `docs/decisions/`
3. `PRODUCT_MODEL.md`
4. `TECH_STACK_DECISIONS.md`
5. Architecture documents
6. Code

If code and docs conflict, do not silently guess. Fix the code or update the relevant document through a deliberate decision.

---

## 1. Core Engineering Principles

- Build production-grade code from day one.
- Prefer small vertical slices over giant vague changes.
- Keep domain logic out of controllers, UI components, and provider adapters.
- Keep infrastructure details out of the domain layer.
- Use explicit boundaries.
- Use real tests.
- Use migrations.
- Use typed contracts.
- Do not hide unsafe shortcuts in TODOs.
- Do not introduce technology without a documented decision.
- Do not optimize for AI convenience over maintainability.

---

## 2. Forbidden Patterns

Never introduce:

- Firebase as core backend
- Redpanda
- RabbitMQ at launch
- Direct Kafka clients in use-case/application code
- Generic string query parser for database filtering
- Raw SQL string concatenation from user input
- JPA entities in the domain layer
- Controller business logic
- Global god API file
- Fake runtime credentials
- Random JWT subject
- Card PAN/CVV storage
- Pay-to-quote
- Lead-credit burn
- Direct cross-service database joins
- Hibernate `ddl-auto=update` in production
- Business logic inside Compose UI or React components
- Sensitive admin mutation without audit log
- Private service requests indexed in public SEO
- Sensitive data in logs/events
- Custom cryptography for chat
- MinIO
- AGPL/GPL/LGPL/SSPL/copyleft infrastructure without ADR approval
- Local text-file application logging such as `app.log`, `debug.log`, `local.txt`, or `errors.txt`
- Manual JWT construction/parsing/signing outside Spring Security

---

## 3. Repository-Level Rules

Target structure:

```text
viaverse-platform/
  docs/
    decisions/
    diagrams/
    standards/
  services/
    identity-service/
    marketplace-service/
    payment-service/
    messaging-service/
    media-service/
    notification-service/
    search-service/
    trust-gamification-service/
    ads-monetization-service/
    admin-bff/
  apps/
    mobile-kmp/
    web-next/
    admin-next/
  packages/
    api-contracts/
    proto-contracts/
    design-tokens/
    shared-test-fixtures/
  infra/
    docker-compose/
    terraform-or-cdk/
    k8s/
  .github/
    workflows/
```

Rules:

- Shared packages must not become dumping grounds.
- Contracts are versioned.
- Docs live with the repository and are reviewed.
- Every major decision must have an ADR.
- Code generation or AI output must respect boundaries.

---

## 4. Branching Rules

Recommended:

```text
main
feature/<area>-<short-task>
release/<version>
hotfix/<issue>
```

Optional integration branch if the team wants release trains:

```text
develop
```

Rules:

- No direct commits to `main`.
- Every change goes through PR.
- CI must pass before merge.
- Architecture-impacting PRs require doc/ADR update.
- Keep feature branches small.
- Avoid long-running chaotic branches.
- Do not batch unrelated features in one PR.

---

## 5. Commit Rules

Commit messages should be clear and scoped.

Recommended format:

```text
<type>(<scope>): <summary>
```

Types:

```text
feat
fix
docs
test
refactor
chore
build
ci
perf
security
```

Examples:

```text
feat(identity): add OTP challenge creation
fix(payment): make iyzico callback idempotent
docs(architecture): add service ownership matrix
test(marketplace): cover quote acceptance policy
security(auth): enforce tokenVersion check
```

Rules:

- Do not commit generated junk.
- Do not commit secrets.
- Do not mix unrelated work.
- Migration changes should be explicit.

---

## 6. Pull Request Rules

Every PR should include:

- What changed
- Why it changed
- Scope
- Non-goals
- Tests run
- Screenshots if UI
- Migration notes if DB changed
- Security/privacy impact if relevant
- Event/API contract changes if relevant
- Rollback considerations if risky

PR checklist:

```text
- [ ] Follows AGENTS.md
- [ ] Tests added/updated
- [ ] Docs updated if needed
- [ ] No secrets
- [ ] No sensitive logs
- [ ] Authorization checked
- [ ] Error handling standard
- [ ] CI passes
```

---

## 7. Backend Coding Rules

Backend services use Java 25 + Spring Boot with Gradle Kotlin DSL.

Each service follows:

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

### Layer Rules

API layer:

- Controllers are thin.
- Controllers validate request DTO shape.
- Controllers call application use cases.
- Controllers do not call repositories.
- Controllers do not implement business logic.
- Controllers do not publish Kafka events directly.

Application layer:

- Use cases orchestrate.
- Transactions live here.
- Authorization policy is invoked here.
- Ports are called here.
- Outbox events are written here.

Domain layer:

- Pure business model.
- No Spring.
- No JPA.
- No Kafka.
- No HTTP DTOs.
- No provider SDKs.
- Owns invariants and state transitions.

Infrastructure layer:

- JPA entities
- Persistence adapters
- Provider adapters
- Kafka consumers/publishers
- gRPC clients/servers
- Security config
- Spring configuration

---

## 8. Java Style Rules

General:

- Prefer clear code over clever code.
- Use meaningful names.
- Avoid giant methods.
- Avoid hidden side effects.
- Prefer immutable value objects where practical.
- Use constructor injection.
- Avoid field injection.
- Keep null handling explicit.
- Use `Optional` for optional return values where appropriate.
- Use Bean Validation on API DTOs.
- Use domain-specific errors.

Package naming:

```text
app.viaverse.<context>.<layer>
```

Class naming examples:

```text
CreateServiceRequestUseCase
AcceptQuoteUseCase
PaymentProviderPort
JpaServiceRequestRepositoryAdapter
ServiceRequestJpaEntity
ServiceRequestMapper
QuotePolicy
PaymentCallbackHandler
```

---

## 9. Backend Transaction Rules

- Use transactions at use-case/application service level.
- Do not start transactions in controllers.
- Do not perform long external provider calls inside DB transaction unless explicitly safe.
- Persist state and outbox event in one transaction.
- Use optimistic locking for state transitions where needed.
- Payment callbacks must be idempotent.
- Duplicate commands must be safe where user double-click is possible.

---

## 10. Persistence Rules

- Use Flyway for schema changes.
- Use Hibernate validate.
- No `ddl-auto=update` or `ddl-auto=create` for normal development.
- Every service with JPA entities declares Spring Data JPA, PostgreSQL driver, Flyway core, and Flyway PostgreSQL support.
- Migration files must create all tables expected by JPA entities before `bootRun` or debug.
- JPA entities are infrastructure only.
- Domain models are not JPA entities.
- Repositories exposed to application as ports.
- Do not expose entities through API.
- Do not rely on lazy loading in API serialization.
- Use explicit mappers.

Example:

```text
Domain: ServiceRequest
JPA: ServiceRequestJpaEntity
Mapper: ServiceRequestPersistenceMapper
Port: ServiceRequestRepositoryPort
Adapter: JpaServiceRequestRepositoryAdapter
```

---

## 11. Query Rules

Transactional dynamic filters:

- Use JPA Specification / Criteria Predicate.
- QueryDSL may be added later through ADR if justified.
- No generic string filter parser.
- No raw SQL concatenation from user input.
- No controller-built queries.

Search:

- Use OpenSearch through search-service.
- PostgreSQL is not the full discovery engine.
- Search documents are projections.

---

## 12. API Rules

REST external APIs:

- Versioned under `/api/v1`.
- Use DTOs.
- Use request validation.
- Use ProblemDetail errors.
- Use stable error codes.
- Use cursor pagination.
- Use idempotency keys for critical commands.
- Return correlation ID on errors.
- Do not expose stack traces.

Error code examples:

```text
AUTH_OTP_EXPIRED
AUTH_ACCOUNT_SUSPENDED
REQUEST_NOT_FOUND
QUOTE_ALREADY_EXPIRED
PAYMENT_CALLBACK_DUPLICATE
PAYMENT_PROVIDER_FAILED
FORBIDDEN_RESOURCE_ACCESS
```

---

## 13. Event Rules

Kafka access:

- Use Spring Cloud Stream.
- No direct Kafka clients inside use cases.
- Use outbox for important domain events.
- Consumers are idempotent.
- Events are versioned.
- DLQ configured.
- Include correlation ID and causation ID.
- No secrets or raw sensitive data in events.

Event naming:

- Past tense.
- Fact-based.

Examples:

```text
ServiceRequestPublished
QuoteAccepted
PaymentAuthorized
WorkDelivered
ReviewSubmitted
BadgeAwarded
```

---

## 14. gRPC Rules

- Use gRPC for internal typed service calls.
- Do not expose gRPC directly to mobile/web.
- Set deadlines/timeouts.
- Use retries only where safe.
- Avoid long sync call chains.
- Do not use gRPC to bypass data ownership.
- Proto contracts live under `packages/proto-contracts`.

---

## 15. Security Coding Rules

- JWT `sub` is persisted `Account.id`.
- Use Spring Security OAuth2 Resource Server for Bearer token authentication.
- Use Spring Security `JwtEncoder`/`JwtDecoder` with Nimbus-backed implementation.
- Do not manually build, sign, parse, or verify JWT.
- Validate account exists and is active.
- Enforce capability checks.
- Enforce resource ownership checks.
- Use application policy services.
- Deny by default.
- Admin mutations require audit.
- Step-up auth for high-risk actions.
- Rate-limit abuse-prone endpoints.
- Do not log tokens, OTPs, card data, secrets or raw private messages.
- Do not expose internal exception details.

---

## 16. Payment Coding Rules

- No PAN/CVV storage.
- Provider integrations behind `PaymentProvider`.
- Payment callbacks idempotent.
- Store callback records safely.
- Validate amount/currency/provider reference.
- Payout blocked during dispute.
- Refunds and payout releases require `ADMIN_PAYMENT`.
- Payment events contain safe references only.
- Payment screens must explain provider-tokenized cards.

---

## 17. Chat and Media Coding Rules

Chat:

- Conversation participant authorization required.
- Job/quote context checked.
- Contact-sharing detection before allowed state.
- Report/block available.
- Admin access scoped and audited.
- Future E2EE boundary preserved.
- No custom crypto.

Media:

- Presigned S3 upload.
- Confirm upload.
- Metadata validation.
- EXIF stripping.
- Moderation status.
- Private access controlled.
- CDN only for public/approved media or signed private access.

---

## 18. KMP / Compose Coding Rules

- Use `ViaverseTheme`.
- No raw colors in feature screens.
- No giant composables.
- Prefer stateless composables.
- Business logic in ViewModel/use case, not UI.
- Feature modules own presentation state.
- Runtime screens use real repositories/API.
- Preview data is fixture-only and separated.
- Network through Ktor client layer.
- Secure token storage through platform abstraction.
- All user-facing strings localized.
- Accessibility labels added.

Feature structure:

```text
feature/request/
  data/
  domain/
  presentation/
```

State example:

```kotlin
data class RequestCreateState(
    val step: RequestCreateStep,
    val description: String,
    val budgetMode: BudgetMode?,
    val isSubmitting: Boolean,
    val error: UiError?
)
```

---

## 19. React / Next.js Coding Rules

- Use TypeScript.
- Public pages use SSR/SSG.
- Admin pages require auth and RBAC.
- Do not duplicate backend business rules.
- Use typed API clients.
- Use localized SEO metadata.
- Public pages must not expose private data.
- Admin mutations require reason where applicable.
- Components should use design tokens.
- Accessibility and keyboard focus required.

---

## 20. Localization Rules

Initial languages:

- Turkish
- English

Rules:

- No hardcoded user-facing strings.
- Use localization keys.
- Format date/time/currency by locale.
- SEO pages localized.
- Admin content supports localized category names.
- Design should allow text expansion.

---

## 21. Testing Rules

Backend:

- Unit tests for domain policies and value objects.
- Use case tests.
- Repository integration tests with Testcontainers.
- API tests.
- gRPC contract tests.
- Kafka consumer tests.
- Outbox tests.
- Payment callback idempotency tests.
- Authorization tests.
- Architecture tests with ArchUnit or equivalent.

Client:

- ViewModel/state tests.
- Repository tests.
- Fake API tests.
- Critical UI state tests.
- Navigation flow tests.
- Accessibility checks.
- Screenshot/regression tests later.

E2E critical flows:

```text
Login -> request -> quote -> accept -> payment -> delivery -> review
Worker onboarding -> send quote -> complete job
Business onboarding -> subscription -> receive request
Dispute flow
Admin verification/moderation/payment ops
```

---

## 22. Observability Rules

Every important flow should have:

- Structured ECS JSON logs to stdout/stderr
- Correlation ID
- Metrics
- Tracing where relevant
- Safe error code

Application logs must not be written to local text files or `audit_log`. Do not create `app.log`, `debug.log`, `local.txt`, `errors.txt`, or similar default logs.

`audit_log` is for typed security, legal, and account-critical events only. Use typed `AuditAction` values or typed audit event factories; do not pass random action strings.

OpenSearch is the default central log/search store. Graylog is not the default because Graylog Open is SSPL. Loki/Grafana AGPL concerns must be reviewed before adding them. Log collection should use OpenTelemetry Collector or Fluent Bit.

Track:

- Request latency
- Error rate
- Kafka lag
- Outbox backlog
- Payment callback failure
- Search latency
- Notification delivery failure
- Media processing failure
- Rate limit hits
- Authorization denials

Never log:

- Passwords
- OTP codes
- Tokens
- Card data
- Secrets
- Raw private messages
- Verification documents

---

## 23. Configuration Rules

- No secrets in git.
- Provide `.env.example`.
- Use local ignored env files.
- Production secrets in AWS Secrets Manager.
- EKS later through External Secrets Operator.
- All provider credentials externalized.
- No hardcoded URLs for production services.

---

## 24. AI Coding Agent Rules

When using AI coding tools:

- Keep prompts small.
- Link relevant docs.
- Define goal and non-goals.
- Ask for plan before risky work.
- Require tests.
- Require validation commands.
- Do not let AI change architecture silently.
- Do not accept AI-added dependencies without review.
- Do not accept AI-generated security/payment shortcuts.
- Review generated code carefully.

AI task template:

```text
Task title:
Context docs:
Goal:
Non-goals:
Files likely affected:
Architecture constraints:
Security/privacy constraints:
Acceptance criteria:
Validation commands:
Expected PR summary:
```

---

## 25. Definition of Done

A task is done only when:

- Feature works through real backend or explicitly marked fixture-only preview.
- Authorization and ownership checks are implemented.
- Tests added/updated.
- Events/outbox added if state change matters.
- OpenAPI/proto updated if API changes.
- Migrations included if DB changes.
- Errors use standard ProblemDetail.
- Telemetry/logging added for important flows.
- No sensitive data in logs/events.
- UI uses design system.
- Localization handled.
- Docs updated if behavior/decision changed.
- CI passes.

---

## 26. Definition of Ready

A task is ready only when:

- Problem clearly described.
- User value clear.
- Actors identified.
- Scope listed.
- Non-goals listed.
- Relevant docs/ADRs linked.
- Data owner clear.
- API/event impact clear.
- Security/privacy impact considered.
- Acceptance criteria written.
- Validation commands known.

---

## 27. Code Review Checklist

Reviewers should check:

- Does this follow AGENTS.md?
- Does it respect service boundaries?
- Is domain clean?
- Are authorization checks correct?
- Is payment/security/privacy safe?
- Are errors standardized?
- Are tests meaningful?
- Are migrations safe?
- Are events needed and safe?
- Are logs safe?
- Is UI using design system?
- Is the change too broad?
- Are docs updated?

---

## 28. Release Coding Rules

Before release:

- Run full CI.
- Run critical E2E flows.
- Validate migrations.
- Validate rollback plan.
- Validate payment sandbox/production config.
- Validate WAF/rate limits.
- Validate admin RBAC.
- Validate SEO public privacy.
- Validate monitoring/alerts.
- Validate backup/restore plan.
- Check dependency/security scans.

---

## 29. Final Rule

If a shortcut touches identity, payment, privacy, security, trust, moderation, admin, or public SEO data, it is not a harmless shortcut.

Stop and document the correct approach.

---

# Canonical Addendum — Corrected MCP/Service Guardrails

## Global Service Rule

These rules are not identity-only. They apply to all current and future Viaverse backend services.

Every backend service must use Java 25, Spring Boot, Gradle Kotlin DSL, Flyway migrations, Hibernate `ddl-auto=validate`, clear feature/use-case package structure, thin controllers, focused use cases, centralized `AppErrorCode` errors, structured stdout/stderr logs, OpenTelemetry-ready observability, OpenSearch as the default log/search store, audit logging separate from application logging, permissive-license infrastructure unless ADR approves otherwise, and tests for each vertical slice.

## Task Sizing

Avoid prompts such as `finish full template`, `complete all flows`, `port all React screens`, or `build marketplace/payment/chat/business all at once`.

Prefer vertical slices:

- `identity-auth-backend`
- `auth-abuse-protection`
- `mobile-auth-client`
- `mobile-auth-ui`
- `profile-foundation`
- `observability-foundation`
- `search-foundation`

## Error Codes

Do not throw `UnauthorizedException("literal message")` or `ValidationException("literal message")` without an error code. Identity/auth flows must use canonical auth codes such as `AUTH_INVALID_ACCESS_TOKEN`, `AUTH_INVALID_OTP`, `AUTH_RATE_LIMITED`, `AUTH_INVALID_REFRESH_TOKEN`, `AUTH_SESSION_EXPIRED`, and `AUTH_ACCOUNT_NOT_ACTIVE`.

## License and Local Infra

MinIO must not be used. SeaweedFS is allowed for local S3-compatible storage because it is Apache-2.0. Application code uses generic object-storage abstractions.

Local dummy env values may be committed only if explicitly local-only and not real secrets. Production/staging secrets are never committed.


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
