# Viaverse UML Part 22 — Business Account / Merchant Operations Flow

Bu part işletme hesabı, merchant onboarding, işletme doğrulama, şube/hizmet alanı, staff roles, hizmet kataloğu, lead alma, business quote, job/order yönetimi, abonelik ve search publish akışlarını anlatır.

## Diagramlar

1. `01_business_account_use_case`
   - Create business account
   - Verify business
   - Manage business profile
   - Manage branches/service areas
   - Invite staff and roles
   - Manage service catalog
   - Manage availability
   - Receive leads/requests
   - Send quote as business
   - Manage jobs/orders
   - Manage subscription
   - Business insights
   - Reviews/reputation
   - Publish to search

2. `02_business_onboarding_activity`
   - Operate Business capability
   - Business draft
   - Profile completion
   - Verification docs
   - Verification decision
   - Service catalog
   - Branch/service area
   - Plan/trial
   - Publish-ready check
   - Search/lead routing publish

3. `03_business_onboarding_sequence`
   - Client/BFF/business-service/identity-service/media-service/billing-service/search-service/Kafka

4. `04_business_component`
   - Business UI
   - BFF
   - Business controllers/use cases/policies/ports
   - identity-service
   - media-service
   - billing-service
   - PostgreSQL
   - search/trust
   - Kafka

5. `05_business_class_model`
   - BusinessAccount
   - BusinessBranch
   - BusinessStaffMember
   - ServiceCatalogItem
   - BusinessVerification
   - BusinessSubscription
   - BusinessPolicy
   - BusinessEvents

6. `06_business_account_state`
   - DRAFT
   - PROFILE_INCOMPLETE
   - PENDING_VERIFICATION
   - VERIFIED
   - VERIFICATION_REJECTED
   - PUBLISH_READY
   - PUBLISHED
   - SUSPENDED
   - ARCHIVED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Business account aynı Account modelinin capability’sidir; ayrı identity island değildir.
- BusinessAccount, Account ile aynı şey değildir: Account identity/login sahibidir; BusinessAccount merchant profile/branch/catalog/staff/publish state sahibidir.
- Business UI bugün tek uygulama içinde akış olabilir, ileride ayrı store app olabilir; backend model bundan bağımsız kalır.
- Business draft verification’dan önce oluşturulabilir.
- Public search visibility ve lead routing için publish-ready policy gerekir.
- VERIFIED tek başına yeterli değildir; catalog, branch/service area ve billing/subscription gate’leri de geçmelidir.
- Staff permissions backend policy ile kontrol edilir; UI seçimine güvenilmez.
