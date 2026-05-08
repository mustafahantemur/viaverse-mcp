# PAYMENT_MODEL.md — Viaverse Payment Model

This document defines Viaverse payment, wallet, commission, refund, payout, and provider integration rules.

---

## 1. Payment Principles

Viaverse payments must be:

- Safe
- Transparent
- Provider-tokenized
- Idempotent
- Auditable
- Dispute-aware
- Worker-friendly
- Replaceable through provider abstraction

Non-negotiable rule:

> Viaverse never stores raw card PAN or CVV.

---

## 2. Payment Providers

### Turkey Launch

Primary provider:

```text
iyzico
```

### Global Expansion

Adapter included:

```text
Stripe
```

Stripe may remain disabled until global expansion.

### Card Wallet / Stored Card Direction

Adapter-ready:

```text
Masterpass
```

### Mobile Wallets

Supported through provider capabilities:

```text
Apple Pay
Google Pay
```

---

## 3. Provider Abstraction

All payment logic must go through:

```text
PaymentProvider
```

Example interface direction:

```text
PaymentProvider
  createPaymentSession()
  verifyCallback()
  getPaymentStatus()
  requestRefund()
  capturePayment()
  cancelPayment()
  createStoredPaymentMethod()
  deleteStoredPaymentMethod()
```

Adapters:

```text
IyzicoPaymentAdapter
StripePaymentAdapter
MasterpassAdapter
```

Rules:

- Domain does not know provider SDKs.
- Application layer depends on payment port.
- Infrastructure implements provider adapters.
- Provider-specific errors are mapped to standard payment errors.
- Provider callbacks are idempotent.

---

## 4. Payment Data Model

### PaymentSession

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
  updatedAt
```

### StoredPaymentMethod

```text
StoredPaymentMethod
  id
  accountId
  provider
  providerToken
  cardBrand?
  last4?
  expiryMonth?
  expiryYear?
  status
  createdAt
```

Rules:

- `providerToken` is not raw card data.
- Do not store card number.
- Do not store CVV.
- Store only safe display fields such as brand and last4.
- Deleting saved card deletes/disables provider token reference.

### ProviderCallbackRecord

```text
ProviderCallbackRecord
  id
  provider
  providerEventId?
  paymentSessionId?
  rawReferenceId?
  status
  receivedAt
  processedAt?
  idempotencyKey
  safePayloadHash?
