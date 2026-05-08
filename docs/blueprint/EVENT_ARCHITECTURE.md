# EVENT_ARCHITECTURE.md — Viaverse Event Architecture

This document defines the event-driven architecture for Viaverse.

---

## 1. Event Architecture Goal

Events allow Viaverse services to remain decoupled while propagating business facts to search, notifications, trust scoring, feed projections, analytics, moderation and admin operations.

Principle:

> Events are immutable facts about things that already happened.

---

## 2. Broker Decision

Viaverse uses:

```text
Apache Kafka / Amazon MSK
```

Application abstraction:

```text
Spring Cloud Stream Kafka Binder
```

Rules:

- No direct Kafka clients in use-case/application code.
- No Redpanda.
- No RabbitMQ at launch.
- Use Spring Cloud Stream bindings.
- Use outbox for critical state changes.

---

## 3. Event Publishing Pattern

Critical domain events use the outbox pattern.

```text
Use case transaction
  -> update PostgreSQL state
  -> insert OutboxEvent in same transaction
  -> commit

Outbox publisher
  -> reads unpublished events
  -> publishes through Spring Cloud Stream
  -> marks as published

Kafka
  -> services consume events
  -> projections/search/notifications/trust/analytics update
```

Why:

- Avoids state committed without event
- Avoids event published without state
- Supports retries
- Supports audit/debugging

---

## 4. Event Envelope

All events should use a common envelope.

```json
{
  "eventId": "uuid",
  "eventType": "ServiceRequestPublished",
  "eventVersion": 1,
  "occurredAt": "2026-01-01T12:00:00Z",
  "producer": "marketplace-service",
  "aggregateType": "ServiceRequest",
  "aggregateId": "uuid",
  "correlationId": "uuid",
  "causationId": "uuid",
  "payload": {}
}
```

Required fields:

- eventId
- eventType
- eventVersion
- occurredAt
- producer
- aggregateType
- aggregateId
- correlationId
- causationId
- payload

---

## 5. Event Naming

Use past tense.

Good:

```text
AccountCreated
ServiceRequestPublished
QuoteSent
QuoteAccepted
PaymentAuthorized
WorkDelivered
ReviewSubmitted
BadgeAwarded
```

Bad:

```text
CreateAccount
PublishRequest
SendQuote
DoPayment
ProcessReview
```

---

## 6. Event Versioning

Rules:

- Every event has `eventVersion`.
- Never break existing consumers casually.
- Prefer additive changes.
- Keep old fields until all consumers migrate.
- Major incompatible changes require new version or new event type.
- Document event schemas.

Suggested location:

```text
packages/api-contracts/events/
```

or:

```text
docs/events/
```

---

## 7. Sensitive Data Rules

Do not include in events:

- Raw card data
- CVV/PAN
- Tokens
- Provider secrets
- OTP codes
- Raw private messages
- Verification document images
- Exact private address unless strictly required and protected
- Large media payloads

Use references:

```text
mediaId
paymentSessionId
conversationId
jobId
accountId
businessId
```

---

## 8. Topic Direction

Exact topic naming can be finalized during implementation.

Suggested topic style:

```text
viaverse.identity.events
viaverse.marketplace.events
viaverse.payment.events
viaverse.messaging.events
viaverse.media.events
viaverse.notification.events
viaverse.search.events
viaverse.trust.events
viaverse.ads.events
viaverse.admin.audit.events
```

Alternative:

```text
<environment>.viaverse.<context>.events
```

Example:

```text
prod.viaverse.marketplace.events
```

Rules:

- Keep naming consistent.
- Avoid too many tiny topics too early.
- Use DLQ topics.
- Use consumer groups per service.

---

## 9. Core Event Catalog

### 9.1 Identity Events

```text
AccountCreated
AccountUpdated
AccountSuspended
AccountDeletionRequested
AccountAnonymized
AccountCapabilityEnabled
AccountCapabilitySuspended
LinkedIdentityAdded
LinkedIdentityRemoved
OtpRequested
OtpVerified
SessionCreated
SessionRevoked
ConsentGranted
ConsentRevoked
```

