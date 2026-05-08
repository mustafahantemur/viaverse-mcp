# ROADMAP.md — Viaverse Delivery Roadmap

This roadmap turns the Viaverse blueprint into an implementation plan.

The goal is not to build a low-quality production-shaped launch slice. The goal is to build a staged production-grade platform with clear boundaries, testable vertical slices, and strong documentation.

---

## 1. Roadmap Principles

- Rebuild from zero.
- Treat greenfield implementation/prototype as learning material only.
- Document first.
- Build small vertical slices.
- Keep architecture boundaries from day one.
- Validate every phase with real tests.
- Avoid mock runtime illusions.
- Do not overbuild advanced features before foundation is stable.
- Defer implementation, not architectural thinking.

---

## 2. Phase Overview

```text
Phase 0 — Foundation Documentation and Decisions
Phase 1 — Repository and Platform Skeleton
Phase 2 — Identity, Onboarding and Security Base
Phase 3 — Request Creation and Matching Core
Phase 4 — Worker and Business Capability
Phase 5 — Quote, Chat, Payment and Job Lifecycle
Phase 6 — Reviews, Trust, Moderation and Support
Phase 7 — Search, SEO, AI Assist and Analytics
Phase 8 — Admin, Monetization and Launch Readiness
Phase 9 — Scale Hardening and Growth
```

---

# Phase 0 — Foundation Documentation and Decisions

## Goal

Create the decision system before code.

AI agents and developers must have a clear source of truth.

## Deliverables

Root docs:

- `AGENTS.md`
- `README.md`
- `PRODUCT_MODEL.md`
- `TECH_STACK_DECISIONS.md`
- `ARCHITECTURE.md`
- `BACKEND_ARCHITECTURE.md`
- `CLIENT_ARCHITECTURE.md`
- `DATA_ARCHITECTURE.md`
- `EVENT_ARCHITECTURE.md`
- `SECURITY_MODEL.md`
- `PAYMENT_MODEL.md`
- `PRIVACY_AND_KVKK.md`
- `MONETIZATION_MODEL.md`
- `GAMIFICATION_AND_TRUST.md`
- `SEO_AND_GROWTH.md`
- `CODING_RULES.md`
- `ROADMAP.md`