```

Rules:

- Store safe references and hashes.
- Do not store provider secrets.
- Avoid storing full sensitive provider payloads if not needed.
- Deduplicate callbacks.

---

## 5. Payment Lifecycle

```text
Quote accepted
-> Job created
-> Payment session created
-> Provider checkout/tokenized payment
-> Provider callback
-> Callback verified
-> Payment authorized/captured
-> Job payment state updated
-> Work performed
-> Delivery approved
-> Commission calculated
-> Payout eligible
-> Payout queued/released
```

Possible statuses:

```text
CREATED
PENDING
AUTHORIZED
FAILED
CAPTURED
CANCELLED
REFUND_REQUESTED
REFUNDED
PARTIALLY_REFUNDED
EXPIRED
```

---

## 6. Quote Acceptance Payment Flow

```text
Requester accepts quote
-> marketplace-service validates request/quote ownership and state
-> marketplace-service creates or transitions job
-> payment-service creates PaymentSession
-> PaymentProvider creates provider session
-> client opens provider checkout/payment flow
-> provider callback received
-> payment-service verifies and updates state
-> PaymentAuthorized event emitted
-> marketplace-service updates job state
-> notification-service informs participants
```

Rules:

- Quote must not be expired.
- Requester must own request.
- Worker/business must be eligible.
- Amount/currency must match quote.
- Payment session creation should be idempotent where double-click is possible.

---

## 7. Saved Card / Wallet Model

Viaverse can show saved cards, but only through provider tokenization.

Wallet UI may show:

- Card brand
- Last 4 digits
- Expiration month/year
- Default method marker
- Provider label if needed

Wallet must not show/store:

- Full card number
- CVV
- Raw provider secrets

User actions:

- Add card
- Remove card
- Set default
- Pay with saved card
- View payment history

---

## 8. Commission Model

Initial commission:

```text
5% success commission after completed paid work
```

Rules:

- Commission is transparent before payment.
- Worker/business sees net expected amount.
- Commission applies only after successful completed work.
- Refund/dispute may reverse or adjust commission.
- Commission rate can become category/market-specific later.
- Commission config must be admin-controlled and audited.

Example:

```text
Job amount: 2,000 TL
Commission: 5% = 100 TL
Worker/business gross: 2,000 TL
Worker/business net before provider fees/taxes: 1,900 TL
```

Exact provider fees/tax handling must be finalized with accounting/legal review.

---

## 9. Business Subscription Payment

Initial plan:

```text
Business subscription: 500 TL/month
First month free
Admin-configurable
```

Subscription rules:

- Business subscription improves tools/visibility.
- Basic business access remains available.
- Subscription must never become pay-to-quote.
- Subscription status visible in Business Mode.
- Failed subscription payment should not immediately destroy business data.
- Subscription changes audited.

Possible statuses:

```text
TRIALING
ACTIVE
PAST_DUE
CANCELLED
EXPIRED
SUSPENDED
```

---

## 10. Boost / Sponsored Payment

Future monetization:

- Boosted business/provider visibility
- Sponsored feed cards
- Category sponsorship
- Local campaign cards

Rules:

- Sponsored content clearly labeled.
- Boost cannot bypass trust/safety/risk/relevance gates.
- No forced video ads.
- No blocking interstitials.
- No watch-to-continue.
- No ads in payment/safety/dispute-critical flows.

---

## 11. Payout Model

Payout becomes eligible after:

- Job completed
- Delivery approved
- Payment captured/settled
- No active dispute
- Worker/business payout account valid
- Risk rules allow payout

Payout statuses:

```text
NOT_ELIGIBLE
ELIGIBLE
BLOCKED
QUEUED
PROCESSING
RELEASED
FAILED
CANCELLED
```

Rules:

- Payout blocked during dispute.
- Admin release requires `ADMIN_PAYMENT`.
- Manual payout action audited.
- Payout failure creates notification/support path.
- Payout account changes may require verification/step-up.

---

## 12. Refund Model

Refund may happen due to:

- Cancellation
- Failed service
- Dispute decision
- Admin decision
- Provider correction

Refund statuses:

```text
REQUESTED
UNDER_REVIEW
APPROVED
REJECTED
PROCESSING
ISSUED
FAILED
```

Rules:

- Refund request links to job/payment/dispute.
- Refund decision requires reason.
- Refund action audited.
- Provider refund callback idempotent.
- Trust/risk signals may update after refund.

---

## 13. Dispute and Payment Hold

When dispute opens:

```text
DisputeOpened
-> payment-service blocks payout
-> payment state references hold
-> support/admin reviews evidence
-> decision: refund, release, split, redo, close
```

Rules:

- Payout blocked automatically.
- Participants can see clear status.
- Admin decision requires reason.
- Payment action requires `ADMIN_PAYMENT`.
- Dispute resolution updates trust/risk signals.

---

## 14. Idempotency

Required for:

- Create payment session
- Provider callback
- Capture
- Refund
- Payout release
- Subscription webhook
- Boost payment
- Admin payment actions

Idempotency key sources:

- Client `Idempotency-Key`
- Provider event ID
- Payment session ID + action
- Admin action ID

Rules:

- Duplicate callback must not duplicate payment state.
- Duplicate refund request must not issue double refund.
- Duplicate payout release must not release twice.

---

## 15. Provider Callback Verification

Callback handler must:

1. Validate provider signature if available.
2. Validate provider event/session reference.
3. Validate amount and currency.
4. Validate expected state transition.
5. Deduplicate callback.
6. Store callback record.
7. Update payment state.
8. Publish event.
9. Return safe provider response.

Do not:

- Trust callback blindly.
- Update job state directly from controller.
- Store full sensitive payload unnecessarily.
- Log secrets or card data.

---

## 16. Payment Events

Events:

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
SubscriptionActivated
SubscriptionCancelled
SubscriptionPaymentFailed
```

Rules:

- Events contain safe references only.
- No card data.
- No provider secrets.
- Consumers idempotent.

---

## 17. Payment Admin Operations

Admin views:

- Payment sessions
- Provider callback records
- Refunds
- Payouts
- Commission ledger
- Disputes
- Subscription payments
- Reconciliation status

Admin actions:

- Approve refund
- Reject refund
- Retry failed refund
- Block payout
- Release payout
- Reconcile payment
- Mark provider mismatch for investigation

Rules:

- Requires `ADMIN_PAYMENT`.
- Reason required.
- Audit log required.
- Step-up auth required for high-risk actions.
- No raw card data visible.

---

## 18. Payment UX Requirements

Payment screens must show:

- Amount
- Currency
- Service fee/commission if user-facing
- Provider trust explanation
- Saved card display fields
- Payment status
- Failure reason in user-friendly language
- Refund/dispute path where applicable

Payment copy must communicate:

> Viaverse does not store your raw card data. Payments are processed securely through payment providers.

Do not show:

- Ads
- Sponsored distractions
- Unclear hidden fees
- Technical provider errors

---

## 19. Accounting and Legal Review Needed

Before production:

- Tax handling
- Invoice/receipt rules
- Marketplace facilitator obligations
- Worker/business payout legal requirements
- Refund policies
- Commission tax treatment
- Subscription tax treatment
- Provider contracts
- KVKK/payment data handling review

This document defines product/technical direction, not legal advice.

---

## 20. Payment Quality Gates

A payment feature is complete only when:

- No raw card data stored.
- Provider integration behind adapter.
- Idempotency implemented.
- Callback verified.
- Payment state transition tested.
- Failure path tested.
- Refund path considered.
- Payout block during dispute implemented where relevant.
- Admin action audited.
- Sensitive data not logged.
- User-facing status clear.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
