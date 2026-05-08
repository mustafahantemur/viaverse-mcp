# Viaverse UML Part 01 — Authentication / Login / Onboarding

Bu part sadece authentication ve onboarding akışına odaklanır.

## İçerik

1. `01_auth_use_case`
   - Guest user, authenticated account, OAuth provider, OTP provider, support/security admin.
   - Login method selection, phone/email OTP, social login, account create/find, consent, first intent, session, refresh, revoke.

2. `02_auth_activity`
   - OTP ve OAuth dallanması.
   - OTP challenge creation, send, verify.
   - OAuth callback verify.
   - account create/find, consent, intent, session creation.

3. `03_auth_sequence`
   - OTP login sequence.
   - Client → BFF → identity-service → Valkey → Netgsm/SES → PostgreSQL → Kafka/Outbox.

4. `04_auth_component`
   - Client, BFF, AuthController, use cases, policies, ports, DB, Redis/Valkey, providers, Kafka.

5. `05_auth_class_model`
   - Account, AccountCapability, LinkedIdentity, ConsentRecord, OtpChallenge, Session, RefreshTokenFamily, AuthPolicy, AuthEvents.

## Formatlar

Her diagram için:

- `.drawio` — diagrams.net / draw.io içinde düzenlenebilir
- `.svg` — tarayıcıda hızlı kontrol
- `.puml` — PlantUML kaynak

## Kritik Kurallar

- JWT `sub` = persisted `Account.id`
- OTP kodları plaintext saklanmaz, hash saklanır
- Refresh token opaque + hash + rotation
- Required consent version saklanır
- Account status her protected requestte kontrol edilir
- Capabilities accounttan bağımsız şekilde suspend/revoke edilebilir
