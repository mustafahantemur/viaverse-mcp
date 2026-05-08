# Viaverse UML Part 24 — Category / Taxonomy / Dynamic Environment Configuration Flow

Bu part kategori ağacı, tag/intent sistemi, dinamik çevre, request/provider category mapping, category-specific form schemas, search facets, localized labels, taxonomy versioning ve admin publish workflow’unu anlatır.

## Diagramlar

1. `01_taxonomy_dynamic_environment_use_case`
   - Configure dynamic environment
   - Browse category tree
   - Select tags/intents
   - Personalized feed filters
   - Create request with category/budget/urgency
   - Map provider capabilities
   - Match request to providers
   - Rank by relevance/trust/distance/availability
   - Manage taxonomy
   - Manage category rules/forms/fees/risk
   - Localize category labels/slugs
   - Publish taxonomy version
   - AI category suggestion

2. `02_taxonomy_publish_activity`
   - Taxonomy draft
   - Category/tag/form editing
   - Category rules
   - Localized labels/slugs/icons
   - Validation
   - Impact analysis
   - Review/approval
   - Publish
   - Cache invalidation
   - Search reindex
   - Quality monitoring

3. `03_dynamic_environment_matching_sequence`
   - Client/BFF/taxonomy-service/profile/search/trust/analytics/Kafka
   - Dynamic environment feed/search/matching resolution

4. `04_taxonomy_component`
   - Client category picker/forms
   - Admin taxonomy editor
   - taxonomy-service
   - localization-service
   - search-service
   - analytics/AI
   - PostgreSQL
   - Kafka

5. `05_taxonomy_class_model`
   - Category
   - Tag
   - CategoryRule
   - FormSchema
   - DynamicEnvironment
   - CategoryMapping
   - TaxonomyVersion
   - TaxonomyEvents

6. `06_taxonomy_mapping_state`
   - VERSION_DRAFT
   - VERSION_IN_REVIEW
   - VERSION_PUBLISHED
   - VERSION_RETIRED
   - UNMAPPED
   - AI_SUGGESTED
   - MAPPED
   - REMAP_REQUIRED
   - MAPPING_ARCHIVED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- “Dinamik çevre” fixed neighborhood değildir.
- Kullanıcı kendi area, interest, category ve intent sinyallerine göre feed/search/matching kapsamını belirler.
- Taxonomy service canonical category/tag/rule/form-schema modelinin sahibidir.
- Search, localization ve clients published taxonomy versions tüketir.
- Category tree, tags, form schemas ve category rules ayrı modellenir.
- Category-specific fields client screen/backend DTO içine hardcode edilmez.
- Taxonomy değişiklikleri versioned olmalı.
- Published version existing requests, provider capabilities ve SEO URLs ile backward-compatible olmalı ya da explicit migration mapping içermeli.
- Existing request/provider data taxonomy değişiminde kırılmamalı; remap-required state kullanılır.
