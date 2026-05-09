# Viaverse UML Part 05 — Worker Onboarding / Work Mode Flow

Bu part bireysel olarak, işletme hesabı oluşturmadan iş yapmak isteyen kullanıcının Work Mode onboarding akışını anlatır.

Bu akış Personal Mode'dan hafif bir "become a host" geçişi gibi hissedilmelidir: aynı Account korunur, kullanıcı bireysel hizmet verme yeteneğini açar, kategori/alan/uygunluk bilgilerini tamamlar ve policy izin verdiğinde fırsat/teklif yüzeyleri aktif olur.

## Diagramlar

1. `01_worker_onboarding_use_case`
   - Enable Work Mode
   - Eligibility check
   - Worker profile
   - Category/skill selection
   - Service area
   - Availability
   - Portfolio
   - Verification
   - Opportunity queue
   - Send free offer

2. `02_worker_onboarding_activity`
   - Eligibility decision
   - Profile setup
   - Category/area/availability
   - Optional portfolio media
   - Optional verification
   - Publish and index profile

3. `03_worker_onboarding_sequence`
   - Client → BFF → identity-service → marketplace-service → media-service → PostgreSQL → Kafka → search-service

4. `04_worker_onboarding_class_model`
   - WorkerProfile
   - WorkerCategory
   - ServiceArea
   - WorkerAvailability
   - PortfolioItem
   - WorkerVerification
   - WorkerOpportunity
   - WorkerProfilePolicy

5. `05_worker_profile_state`
   - NOT_CREATED
   - DRAFT
   - PENDING_VERIFICATION
   - ACTIVE
   - PAUSED
   - SUSPENDED
   - VERIFICATION_REJECTED
   - DELETED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Worker ayrı hesap değildir; aynı Account üstünde DO_WORK capability açılır.
- Worker business değildir; bireysel hizmet veren profilidir.
- Personal Mode ile Work Mode arasında geçiş kolay olmalıdır, ancak onboarding/policy tamamlanmadan teklif gönderme ve opportunity routing açılmaz.
- Sending offers is free.
- Worker visibility eligibility + trust + service area + category + availability ile belirlenir.
- Verification kategori/risk seviyesine göre zorunlu olabilir.
- PAUSED worker-controlled, SUSPENDED policy/admin-controlled state’tir.
- WorkerProfile marketplace/work capability datadır; Account identity owner olarak identity-service tarafında kalır.