Initial ADRs:

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
```

## Exit Criteria

- All foundation docs created.
- ADR structure created.
- Non-negotiable rules documented.
- Technology decisions documented.
- AI task rules documented.
- Team can create repo skeleton without ambiguity.

---

# Phase 1 — Repository and Platform Skeleton

## Goal

Create a working monorepo skeleton with backend services, client apps, shared contracts, infrastructure scaffolding, and CI.

## Deliverables

Repository structure:

```text
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
```

Backend skeleton:

- Spring Boot service template
- Health endpoint
- Actuator
- Flyway baseline
- PostgreSQL connection
- Standard package structure
- ProblemDetail error handler
- Basic test setup
- Dockerfile template

Client skeleton:

- KMP Compose app
- ViaverseTheme skeleton
- Navigation shell
- Auth placeholder screen
- Ktor client base
- SQLDelight setup
- Next.js web
- Next.js admin

Infra skeleton:

- Local Docker Compose for:
  - PostgreSQL
  - Kafka
  - Cassandra
  - OpenSearch
  - Valkey
  - Mailpit
  - optional LocalStack

CI skeleton:

- Backend compile/test
- Frontend typecheck/lint/build
- KMP check
- Docker build check
- Dependency/security scan baseline

## Exit Criteria

- Repo builds.
- All services boot locally or are scaffolded consistently.
- Local infra starts.
- Health endpoints work.
- CI runs.
- Docs included in repo.
- No secrets committed.

---

# Phase 2 — Identity, Onboarding and Security Base

## Goal

Build the secure account foundation.

## Scope

Backend:

- Account model
- Capability model
- Linked identity model
- OTP challenge model
- Session model
- Refresh token rotation
- Consent model
- Basic profile
- Security settings
- Rate limiting
- JWT validation

Client:

- Splash
- Welcome
- Language selection
- Login method selection
- Phone/email OTP
- OAuth button placeholders/integration
- Consent screen
- Intent selection
- Basic profile setup

Admin:

- Basic account viewer
- Capability status viewer
- Session/security view later

## Key APIs

```text
POST /api/v1/auth/otp/request
POST /api/v1/auth/otp/verify
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
GET  /api/v1/accounts/me
PATCH /api/v1/accounts/me/profile
POST /api/v1/consents
GET  /api/v1/consents/me
POST /api/v1/capabilities/work/start
```

## Events

```text
AccountCreated
OtpRequested
OtpVerified
SessionCreated
SessionRevoked
ConsentGranted
AccountCapabilityEnabled
```

## Quality Gates

- JWT `sub` is persisted `Account.id`.
- No random runtime user IDs.
- OTP codes hashed.
- OTP rate limits implemented.
- Refresh token rotation tested.
- Refresh token reuse detection tested.
- Consent version stored.
- Protected endpoint authorization tests pass.
- No sensitive data in logs.

---

# Phase 3 — Request Creation and Matching Core

## Goal

Let a requester create and publish a real service request, then trigger matching.

## Scope

Backend:

- Category model
- Service request draft
- Budget mode/range
- Location approximation
- Media reference attachment
- Publish flow
- MatchingRequested event
- Search projection skeleton

Client:

- Create sheet
- Request wizard
- AI category suggestion placeholder/stub
- Budget mode selector
- Location/time screen
- Media upload draft UI
- Publish preview
- Publish success

Search/matching:

- Basic category/location candidate generation
- OpenSearch index skeleton
- Worker opportunity projection skeleton

## Key APIs

```text
POST /api/v1/service-requests
PATCH /api/v1/service-requests/{id}
POST /api/v1/service-requests/{id}/publish
GET  /api/v1/service-requests/{id}
GET  /api/v1/categories
POST /api/v1/media/upload-sessions
```

## Events

```text
ServiceRequestDraftCreated
ServiceRequestPublished
MediaAttachedToRequest
MatchingRequested
SearchIndexUpdated
```

## Quality Gates

- User can publish a real request.
- Request stored in PostgreSQL.
- Flyway migrations included.
- Outbox event emitted.
- Search projection created.
- Private request visibility rules respected.
- No mock runtime data.

---

# Phase 4 — Worker and Business Capability

## Goal

Enable users to do work individually or operate a business profile.

## Worker Scope

- Enable Work Mode
- Worker onboarding
- Worker categories
- Service area
- Availability
- Worker profile
- Portfolio references
- Opportunity queue
- Send free offer skeleton

## Business Scope

- Business profile creation
- Business verification status
- Services
- Packages
- Operating hours
- Service area
- Portfolio
- Team model baseline
- Business subscription trial state

## Key APIs

```text
POST /api/v1/work/onboarding/start
PATCH /api/v1/work/profile
GET  /api/v1/work/opportunities
POST /api/v1/businesses
PATCH /api/v1/businesses/{id}
POST /api/v1/businesses/{id}/services
POST /api/v1/businesses/{id}/packages
POST /api/v1/businesses/{id}/subscription/trial
```

## Events

```text
WorkerProfileCreated
WorkerProfileActivated
WorkerCategoriesUpdated
BusinessProfileCreated
BusinessVerificationSubmitted
BusinessProfilePublished
BusinessSubscriptionTrialStarted
```

## Quality Gates

- User can switch Personal/Work/Business mode.
- Worker profile uses same account.
- Business profile uses same account with business capability.
- Worker can view matched opportunities.
- Business profile can be public-safe.
- Subscription trial state exists.
- No pay-to-quote.

---

# Phase 5 — Quote, Chat, Payment and Job Lifecycle

## Goal

Complete the core marketplace transaction flow.

## Scope

Marketplace:

- Send quote
- Quote revision
- Ask question
- Accept quote
- Create job
- Job status lifecycle
- Delivery approval

Chat:

- Conversation creation
- Offer/job-linked chat
- Message sending/reading
- Attachment references
- Contact-sharing detection baseline

Payment:

- Payment session
- iyzico adapter
- Stripe adapter skeleton
- Provider-tokenized saved card model
- Callback handling
- Idempotency
- Commission calculation
- Payout eligibility after completion

Client:

- Quote comparison
- Job workspace
- Chat screen
- Payment confirmation
- Payment result
- Delivery approval
- Review prompt placeholder

## Key APIs

```text
POST /api/v1/quotes
PATCH /api/v1/quotes/{id}
POST /api/v1/quotes/{id}/accept
GET  /api/v1/jobs/{id}
POST /api/v1/jobs/{id}/start
POST /api/v1/jobs/{id}/deliver
POST /api/v1/jobs/{id}/approve-delivery
POST /api/v1/payment-sessions
POST /api/v1/payment-provider-callbacks/iyzico
GET  /api/v1/conversations/{id}/messages
POST /api/v1/conversations/{id}/messages
```

## Events

```text
QuoteSent
QuoteRevisionRequested
QuoteAccepted
JobCreated
PaymentSessionCreated
PaymentAuthorized
PaymentFailed
WorkStarted
WorkDelivered
DeliveryApproved
JobCompleted
CommissionCalculated
PayoutEligible
MessageSent
ContactSharingDetected
```

## Quality Gates

- Full flow works:
  - request
  - quote
  - accept
  - payment
  - job
  - delivery
  - approval
- Payment callbacks idempotent.
- No PAN/CVV stored.
- Chat linked to job/quote.
- Payout blocked until completion.
- Dispute path designed even if not fully implemented.
- Payment failure states clear.

---

# Phase 6 — Reviews, Trust, Moderation and Support

## Goal

Add trust and safety systems around the marketplace.

## Scope

Reviews:

- Two-sided review
- Review tags
- Review moderation status

Trust:

- Trust score baseline
- Badge definitions
- Badge eligibility
- Badge award/revoke

Moderation:

- Report user/content/message/media/review
- Moderation case
- Admin moderation queue
- Hide/remove/warn/suspend actions

Dispute:

- Open dispute
- Evidence
- Payout block
- Admin decision
- Refund/release decision integration

Support:

- Support ticket baseline
- AI support assistant placeholder
- Human escalation

## Key APIs

```text
POST /api/v1/jobs/{id}/reviews
GET  /api/v1/profiles/{id}/trust
POST /api/v1/reports
POST /api/v1/disputes
POST /api/v1/disputes/{id}/evidence
POST /api/v1/support/tickets
```

## Events

```text
ReviewRequested
ReviewSubmitted
TrustScoreRecalculated
BadgeAwarded
BadgeRevoked
ReportCreated
ModerationCaseCreated
DisputeOpened
PayoutBlocked
DisputeResolved
RefundIssued
PayoutReleased
SupportEscalationRequested
```

## Quality Gates

- Reviews only after completed jobs.
- One review per side per job.
- Trust score recalculates.
- Badges can be awarded/revoked.
- Reports create moderation cases.
- Disputes block payout.
- Admin actions audited.
- AI does not make final enforcement decisions.

---

# Phase 7 — Search, SEO, AI Assist and Analytics

## Goal

Make Viaverse discoverable, searchable, intelligent and measurable.

## Search Scope

- OpenSearch full provider/business search
- Service request discovery for workers
- Category search
- Feed/work proof search
- Saved searches
- Map/list search

## SEO Scope

- Next.js public pages
- City/category pages
- Provider public profiles
- Business public profiles
- Service guides
- FAQ blocks
- schema.org structured data
- Sitemap/canonical/OpenGraph
- Turkish/English localization

## AI Scope

- Request assistant
- Category suggestion
- Budget guidance
- Natural language search to filters
- Support assistant
- Moderation assist
- SEO content draft assistant

## Analytics Scope

- Event taxonomy
- Funnel tracking
- Marketplace metrics
- Trust metrics
- Business dashboard metrics

## Key APIs

```text
GET  /api/v1/search/providers
GET  /api/v1/search/businesses
GET  /api/v1/search/categories
POST /api/v1/saved-searches
POST /api/v1/ai/request-suggestions
POST /api/v1/ai/search-parse
GET  /api/v1/analytics/business/{id}
```

## Quality Gates

- Private requests not indexed publicly.
- Search documents are projections.
- SEO pages SSR/SSG.
- Structured data valid.
- AI suggestions editable.
- Analytics avoids PII.
- Cost caps for AI/maps/SMS/media considered.

---

# Phase 8 — Admin, Monetization and Launch Readiness

## Goal

Prepare the platform for real operations and launch.

## Admin Scope

- Admin auth/RBAC
- Account management
- Verification queue
- Moderation queue
- Dispute queue
- Payment ops
- Refund/payout ops
- Category admin
- SEO admin
- Subscription admin
- Audit log viewer
- Analytics dashboards

## Monetization Scope

- Business subscription plan
- First month free
- Subscription status
- Commission config
- Boost/sponsored placement baseline
- Business billing views

## Launch Scope

- KVKK/privacy/terms
- App store readiness
- Play Store readiness
- Payment provider production checklist
- WAF/rate limits
- Backup/restore
- Monitoring/alerts
- Support process
- Incident runbooks

## Quality Gates

- Admin actions audited.
- Business subscription configurable.
- No pay-to-quote.
- Boost cannot bypass trust gates.
- Payment/dispute ops require `ADMIN_PAYMENT`.
- Privacy/legal docs reviewed.
- Critical flows tested.
- Launch checklist complete.

---

# Phase 9 — Scale Hardening and Growth

## Goal

Harden the platform for larger traffic, operational reliability, cost control and growth.

## Scope

- Load testing
- Kafka lag monitoring
- Outbox backlog monitoring
- Cassandra query optimization
- OpenSearch tuning
- RDS performance tuning
- Cost dashboards
- WAF tuning
- Incident runbooks
- Backup/restore drills
- Feature flags
- A/B testing framework
- Growth experiments
- Business analytics expansion
- EKS evaluation
- Multi-region strategy later

## Quality Gates

- SLOs defined.
- Dashboards ready.
- Alerts tested.
- Restore tested.
- Runbooks written.
- Cost alerts configured.
- Critical dependencies monitored.
- Security review complete.

---

# 10. First Build Order

Recommended concrete order:

```text
1. Foundation docs + ADRs
2. Repo skeleton
3. Local infra compose
4. Identity service skeleton
5. Account/session/OTP
6. KMP app shell + login UI
7. Next.js web/admin shell
8. Category model
9. Request creation flow
10. Media upload skeleton
11. Worker onboarding
12. Opportunity queue
13. Quote flow
14. Quote comparison UI
15. Chat linked to quote/job
16. Payment session + iyzico sandbox
17. Job lifecycle
18. Delivery approval
19. Review and trust score
20. Business profile + subscription trial
21. OpenSearch search
22. SEO public pages
23. Moderation/report/block
24. Dispute flow
25. Admin console baseline
26. AI assist
27. Analytics
28. Launch hardening
```

---

# 11. What Not To Build First

Do not build first:

- Full ads platform
- Full AI ranking model
- Full E2EE messaging
- Full multi-region infrastructure
- Advanced GraphQL everything
- Too many service categories
- Heavy gamification before real jobs exist
- Complex enterprise analytics before marketplace flow works
- Overly complex Kubernetes setup before team/ops maturity

Rule:

> Defer implementation, not boundaries.

---

# 12. Milestone 1 — Foundation Release

Goal:

A developer can clone the repo, run local infra, boot services, open apps, and understand architecture.

Includes:

- Docs
- ADRs
- Repo skeleton
- Local infra
- CI
- Service templates
- App shells
- Design system skeleton

Success:

- New engineer can onboard through docs and run project.

---

# 13. Milestone 2 — Authenticated App Shell

Goal:

Real account and login flow.

Includes:

- OTP
- OAuth linking skeleton
- Sessions
- Refresh token
- Consent
- Language
- Intent onboarding
- Profile basics
- Protected app shell

Success:

- User logs in and sees app shell with correct account.

---

# 14. Milestone 3 — Marketplace Core

Goal:

User can create request and receive/send offers.

Includes:

- Categories
- Request wizard
- Budget
- Media
- Worker profile
- Opportunity queue
- Quote sending
- Offer comparison

Success:

- Requester publishes request; worker sends offer; requester sees offer.

---

# 15. Milestone 4 — Paid Job Flow

Goal:

Full paid job lifecycle.

Includes:

- Accept quote
- Payment session
- iyzico sandbox
- Job workspace
- Chat
- Delivery approval
- Review

Success:

- End-to-end paid job works in staging.

---

# 16. Milestone 5 — Trust and Operations

Goal:

Platform becomes operationally safe.

Includes:

- Reviews
- Trust score
- Badges
- Reports
- Moderation
- Disputes
- Refund/payout admin
- Audit logs
- Admin console

Success:

- Support/admin can handle real marketplace issues.

---

# 17. Milestone 6 — Public Growth

Goal:

Platform becomes discoverable and measurable.

Includes:

- Search
- SEO pages
- Public profiles
- AI assist
- Analytics
- Business dashboard
- Subscription
- Launch checklist

Success:

- Viaverse can launch a controlled market.

---

# 18. Team Plan

Initial team:

- Founder/Product owner
- Technical lead/architect
- Backend engineer
- KMP mobile engineer
- React/Next.js web/admin engineer
- Product designer
- QA/automation
- DevOps/platform support

Growth squads later:

- Identity & Trust
- Marketplace & Payments
- Client Experience
- Business/Admin/Ops
- Search/AI/Growth
- Platform/SRE
- Data/Analytics
- Trust & Safety Ops

---

# 19. AI/Copilot Task Process

For each task:

1. Create small task prompt.
2. Include relevant docs only.
3. State goal and non-goals.
4. State files likely affected.
5. State architecture/security constraints.
6. Ask for plan.
7. Implement.
8. Run validation.
9. Review.
10. Update docs/progress.

Task template:

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

# 20. Release Readiness Checklist

Before production:

- Critical flows tested.
- Payment provider production checklist complete.
- KVKK/privacy/terms reviewed.
- Admin RBAC configured.
- WAF/rate limits active.
- Backups configured.
- Restore tested.
- Monitoring dashboards ready.
- Alerts configured.
- Incident runbooks written.
- Support escalation process ready.
- SEO public pages validated.
- Cost alerts configured.
- Security scan clean or accepted risks documented.
- App store policies reviewed.
- Play Store policies reviewed.

---

# 21. Roadmap Quality Gates

Every phase must have:

- Clear scope
- Explicit non-goals
- Exit criteria
- Tests
- Docs
- Security/privacy review
- Data ownership clarity
- API/event impact clarity
- Rollback/recovery thinking where relevant

If a phase leaves the product more patched and less coherent, it failed.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
