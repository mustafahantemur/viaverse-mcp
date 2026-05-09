# Viaverse UML Part 02 — Account Capability / Mode Switching

Bu part, tek Account altında capability modelini ve kullanıcı yüzeyleri arasındaki geçiş kurallarını anlatır.

Personal Mode ile Work Mode arasındaki geçiş hafif olmalıdır: kullanıcı aynı Account ile, Airbnb'deki "become a host" benzeri bir onboarding sonrası bireysel hizmet verme yeteneğini açar. Business Mode ise aynı login altında erişilen ama ayrı BusinessAccount/business profile, merchant onboarding, verification, staff, catalog, subscription ve publish-ready policy gerektiren farklı bir akıştır.

## Diagramlar

1. `01_capability_use_case`
   - Account, Business Owner, Admin aktörleri
   - Personal Mode, Work Mode, Business Mode
   - Capability enable/suspend/revoke

2. `02_capability_activity`
   - Mode switch activity
   - Work ve Business için onboarding/check kararları
   - Suspension/revoke durumunda bloklama

3. `03_capability_state`
   - Capability state machine
   - NOT_ENABLED → PENDING_ONBOARDING → PENDING_REVIEW → ENABLED
   - SUSPENDED / REVOKED / ACCOUNT_DELETED durumları

4. `04_capability_class_model`
   - Account, AccountCapability, BusinessProfile, BusinessTeamMember, CapabilityPolicy, CapabilityAuditLog

5. `05_capability_package_component`
   - Mobile ModeSwitcher
   - BFF
   - identity-service
   - business/marketplace boundary
   - admin-bff
   - PostgreSQL
   - Kafka/Outbox

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Personal / Work / Business ayrı hesap değildir.
- Account tek kalır, capability seti değişir.
- Personal ↔ Work geçişi hafif UX akışıdır; DO_WORK_INDIVIDUALLY capability açılmadan teklif/opportunity yüzeyleri aktif olmaz.
- Business'a geçiş hafif mode switch değildir; OPERATE_BUSINESS capability business onboarding ve BusinessAccount seçimi/oluşturma akışını başlatır.
- UI mode switch sadece UX aksiyonudur; eligibility ve business/work gate'leri backend policy tarafından belirlenir.
- Capability suspension/revoke ayrı ayrı uygulanabilir.
- Admin action reason + audit gerektirir.