### 9.2 Marketplace Events

```text
ServiceRequestDraftCreated
ServiceRequestPublished
ServiceRequestUpdated
ServiceRequestCancelled
MatchingRequested
QuoteDraftCreated
QuoteSent
QuoteViewed
QuoteQuestionAsked
QuoteRevisionRequested
QuoteRevised
QuoteAccepted
QuoteRejected
QuoteWithdrawn
QuoteExpired
JobCreated
JobScheduled
WorkStarted
WorkDelivered
DeliveryApproved
DeliveryRejected
JobCompleted
JobCancelled
DisputeOpened
```

### 9.3 Payment Events

```text
PaymentSessionCreated
PaymentAuthorized
PaymentFailed
PaymentCaptured
PaymentCallbackReceived
RefundRequested
RefundApproved
RefundRejected
RefundIssued
PayoutEligible
PayoutBlocked
PayoutQueued
PayoutReleased
CommissionCalculated
PaymentReconciled
```

### 9.4 Messaging Events

```text
ConversationCreated
MessageSent
MessageRead
MessageReported
ContactSharingDetected
SupportEscalationRequested
```

### 9.5 Media Events

```text
MediaUploadRequested
MediaUploaded
MediaModerationRequested
MediaApproved
MediaRejected
MediaQuarantined
ThumbnailGenerated
VideoTranscoded
```

### 9.6 Notification Events

```text
NotificationCreated
NotificationDelivered
NotificationRead
NotificationSuppressedByPreference
PushTokenRegistered
```

### 9.7 Trust Events

```text
ReviewRequested
ReviewSubmitted
BothReviewsCompleted
TrustScoreRecalculated
BadgeEligibilityChanged
BadgeAwarded
BadgeRevoked
LocalityScoreUpdated
RiskSignalCreated
RiskScoreUpdated
```

### 9.8 Ads / Monetization Events

```text
BusinessSubscriptionTrialStarted
SubscriptionActivated
SubscriptionCancelled
SubscriptionPaymentFailed
BoostCampaignCreated
BoostCampaignActivated
SponsoredImpressionRecorded
SponsoredClickRecorded
```

### 9.9 Admin / Audit Events

```text
AdminViewedSensitiveProfile
AdminChangedVerificationStatus
AdminIssuedRefund
AdminReleasedPayout
AdminModeratedContent
AdminSuspendedCapability
AdminChangedCategory
AdminChangedMonetizationConfig
AdminResolvedDispute
```

---

## 10. Event Consumers

### search-service

Consumes:

- ServiceRequestPublished
- ProviderProfileUpdated
- BusinessProfileUpdated
- ReviewSubmitted
- WorkProofPublished
- CategoryUpdated

Updates:

- OpenSearch indexes

### notification-service

Consumes:

- QuoteSent
- QuoteAccepted
- PaymentAuthorized
- WorkDelivered
- DeliveryApproved
- ReviewRequested
- DisputeOpened
- SupportEscalationRequested

Creates:

- In-app notifications
- Push notifications
- Email/SMS where allowed

### trust-gamification-service

Consumes:

- JobCompleted
- ReviewSubmitted
- DeliveryApproved
- WorkDelivered
- ReportValidated
- PaymentAuthorized/Completed where relevant

Updates:

- Trust score
- Badge eligibility
- Gamification activity

### ads-monetization-service

Consumes:

- BusinessProfilePublished
- SubscriptionActivated
- BoostCampaignCreated
- SponsoredImpressionRecorded

Updates:

- Monetization state
- Campaign analytics

### analytics-service later

Consumes most business events.

Rules:

- No raw PII.
- Analytics taxonomy versioned.
- Consent respected.

---

## 11. Idempotency

Every consumer must handle duplicate events.

Methods:

- Store processed event IDs
- Use unique constraints
- Use idempotent upserts
- Use deterministic projection keys

Example:

```text
processed_events
  event_id
  consumer_name
  processed_at
```

Rules:

- Never assume exactly-once delivery.
- Event replay should not corrupt state.
- Payment events require extra care.

---

## 12. Ordering

