# Payment Provider Contracts

Payment provider integrations are owned by payment-service adapters.

## Supported Direction

```text
Iyzico for Türkiye
Stripe-ready abstraction for global
Masterpass if supported through provider/provider-token flow
```

## Rule

Provider raw statuses must be mapped at adapter boundary.

Domain uses typed provider-independent states:

```text
PaymentProviderType
PaymentIntentStatus
PaymentCaptureStatus
RefundStatus
PayoutStatus
```
