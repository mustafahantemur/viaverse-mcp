# Event Contracts

Event schemas should be stored here.

## Event Naming Rule

Use business facts in past tense:

```text
AccountCreated
CapabilityGranted
RequestCreated
OfferSubmitted
OfferAccepted
JobCreated
JobCompleted
PaymentCaptured
BusinessPublished
MessageSent
TaxonomyPublished
```

Avoid vague names:

```text
UpdateEvent
ProcessData
HandleThing
```

## Required Event Fields

```text
eventId
eventType
eventVersion
aggregateType
aggregateId
occurredAt
producer
payload
```

## Consumer Rules

Consumers must be idempotent, retry-safe, observable and schema-version-aware.
