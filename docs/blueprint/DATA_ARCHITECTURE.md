# DATA_ARCHITECTURE.md — Viaverse Data Architecture

This document defines how Viaverse owns, stores, projects, indexes, retains, protects, and moves data.

---

## 1. Data Architecture Goal

Viaverse data architecture must support:

- Transactional consistency for core business flows
- High-volume feeds and notifications
- Search and discovery
- Smart matching
- Safe payments
- Trust/reputation
- Auditability
- Privacy and KVKK/GDPR-style rights
- Future analytics
- Service ownership boundaries

Principle:

> PostgreSQL is source of truth. Cassandra and OpenSearch are projections/read models. Services own their data.

---

## 2. Data Ownership Matrix

```text
identity-service
  -> Account
  -> Capability
  -> LinkedIdentity
  -> Session
  -> RefreshTokenFamily
  -> ConsentRecord

marketplace-service
  -> ServiceRequest
  -> Quote
  -> QuoteRevision
  -> Job
  -> DeliveryApproval
  -> Cancellation
  -> Dispute entry

payment-service
  -> PaymentSession
  -> PaymentAuthorization
  -> PaymentCapture
  -> RefundRequest
  -> PayoutInstruction
  -> CommissionLedger
  -> ProviderCallbackRecord
  -> IdempotencyRecord

messaging-service
  -> Conversation
  -> Message
  -> AttachmentReference
  -> MessageReport
  -> ConversationPolicyState

media-service
  -> MediaAsset
  -> UploadSession
  -> ObjectStorageKey
  -> Thumbnail
  -> TranscodeJob
  -> MediaModerationStatus

notification-service
  -> NotificationPreference
  -> NotificationInboxItem
  -> Template
  -> PushToken
  -> DeliveryAttempt

search-service
  -> SearchIndexDocument
  -> SavedSearch
  -> SearchQueryLog

trust-gamification-service
  -> Review
  -> ReviewAggregate
  -> TrustScore
  -> BadgeDefinition
  -> AwardedBadge
  -> LocalityScore
  -> GamificationActivityLog

ads-monetization-service
  -> SubscriptionPlan
  -> BusinessSubscription
  -> FreeTrial
  -> BoostCampaign
  -> SponsoredPlacement
  -> AdImpression
  -> AdClick
```

Rule:

> A service may cache or project data from another service, but it does not own or mutate another service's source-of-truth data.

---

## 3. Database Roles

### 3.1 PostgreSQL

Role:

- Transactional source of truth

Use for:

- Accounts
- Capabilities
- Auth/session state
- Consents
- Requests
- Quotes
- Jobs
- Payments
- Refunds
- Payouts
- Business profiles
- Subscriptions
- Moderation cases
- Admin configs
- Badge definitions

Rules:

- Flyway migrations
- Hibernate validate
- Service-owned schemas/databases
- No cross-service joins
- Transactions at use-case level
- Strong constraints for critical data

### 3.2 Cassandra

Role:

- High-volume read models and append-heavy projections

Use for:

- Feed timelines
- Notification inbox
- Activity streams
- Gamification activity log
- Leaderboards
- Counters
- High-volume read projections

Do not use for:

- Account source of truth
- Payment source of truth
- Quote/job lifecycle source of truth
- Authorization source of truth

Cassandra design rule:

> Model tables by query pattern, not by normalization.

### 3.3 OpenSearch

Role:

- Search, discovery, geo search and ranking candidate retrieval

Use for:

- Provider search
- Business search
- Category search
- Service request discovery
- Feed search
- Saved search matching
- Geo search
- SEO/public discovery projections

Rules:

- Search docs are projections.
- Indexes updated via events.
- Private requests are not indexed publicly.
- Search result authorization must still be respected.

### 3.4 Valkey / Redis

Role:

- Short-lived state, cache, rate limiting

Use for:

- OTP challenge throttle
- Login rate limits
- API rate limits
- Frequency caps
- Session revocation cache
- Idempotency hot cache
- Locks
- Hot availability
- Notification dedupe

Rules:

- Not source of truth for critical long-term data.
- Cache misses must be recoverable.
- TTLs must be explicit.

### 3.5 S3

