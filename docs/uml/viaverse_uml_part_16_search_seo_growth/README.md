# Viaverse UML Part 16 — Search / SEO / Growth / AI Discovery Flow

Bu part search, discovery, SEO landing pages, growth analytics, recommendation, sponsored placement ve AI-assisted search akışını anlatır.

## Diagramlar

1. `01_search_seo_growth_use_case`
   - Search dynamic environment
   - Category/location/budget/rating filters
   - Ranking by relevance/trust/freshness
   - AI-assisted intent/category suggestion
   - Public entity indexing
   - Privacy/visibility gates
   - SEO page generation
   - Sitemap/schema.org
   - Privacy-safe analytics
   - Recommendation
   - A/B tests
   - Native sponsored placement

2. `02_search_activity`
   - Query parsing
   - Optional AI intent
   - Privacy gates
   - OpenSearch query
   - Availability/capability/trust/safety filter
   - Ranking
   - Sponsored after gates
   - Personalization
   - Analytics and quality metrics

3. `03_search_sequence`
   - Client/BFF/search-service/OpenSearch/trust/analytics/Kafka
   - Entity indexing event flow

4. `04_search_component`
   - SearchController
   - SeoController
   - GrowthController
   - Search use cases/policies/ports
   - OpenSearch
   - AI intent service
   - analytics-service
   - SEO DB
   - CDN/SSR

5. `05_search_class_model`
   - SearchDocument
   - SearchQuery
   - RankingScore
   - SeoPage
   - SearchAnalyticsEvent
   - GrowthExperiment
   - SearchPolicy
   - SearchEvents

6. `06_seo_index_state`
   - PENDING_INDEX
   - INDEXED_VISIBLE
   - INDEXED_HIDDEN
   - STALE_REBUILD_NEEDED
   - REMOVED
   - SEO_DRAFT
   - SEO_PUBLISHED
   - SEO_NOINDEX

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- SearchDocument projection’dır, source-of-truth değildir.
- Public search exact address/private request details içermez.
- Public search approximate area + approved public fields kullanır.
- Sponsored placement safety/privacy/relevance gate’lerini bypass edemez.
- SEO pages sadece public/approved projectionlardan üretilir.
- Thin/duplicate/private sayfalar için noindex tercih edilir.
- AI-assisted search category/intent suggestion yapabilir; final privacy/ranking gate yine search policy’den geçer.