Kafka provides ordering only within a partition.

Partition key should usually be aggregate ID:

- accountId for identity events
- requestId for service request events
- jobId for job/payment workflow events
- conversationId for messages
- mediaId for media events
- businessId for business subscription events

Rules:

- Do not rely on global ordering.
- Handle out-of-order events where possible.
- Use version/state checks.
- Consumers should ignore stale events.

---

## 13. Retry and DLQ

Rules:

- Use retry with backoff.
- Use DLQ for poison events.
- Alert on DLQ growth.
- Alert on Kafka lag.
- Include error reason.
- Provide replay tooling.

DLQ examples:

```text
viaverse.marketplace.events.dlq
viaverse.payment.events.dlq
```

---

## 14. Correlation and Causation

Every event must include:

- correlationId
- causationId

Example:

```text
User publishes request
correlationId = request HTTP correlation ID
ServiceRequestPublished.causationId = command ID
MatchingRequested.causationId = ServiceRequestPublished.eventId
NotificationCreated.causationId = QuoteSent.eventId
```

This allows tracing full business flows.

---

## 15. Outbox Table

Suggested structure:

```text
outbox_event
  id UUID primary key
  aggregate_type varchar
  aggregate_id UUID
  event_type varchar
  event_version int
  payload jsonb
  status varchar
  created_at timestamp
  published_at timestamp null
  retry_count int
  last_error text null
  correlation_id UUID
  causation_id UUID
```

Statuses:

```text
PENDING
PUBLISHED
FAILED
DEAD
```

---

## 16. Event Schema Documentation

Every event should document:

- Event name
- Producer
- Consumers
- Trigger
- Payload
- Version
- Sensitivity
- Ordering key
- Idempotency key
- Example JSON

Suggested file:

```text
docs/events/<event-name>.md
```

---

## 17. Event Quality Gates

Before adding an event:

- Is it a fact that already happened?
- Who produces it?
- Who consumes it?
- Is it necessary?
- Does it contain sensitive data?
- What is the aggregate ID?
- What is the partition key?
- Is it versioned?
- Are consumers idempotent?
- Is DLQ configured?
- Is the event documented?

---

## 18. Example Event: ServiceRequestPublished

```json
{
  "eventId": "uuid",
  "eventType": "ServiceRequestPublished",
  "eventVersion": 1,
  "occurredAt": "2026-01-01T12:00:00Z",
  "producer": "marketplace-service",
  "aggregateType": "ServiceRequest",
  "aggregateId": "request-id",
  "correlationId": "request-correlation-id",
  "causationId": "publish-command-id",
  "payload": {
    "requestId": "request-id",
    "requesterAccountId": "account-id",
    "categoryId": "category-id",
    "budgetMode": "RANGE",
    "budgetMin": 1000,
    "budgetMax": 2000,
    "currency": "TRY",
    "locationAreaId": "area-id",
    "urgency": "NORMAL",
    "publishedAt": "2026-01-01T12:00:00Z"
  }
}
```

Consumers:

- search-service
- notification-service
- matching consumer
- analytics later

---

## 19. Example Event: PaymentAuthorized

```json
{
  "eventId": "uuid",
  "eventType": "PaymentAuthorized",
  "eventVersion": 1,
  "occurredAt": "2026-01-01T12:05:00Z",
  "producer": "payment-service",
  "aggregateType": "PaymentSession",
  "aggregateId": "payment-session-id",
  "correlationId": "correlation-id",
  "causationId": "provider-callback-id",
  "payload": {
    "paymentSessionId": "payment-session-id",
    "jobId": "job-id",
    "amount": "1500.00",
    "currency": "TRY",
    "provider": "IYZICO"
  }
}
```

Do not include:

- Card number
- CVV
- Provider secret
- Raw provider callback payload

---

## 20. Event Architecture Quality Gates

- Event published through outbox.
- Event schema documented.
- Event does not leak sensitive data.
- Consumers idempotent.
- DLQ configured.
- Correlation/causation IDs present.
- Event version present.
- Payload contains references, not large private data.
- Tests cover producer and consumer.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
