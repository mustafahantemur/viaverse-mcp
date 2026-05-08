# Viaverse UML Part 11 — Review / Trust / Badge / Gamification Flow

Bu part tamamlanan işler sonrası iki taraflı yorum, puanlama, trust score, badge ve gamification akışlarını anlatır.

## Diagramlar

1. `01_trust_use_case`
   - Review prompt
   - Worker/business review
   - Requester review
   - Review tags
   - Review eligibility validation
   - Moderation
   - Trust score recalculation
   - Badge evaluation
   - Points/streak update
   - Ranking signal update
   - Abusive review report
   - Admin review moderation

2. `02_trust_activity`
   - JobCompleted
   - Review prompt creation
   - Review submit
   - Duplicate/eligibility check
   - Moderation scan
   - Publish or admin queue
   - Trust score
   - Badge award/revoke
   - Gamification points
   - Search ranking update

3. `03_trust_sequence`
   - Kafka JobCompleted → trust-gamification-service
   - Client review submission
   - marketplace-service job participant validation
   - moderation-service scan
   - PostgreSQL persist
   - TrustScoreUpdated / BadgeAwarded events
   - search/matching projection update

4. `04_trust_class_model`
   - Review
   - ReviewTag
   - ReviewPrompt
   - TrustScore
   - BadgeAward
   - GamificationAccount
   - BadgeRule
   - TrustPolicy
   - TrustEvents

5. `05_trust_component`
   - TrustController / ReviewController
   - Trust use cases
   - Trust policies
   - Marketplace validation
   - Moderation
   - PostgreSQL
   - Kafka
   - Search/matching projection

6. `06_badge_state`
   - Review prompt lifecycle
   - Review moderation states
   - Badge eligible / active / revoked lifecycle

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Reviews are job-bound.
- Reviews are two-sided.
- One review per side per job.
- TrustScore derived aggregate olarak hesaplanır.
- Matching trust signal tüketir ama trust hesaplamaz.
- Internal risk/fraud reasonları kullanıcıya gösterilmez.
- Badge kalıcı garanti değildir; risk/trust değişirse revoke/downgrade olabilir.
- Review moderation PII, abuse, spam, hate ve manipülasyon kontrollerini içermeli.