Role:

- Object/media storage

Use for:

- Request media
- Chat attachments
- Work proof
- Business portfolio
- Profile images
- Thumbnails
- Videos
- Data exports

Rules:

- Upload through presigned URLs.
- Access controlled through media-service.
- Public media goes through CloudFront.
- Private media requires signed/authorized access.
- EXIF stripping and moderation required.

---

## 4. Core Entities

### 4.1 Account

```text
Account
  id: UUID
  status: ACTIVE | SUSPENDED | DELETED_PENDING | DELETED
  displayName
  avatarMediaId?
  primaryEmail?
  primaryPhone?
  locale
  country
  riskLevel
  tokenVersion
  createdAt
  updatedAt
  deletedAt?
```

Rules:

- Account ID is JWT subject.
- Account is not equal to customer/provider.
- Account can have multiple capabilities.

### 4.2 AccountCapability

```text
AccountCapability
  accountId
  capability
  status
  reason?
  enabledAt
  suspendedAt?
```

Capabilities:

```text
REQUEST_WORK
DO_WORK_INDIVIDUALLY
OPERATE_BUSINESS
SOCIAL_PARTICIPATION
ADMIN_SUPPORT
ADMIN_VERIFICATION
ADMIN_PAYMENT
ADMIN_MODERATION
ADMIN_CATEGORY
ADMIN_ANALYTICS
ADMIN_SUPER
```

### 4.3 ServiceRequest

```text
ServiceRequest
  id
  requesterAccountId
  categoryId
  title
  description
  budgetMode
  budgetMin?
  budgetMax?
  currency
  locationApproximation
  urgency
  preferredTime
  status
  createdAt
  publishedAt?
```

### 4.4 Quote

```text
Quote
  id
  requestId
  workerAccountId?
  businessId?
  price
  currency
  estimatedStart?
  estimatedDuration?
  message
  status
  expiresAt
  createdAt
```

### 4.5 Job

```text
Job
  id
  requestId
  acceptedQuoteId
  requesterAccountId
  workerAccountId?
  businessId?
  paymentSessionId?
  status
  scheduledAt?
  startedAt?
  deliveredAt?
  completedAt?
```

### 4.6 PaymentSession

```text
PaymentSession
  id
  jobId
  requesterAccountId
  workerAccountId?
  businessId?
  provider
  providerSessionId
  amount
  currency
  commissionAmount
  status
  idempotencyKey
  createdAt
```

### 4.7 Review

```text
Review
  id
  jobId
  reviewerAccountId
  revieweeAccountId?
  revieweeBusinessId?
  role
  rating
  text
  tags
  status
  createdAt
```

### 4.8 TrustScore

```text
TrustScore
  subjectType: ACCOUNT | BUSINESS
  subjectId
  score
  components
  updatedAt
```

### 4.9 MediaAsset

```text
MediaAsset
  id
  ownerAccountId
  contextType
  contextId
  objectKey
  thumbnailKey?
  mediaType
  visibility
  moderationStatus
  createdAt
```

---

## 5. Status Models

### 5.1 Service Request Status

```text
DRAFT
PUBLISHED
COLLECTING_OFFERS
QUOTE_SELECTED
CANCELLED
EXPIRED
CONVERTED_TO_JOB
```

### 5.2 Quote Status

```text
DRAFT
SENT
QUESTIONED
REVISION_REQUESTED
REVISED
ACCEPTED
REJECTED
WITHDRAWN
EXPIRED
```

### 5.3 Job Status

```text
PAYMENT_PENDING
PAYMENT_AUTHORIZED
SCHEDULED
IN_PROGRESS
DELIVERED
APPROVED
COMPLETED
CANCELLED
DISPUTED
REFUNDED
```

### 5.4 Payment Status

```text
CREATED
AUTHORIZED
FAILED
CAPTURED
REFUND_REQUESTED
REFUNDED
PARTIALLY_REFUNDED
CANCELLED
```

### 5.5 Media Moderation Status

```text
PENDING
APPROVED
REJECTED
QUARANTINED
NEEDS_REVIEW
```

---

## 6. Data Flow Patterns

### 6.1 Transaction + Outbox

