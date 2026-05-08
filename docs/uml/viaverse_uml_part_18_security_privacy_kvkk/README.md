# Viaverse UML Part 18 — Security / Privacy / KVKK Flow

Bu part authentication, authorization, rate limiting, consent, DSAR/KVKK, retention, secret management, encryption, audit and incident response akışlarını anlatır.

## Diagramlar

1. `01_security_privacy_use_case`
   - Secure login
   - JWT / refresh token
   - Capability authorization
   - Rate limit / abuse guard
   - Consent management
   - DSAR export/delete/correct
   - Data minimization
   - Retention/deletion
   - Encryption
   - External Secrets + KMS
   - Sensitive access audit
   - Security incident response

2. `02_auth_authorization_activity`
   - TLS/WAF/edge checks
   - Rate limit
   - Token validation
   - Claims/capabilities load
   - RBAC/ABAC/ownership/resource-state policy
   - Sensitive audit
   - Redacted response

3. `03_privacy_dsar_sequence`
   - User → Client → BFF → privacy-service → identity-service → domain services → audit-service → Kafka
   - DSAR orchestration and receipts

4. `04_security_privacy_component`
   - Client secure storage/privacy center
   - AWS Edge/WAF/TLS
   - API Gateway
   - identity-service
   - privacy-service
   - domain services
   - External Secrets + KMS
   - audit-service
   - DB
   - Kafka
   - security monitoring

5. `05_security_privacy_class_model`
   - AccountSession
   - CapabilityGrant
   - ConsentRecord
   - DataSubjectRequest
   - SecurityPolicy
   - DataClassification
   - SensitiveAccessAudit
   - SecurityPrivacyEvents

6. `06_consent_dsar_state`
   - CONSENT_UNKNOWN
   - CONSENT_GRANTED
   - CONSENT_WITHDRAWN
   - DSAR_OPENED
   - IDENTITY_VERIFIED
   - PROCESSING
   - COMPLETED
   - REJECTED
   - INCIDENT_RAISED
   - INCIDENT_RESOLVED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Backend her use case’te capability, ownership, resource state ve policy kontrolü yapmalı.
- UI state’e güvenilmez.
- Authentication, authorization, consent, retention ve audit platform-level controls kabul edilir.
- Account identity, capability grants ve consent ayrı kavramlardır.
- Product capabilities sadece JWT içindeki string role olarak modellenmemeli.
- Domain services direct secret access yapmaz; External Secrets/KMS üzerinden kontrollü erişir.
- Sensitive/private access auditlenir.
- DSAR privacy-service tarafından koordine edilir; domain services kendi export/delete/anonymize logic’ini çalıştırır.
- Consent withdrawal optional future processing’i durdurur; legally required transactional records otomatik silinmeyebilir.
