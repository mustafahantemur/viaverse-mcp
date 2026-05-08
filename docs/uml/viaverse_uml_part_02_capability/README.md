# Viaverse UML Part 02 — Account Capability / Mode Switching

Bu part, tek Account altında Personal / Work / Business modlarına geçiş ve capability modelini anlatır.

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
- UI mode switch sadece UX aksiyonudur; eligibility backend policy tarafından belirlenir.
- Capability suspension/revoke ayrı ayrı uygulanabilir.
- Admin action reason + audit gerektirir.