Use for critical domain events.

```text
Use case starts transaction
-> state changes in PostgreSQL
-> outbox event inserted
-> transaction commits
-> outbox publisher publishes event
-> consumers update projections
```

### 6.2 Projection

Used for:

- Search indexes
- Feed read models
- Notification inbox
- Trust aggregates
- Analytics

Projection rule:

> Projections can be rebuilt from source-of-truth data and events where practical.

### 6.3 Cache

Used for performance only.

Cache rule:

> Cache is disposable. Source-of-truth must survive cache loss.

### 6.4 Search Index

Used for discovery.

Search rule:

> Search results are projections. Sensitive visibility rules still apply.

---

## 7. Cassandra Table Direction

Exact schemas will be defined per query pattern.

Candidate tables:

```text
feed_by_account
feed_by_location_category
notifications_by_account
activity_by_subject
badge_activity_by_subject
leaderboard_by_location_category_period
worker_opportunities_by_account
message_inbox_by_account
```

Partition key rules:

- Avoid hot partitions.
- Query by partition key.
- Include time bucketing where needed.
- Define TTL where appropriate.
- Avoid unbounded partitions.

---

## 8. OpenSearch Index Direction

Candidate indexes:

```text
providers_v1
businesses_v1
service_requests_v1
feed_posts_v1
categories_v1
public_profiles_v1
seo_pages_v1
```

Index document rule:

- Include only fields needed for search/ranking/display.
- Do not include private exact address.
- Do not include secrets or payment data.
- Keep document version.

---

## 9. Data Classification

```text
Public
  - Public category pages
  - Approved public business profiles
  - Approved public provider profiles
  - Public work proof

Internal
  - Operational metadata
  - Search analytics
  - Admin notes
  - Non-sensitive logs

Confidential
  - Account contact info
  - Private messages
  - Payment provider tokens
  - Verification documents
  - Dispute evidence

Highly Sensitive
  - JWT signing keys
  - OAuth secrets
  - Payment provider secrets
  - Database credentials
  - SMS/email credentials
```

Rules:

- Highly sensitive data lives in Secrets Manager.
- Confidential data access must be authorized.
- Public data must be explicitly public.
- Logs/events must avoid confidential/highly sensitive payloads.

---

## 10. Privacy Data Flows

User rights:

- View privacy settings
- View consent status
- Revoke optional consent
- Download my data
- Delete account
- Manage marketing preferences
- Manage blocked users
- Manage public profile visibility

Deletion:

- Delete or anonymize personal data where possible.
- Retain payment/financial records as legally required.
- Remove/anonymize public profile data.
- Remove from search indexes/projections.
- Preserve audit records where legally required.

---

## 11. Retention Direction

Initial retention direction:

- OTP challenges: short retention
- Sessions: active plus security audit period
- Payment records: legally required retention
- Dispute records: safety/compliance retention
- Moderation reports: safety/compliance retention
- Private messages: policy to be finalized with legal/privacy review
- Public posts: until deletion/moderation policy applies
- Deleted account data: anonymized where possible

Legal review required before production.

---

## 12. Backup and Recovery

Requirements:

- PostgreSQL backups
- Point-in-time recovery where possible
- S3 versioning/lifecycle strategy
- OpenSearch index rebuild plan
- Cassandra backup/snapshot strategy
- Disaster recovery runbooks
- Restore drills

Recovery rule:

> A backup that has never been restored is not trusted.

---

## 13. Data Quality Rules

- Use IDs consistently.
- Use UTC timestamps.
- Store currency explicitly.
- Do not use floating point for money.
- Use decimal/money-safe types.
- Normalize phone/email before hashing/comparison.
- Keep audit timestamps.
- Prefer explicit statuses over boolean flags.
- Use optimistic locking for state transitions where needed.

---

## 14. Data Architecture Quality Gates

Before adding a new entity/table/index:

- Who owns it?
- Is it source of truth or projection?
- Does it contain personal data?
- Does it require retention policy?
- Does it require audit logging?
- Does it require deletion/anonymization?
- Does it need search indexing?
- Does it need event publication?
- Does it need idempotency?
- Does it affect payments/trust/moderation?


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
