# Viaverse Platform

Viaverse is a trust-first local services and work network.

It combines a marketplace, social trust layer, safe payments, smart matching, business tooling, SEO-ready public discovery, AI-assisted flows, and scalable AWS-native infrastructure.

This repository is a rebuild from zero. The greenfield implementation/prototype is learning material only.

---

## 1. Product Summary

Viaverse enables one real account to activate multiple capabilities:

```text
Account
  ├── Request Work
  ├── Do Work Individually
  ├── Operate Business
  ├── Social Participation
  └── Admin capabilities, internal only
```

A user can:

- Request work
- Receive and compare offers
- Pay safely
- Track jobs
- Review both sides
- Do work individually
- Operate a business profile
- Share work proof
- Participate in local discovery
- Build trust through ratings and badges

Viaverse is marketplace-first, with a social trust layer.

---

## 2. Core Principles

- One account, multiple capabilities
- Marketplace-first
- Social trust layer second
- Worker-friendly monetization
- No pay-to-quote
- No lead-credit burn
- Safe payments
- Two-sided reviews
- Smart matching
- Explainable recommendations
- AI assists, humans decide
- Vendor abstractions for external providers
- AWS-native infrastructure
- Production-grade architecture from day one

---

## 3. Main Product Modes

### Personal Mode

For users who want to request work, explore services, post locally, pay, review, and manage their profile.

### Work Mode

For individuals who want to earn money without creating a formal business profile.

Personal Mode and Work Mode should feel like a lightweight capability transition, similar to an Airbnb-style “become a host” path:

- The user keeps the same Account and login.
- The user enables individual work capability through onboarding.
- The user can move between requesting work and doing individual work without creating a separate identity.
- Eligibility, verification, category, service area and safety gates are still enforced by backend policy.

Includes:

- Worker onboarding
- Categories
- Service area
- Availability
- Matched requests
- Free offer sending
- Active jobs
- Earnings
- Reviews
- Badges

### Business Mode

For formal businesses or teams.

Business Mode is not just another lightweight personal switch. It is an additional business workspace attached to the same Account, with a distinct BusinessAccount/profile, merchant onboarding, verification, staff/team roles, catalog, subscription and publish-ready policy.

Rules:

- The same person may request work, do individual work and operate a business, but these are separate capabilities.
- BusinessAccount is not the same thing as Account.
- Business setup follows a different flow from individual Work Mode.
- Business visibility, lead routing and public profile publishing require business-specific gates.

Includes:

- Business profile
- Verification
- Services
- Packages
- Operating hours
- Team members
- Portfolio
- Reviews
- Badges
- Subscription
- Analytics
- Promotions/boost later

### Admin Mode

Separate React/Next.js admin console for:

- Verification
- Support
- Moderation
- Disputes
- Payments
- Refunds
- Categories
- SEO
- Subscriptions
- Analytics
- Audit logs

---

## 4. High-Level Architecture

```text
Clients
  ├── KMP Compose Mobile App
  ├── Next.js Web / SEO
  └── Next.js Admin

Entry
  ├── API Gateway / App BFF
  ├── REST APIs
  └── GraphQL BFF for complex read aggregation where useful

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

Data
  ├── PostgreSQL
  ├── Cassandra
  ├── OpenSearch
  ├── Valkey / Redis
  └── S3

Async
  └── Kafka / Amazon MSK via Spring Cloud Stream

Cloud
  └── AWS-native
```

---

## 5. Technology Stack

### Client

- Kotlin Multiplatform
- Compose Multiplatform
- Ktor Client
- SQLDelight
- Koin
- Coil 3
- React + TypeScript + Next.js for web/admin/SEO

### Backend

- Java
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA + Hibernate
- Flyway
- Spring Cloud Stream Kafka Binder
- Spring gRPC
- Resilience4j
- ProblemDetail + `@RestControllerAdvice`

### Data and Infra

- PostgreSQL
- Cassandra
- OpenSearch
- Valkey / Redis
- S3
- CloudFront
- AWS WAF
- AWS Shield
- Route 53
- ECS Fargate first
- EKS later if justified
- Amazon MSK
- Amazon SES
- Netgsm
- Google Maps first provider
- iyzico primary payment provider
- Stripe adapter included but may remain disabled

---

## 6. Repository Structure

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

The actual implementation may start smaller, but the boundaries must be designed from day one.

---

## 7. Backend Service Structure

Each service should follow:

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

- Domain is pure business model.
- JPA entities are infrastructure.
- Controllers are thin.
- Use cases orchestrate.
- Provider integrations are adapters.
- No cross-service database joins.

---

## 8. Local Development

Planned local dependencies:

- PostgreSQL
- Cassandra
- Kafka
- OpenSearch
- Valkey / Redis
- Mailpit
- LocalStack optional for S3-like testing
- Mock payment provider / iyzico sandbox adapter

Example future command:

```bash
docker compose -f infra/docker-compose/docker-compose.local.yml up -d
```

Exact commands will be finalized when the repo skeleton is created.

---

## 9. Validation

Expected validation groups:

```bash
# Backend
./mvnw clean verify

# Web/admin
npm run typecheck
npm run lint
npm run build

# KMP
./gradlew check

# Full verification
npm run verify:all
```

These may be adjusted based on final monorepo tooling.

---

## 10. Documentation Map

Important docs:

- `AGENTS.md`
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

Architecture decisions must be recorded under:

```text
docs/decisions/
```

---

## 11. Non-Negotiable Rules

- No Firebase core backend
- No Redpanda
- No RabbitMQ at launch
- No pay-to-quote
- No lead-credit burn
- No raw card storage
- No random JWT subject
- No generic string database query parser
- No JPA entities in domain
- No controller business logic
- No global god API file
- No direct cross-service DB joins
- No Hibernate `ddl-auto=update` in production
- No sensitive data in logs/events
- No admin sensitive action without audit log

---

## 12. Roadmap Summary

```text
0. Foundation docs and ADRs
1. Repository/platform skeleton
2. Identity, onboarding, security base
3. Request creation and matching core
4. Worker and business capability
5. Quote, chat, payment, job lifecycle
6. Reviews, trust, moderation, support
7. Search, SEO, AI assist, analytics
8. Admin, monetization, launch readiness
9. Scale hardening and growth
```

---

## 13. Current Status

Planning and documentation phase.

The next step is to create the full repository documentation set and then generate the initial repo skeleton.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
