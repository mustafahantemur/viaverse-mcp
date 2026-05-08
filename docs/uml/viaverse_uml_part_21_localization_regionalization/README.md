# Viaverse UML Part 21 — Localization / Multi-language / Regionalization Flow

Bu part TR/EN başlangıç dil desteği, locale resolution, category/service localization, regional rules, currency/tax formatting, localized SEO, hreflang/canonical, notification template localization ve translation admin workflow’unu anlatır.

## Diagramlar

1. `01_localization_use_case`
   - Locale detection
   - Language selection
   - Date/number/currency/unit formatting
   - Localized UI/content
   - Category/service/tag localization
   - Regional legal/country/city rules
   - Localized pricing
   - Localized notifications
   - Localized SEO pages
   - Slug/canonical/hreflang
   - Translation management
   - Fallback chain
   - Translation quality checks

2. `02_locale_resolution_activity`
   - Profile locale
   - Device/browser locale
   - SEO route/country locale
   - Effective locale resolution
   - Fallback chain
   - Bundle/regional config load
   - Money/date/unit formatting
   - SEO/notification branch
   - Missing key tracking

3. `03_localization_sequence`
   - Client/Web SSR → BFF → localization-service → identity/profile → taxonomy-service → pricing/payment config → translation store → Kafka

4. `04_localization_component`
   - KMP/Next.js resource bundles
   - BFF localized DTO facade
   - localization-service
   - translation store
   - taxonomy-service
   - pricing/config-service
   - SEO/search
   - notification-service
   - Kafka

5. `05_localization_class_model`
   - LocaleProfile
   - TranslationKey
   - TranslationValue
   - RegionalConfig
   - LocalizedSlug
   - LocalizedTemplate
   - LocalizationPolicy
   - LocalizationEvents

6. `06_translation_regional_state`
   - MISSING
   - DRAFT
   - IN_REVIEW
   - PUBLISHED
   - FALLBACK_ACTIVE
   - DEPRECATED
   - ARCHIVED
   - REGIONAL_CONFIG_DRAFT
   - REGIONAL_CONFIG_PUBLISHED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Localization sadece text translation değildir.
- Locale UI, category names, search, SEO, notification templates, currency/tax formatting ve legal/policy surfaces’ı etkiler.
- Canonical domain values language-neutral kalır.
- Localization layer ID/value’ları locale-specific label, slug, template ve format’a çevirir.
- Translation keys stable code contract’tır; text values versioned content’tir.
- Missing translation app’i kırmaz; fallback olur ve quality metric/event üretir.
- SEO localized slugs canonical/hreflang consistency olmadan publish edilmez.
- TR/EN başlangıç, global expansion için future locale modelini kırmayacak şekilde tasarlanır.
