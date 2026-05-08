# Viaverse UML Part 04 — Matching / Search / Recommendation Flow

Bu part, yayınlanan service request sonrasında sistemin doğru worker/business adaylarını bulma ve opportunity üretme akışını anlatır.

## Diagramlar

1. `01_matching_activity`
   - MatchingRequested eventinden WorkerOpportunity ve notification üretimine kadar activity flow.

2. `02_matching_sequence`
   - Kafka → matching-consumer → marketplace-service → OpenSearch → identity-service → trust-gamification → Valkey → notification-service.

3. `03_matching_component`
   - Candidate Generator, Eligibility Filter, Ranking Engine, Trust/Risk, OpenSearch, Valkey, Notification bileşenleri.

4. `04_matching_data_flow`
   - Request snapshot, OpenSearch index, identity eligibility, trust data, hot state, ranker, opportunity projection, notifications.

5. `05_matching_class_model`
   - RequestSnapshot, CandidateProvider, EligibilityResult, TrustSummary, MatchScore, MatchExplanation, WorkerOpportunity, MatchingPolicy.

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Boost/subscription sinyali en son uygulanır.
- Trust, safety, risk, eligibility ve relevance gate'leri geçilmeden boost uygulanmaz.
- Matching explainable olmalı.
- New worker fairness kontrollü exposure olarak uygulanır.
- Search/OpenSearch projectiondır; source-of-truth marketplace-service/PostgreSQL tarafındadır.
- Internal risk/fraud reasonları normal kullanıcıya gösterilmez.
