# WEB_FRONTEND_STRUCTURE.md

## Purpose

Web uses Next.js + TypeScript for landing, SEO, admin console and optional business console.

## Landing Web Tree

```text
apps/landing-web/
  package.json
  next.config.ts
  src/
    app/
      [locale]/
        layout.tsx
        page.tsx
        kategori/[slug]/page.tsx
        sehir/[citySlug]/page.tsx
        hizmet/[serviceSlug]/page.tsx
        business/[businessSlug]/page.tsx
        privacy/page.tsx
        kvkk/page.tsx
        terms/page.tsx
    components/
      layout/
      landing/
      seo/
      forms/
    content/
      tr/
      en/
    lib/
      seo/
      localization/
      analytics/
```

## Admin Web Tree

```text
apps/admin-web/
  src/
    app/
      login/
      dashboard/
      users/
      businesses/
      requests/
      payments/
      moderation/
      taxonomy/
      monetization/
      support/
      settings/
    components/
      admin-layout/
      data-table/
      forms/
      charts/
      audit/
    features/
      users/
      businesses/
      marketplace/
      moderation/
      taxonomy/
      monetization/
      support/
    lib/
      api/
      auth/
      permissions/
      formatters/
```

## Type Rule

Use generated contract types/enums. Do not use raw string checks such as `user.type === "customer"` or `status === "ACTIVE"`.
