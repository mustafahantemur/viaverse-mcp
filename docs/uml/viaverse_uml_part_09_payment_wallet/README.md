# Viaverse UML Part 09 — Payment / Wallet Flow

Bu part quote acceptance sonrasında ödeme, kayıtlı kart, provider callback, commission, refund ve payout temelini anlatır.

## Diagramlar

1. `01_payment_use_case`
   - Accept quote
   - Create payment session
   - Choose payment method
   - Provider checkout
   - Saved card tokenized payment
   - Add card token
   - Provider callback verification
   - Commission calculation
   - Payout hold
   - Refund / payout admin actions

2. `02_payment_activity`
   - PaymentSession creation
   - saved card vs checkout branch
   - provider callback
   - signature/amount/currency/state validation
   - idempotency
   - PaymentAuthorized / PaymentFailed
   - CommissionCalculated
   - payout hold

3. `03_payment_sequence`
   - Requester Client → BFF → marketplace-service → payment-service → provider adapter → PostgreSQL → Kafka → notification-service

4. `04_payment_component`
   - PaymentController
   - ProviderCallbackController
   - Payment use cases
   - Payment policies
   - PaymentProvider / WalletProvider ports
   - iyzico / Stripe / Masterpass adapters
   - payment tables
   - admin refund/payout actions

5. `05_payment_class_model`
   - PaymentSession
   - StoredPaymentMethod
   - ProviderCallbackRecord
   - CommissionLedger
   - RefundRequest
   - PayoutHold
   - PaymentPolicy
   - PaymentEvents

6. `06_payment_state`
   - CREATED
   - PENDING
   - AUTHORIZED
   - CAPTURED
   - FAILED
   - CANCELLED
   - REFUND_REQUESTED
   - REFUNDED
   - PAYOUT_HELD
   - PAYOUT_ELIGIBLE
   - PAYOUT_RELEASED
   - PAYOUT_BLOCKED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Viaverse raw card PAN/CVV saklamaz.
- Stored cards provider token olarak tutulur.
- cardBrand / last4 gibi safe display alanları tutulabilir.
- Payment provider SDK’ları sadece adapter katmanında olur.
- Provider callback verification zorunludur.
- Create session, callback, capture, refund, payout release idempotent olmalıdır.
- Commission initial 5% olarak hesaplanır.
- Captured payment ile payout state ayrı tutulur.
- Dispute/risk payout’u block eder.
- Refund/payout admin action reason + audit ister.
