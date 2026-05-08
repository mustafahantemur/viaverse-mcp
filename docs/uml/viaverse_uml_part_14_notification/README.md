# Viaverse UML Part 14 — Notification / Inbox / Push / Email / SMS Flow

Bu part domain eventlerinden in-app inbox, push, email, SMS, retry, digest, preference ve admin broadcast akışına kadar notification sistemini anlatır.

## Diagramlar

1. `01_notification_use_case`
   - Consume domain event
   - Notification policy
   - User preferences
   - Localized template
   - In-app inbox item
   - Push / email / SMS delivery
   - Mark read/archive
   - Retry
   - Digest
   - Admin broadcast
   - Audit and delivery events

2. `02_notification_activity`
   - Deduplicate event
   - Policy evaluation
   - Preferences/quiet hours
   - Template rendering
   - Inbox creation
   - Channel dispatch
   - Delivery result
   - Retry/backoff/digest
   - Delivery events

3. `03_notification_sequence`
   - Kafka → notification-service → identity/preferences → template/i18n → PostgreSQL → FCM/APNs → SMTP/Netgsm → Client

4. `04_notification_component`
   - Notification event consumer
   - NotificationController
   - PreferenceController
   - Notification use cases
   - Policies
   - Provider adapters
   - Retry worker
   - PostgreSQL
   - Kafka

5. `05_notification_class_model`
   - Notification
   - NotificationAttempt
   - NotificationPreference
   - DeviceToken
   - NotificationTemplate
   - NotificationPolicy
   - AdminCampaign
   - NotificationEvents

6. `06_notification_state`
   - CREATED
   - SUPPRESSED
   - QUEUED
   - SCHEDULED
   - DISPATCHING
   - SENT
   - FAILED
   - READ
   - ARCHIVED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Domain services push/SMS/email provider’larına doğrudan gitmez.
- Notification service domain event tüketir ve kime, ne zaman, hangi kanaldan gidileceğine karar verir.
- In-app inbox user-visible source log olarak kalır.
- Push/email/SMS sadece delivery channel’dır.
- Preference, consent, quiet hours, rate limit ve retry policy zorunludur.
- High-priority transactional events digest’e düşmemeli.
- Deduplication key her notification için olmalı.
- Device token raw erişimi kısıtlı/şifreli olmalı; broad read modelde tokenHash/display metadata tutulmalı.
