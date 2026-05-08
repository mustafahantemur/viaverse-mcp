# Viaverse UML Part 06 — Business Account / Business Mode Flow

Bu part işletme hesabı / Business Mode akışını anlatır.

## Diagramlar

1. `01_business_use_case`
   - Start Business Mode
   - Create business profile
   - Business verification
   - Trial/subscription
   - Services/categories
   - Packages/pricing
   - Operating hours/service area
   - Team roles
   - Portfolio
   - Public profile
   - Matched requests
   - Free business offers
   - Dashboard/subscription/pause

2. `02_business_activity`
   - Business onboarding activity
   - Eligibility, profile, services/packages, team, verification, trial, publish, index

3. `03_business_sequence`
   - Client → BFF → identity-service → marketplace-service → monetization/payment → PostgreSQL → Kafka → search/SEO

4. `04_business_class_model`
   - BusinessProfile
   - BusinessService
   - BusinessPackage
   - BusinessHours
   - BusinessTeamMember
   - BusinessServiceArea
   - BusinessSubscription
   - BusinessPolicy
   - BusinessEvents

5. `05_business_state`
   - Business profile state
   - Subscription state
   - Profile and subscription state separation

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- BusinessProfile ayrı login identity değildir.
- Aynı Account, OPERATE_BUSINESS capability ile business profile yönetir.
- BusinessTeamMember role-based access sağlar.
- Business subscription tooling/visibility sağlar ama pay-to-quote değildir.
- Business offers free kalır.
- Subscription/boost trust, safety ve relevance gate’lerini bypass edemez.
- Profile state ve subscription state ayrı tutulur.
