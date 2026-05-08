# Viaverse UML Part 15 — Admin / Moderation / Operations Flow

Bu part admin panel, moderation queue, support case, RBAC/ABAC, audit, appeal ve platform config operasyonlarını anlatır.

## Diagramlar

1. `01_admin_use_case`
   - Admin login with MFA
   - Role/permission check
   - Moderation queue
   - Admin case
   - Scoped user/account inspection
   - Reported content review
   - Moderation actions
   - Support/dispute handling
   - Refund/payout approval
   - Platform config
   - Admin broadcast
   - Audit and appeal

2. `02_admin_activity`
   - Admin MFA/session check
   - RBAC/ABAC check
   - Evidence review
   - Action decision
   - Reason required
   - Policy validation
   - Domain action
   - Immutable audit
   - Appeal path
   - Admin events

3. `03_admin_sequence`
   - Admin UI → Admin BFF → identity-service → admin-ops-service → domain service → audit-service → PostgreSQL → Kafka

4. `04_admin_component`
   - React Admin UI
   - Admin BFF
   - Admin controllers/use cases/policies/ports
   - identity-service
   - domain services
   - audit-service
   - PostgreSQL
   - Kafka
   - Monitoring/SIEM

5. `05_admin_class_model`
   - AdminCase
   - AdminAction
   - AdminPermission
   - AuditRecord
   - Appeal
   - PlatformConfigChange
   - AdminPolicy
   - AdminEvents

6. `06_admin_case_state`
   - OPENED
   - TRIAGE
   - ASSIGNED
   - UNDER_REVIEW
   - ACTION_APPLIED
   - CLOSED
   - ESCALATED
   - APPEAL_OPENED
   - APPEAL_REVIEW
   - APPEAL_RESOLVED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Admin access MFA-protected olmalı.
- Admin UI domain servislerine doğrudan gitmez.
- Tüm admin action’lar Admin BFF + policy + audit üzerinden akar.
- RBAC/ABAC ve resource scope kontrolü zorunludur.
- Least privilege: admin ekranları minimum gerekli veriyi göstermeli.
- Private messages/media access explicit reason + scoped audit ister.
- Destructive, privacy-sensitive, suspension ve money action reason olmadan geçersizdir.
- Appeal separation of duties gerektirebilir.
