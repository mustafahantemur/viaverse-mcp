# Viaverse UML Part 23 — Monetization / Subscription / Ads / Commission Configuration Flow

Bu part işletme aboneliği, ilk ay ücretsiz trial, 500 TL gibi admin-configurable planlar, %5 default configurable commission, job/order platform fee, net payout, sponsored placement, ad budget, promotions ve revenue reporting akışlarını anlatır.

## Diagramlar

1. `01_monetization_use_case`
   - Choose subscription plan
   - First month free trial
   - Billing profile
   - Renew/cancel/downgrade
   - Commission calculation
   - Platform fee
   - Net payout
   - Refund/dispute adjustment
   - Sponsored placement
   - Ad budget/caps/pacing
   - Ad relevance gates
   - Revenue reporting
   - Admin monetization config
   - Promotions/coupons/free credits

2. `02_subscription_billing_activity`
   - Eligibility
   - Trial availability
   - Payment method
   - Billing profile
   - Subscription activation
   - Feature gates
   - Renewal charge
   - Dunning/grace
   - Feature limitation
   - Subscription events

3. `03_commission_payout_sequence`
   - marketplace-service → monetization-service → pricing/config-service → payment-service → ledger/accounting → payout provider → trust/reporting → Kafka

4. `04_monetization_component`
   - Business UI
   - Admin UI
   - BFF
   - Monetization controllers/use cases/policies/ports
   - payment-service
   - ledger/accounting
   - search/ads-service
   - PostgreSQL
   - Kafka
   - analytics/reporting

5. `05_monetization_class_model`
   - SubscriptionPlan
   - BusinessSubscription
   - CommissionRule
   - PlatformFee
   - SponsoredPlacement
   - Promotion
   - MonetizationPolicy
   - MonetizationEvents

6. `06_subscription_ads_state`
   - TRIALING
   - ACTIVE
   - PAST_DUE
   - GRACE_PERIOD
   - LIMITED
   - CANCELLED
   - AD_DRAFT
   - AD_REVIEW
   - AD_LIVE
   - AD_PAUSED
   - AD_ENDED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Normal users free by default.
- Monetization başlangıcı: business subscription + configurable commission + non-blocking sponsored placements.
- 500 TL, first month free, %5 commission gibi değerler hardcode edilmez; admin-configurable ve effective-dated tutulur.
- Sponsored placement relevance/safety/privacy gate’lerini bypass edemez.
- Monetization service business rules hesaplar; payment-service money movement ve provider integration sahibidir.
- Ledger/accounting financial truth tutar.
- Marketplace-service içinde fee/commission hesaplanmaz.
- Historic invoices and fee records used-rule snapshot saklamalıdır.
- Subscription state business feature gates’i etkiler; ad state sponsored visibility’i etkiler.
