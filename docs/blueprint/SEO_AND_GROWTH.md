# SEO_AND_GROWTH.md — Viaverse SEO, Search, AI Discovery and Growth Model

This document defines Viaverse's SEO, growth loops, marketplace analytics, AI-search readiness, and demand/supply growth model.

---

## 1. Growth Principle

Viaverse should grow through trust, completed jobs, public discovery, and repeat use.

Core growth principle:

> Better matching creates better jobs. Better jobs create reviews and work proof. Reviews and work proof create trust. Trust creates more demand and supply.

---

## 2. Growth Loops

### 2.1 Demand Loop

```text
SEO / public discovery
-> service request
-> offers
-> completed job
-> review
-> trust
-> more demand
```

### 2.2 Supply Loop

```text
Worker/business joins
-> receives matched opportunities
-> earns money
-> gets reviews and badges
-> ranks better
-> gets more work
```

### 2.3 Trust Loop

```text
Verification
-> safe payment
-> job completion
-> two-sided review
-> moderation/risk control
-> better matching
```

### 2.4 Content Loop

```text
Completed job
-> work proof
-> feed engagement
-> provider/business discovery
-> new service request
```

---

## 3. SEO Strategy

SEO is a major acquisition channel because people search for services with high intent.

Public page types:

- City/category pages
- Provider public profiles
- Business public profiles
- Service category guides
- FAQ pages
- Blog/content pages
- How-to guides
- Comparison pages where appropriate

Examples:

```text
/tr/istanbul/cleaning
/tr/ankara/plumbing
/business/{slug}
/provider/{slug}
/services/{category}
```

Rules:

- Next.js SSR/SSG.
- Turkish and English localization.
- schema.org structured data.
- Canonical URLs.
- Sitemap.
- OpenGraph metadata.
- Private service requests are not indexed.

---

## 4. Programmatic SEO

Programmatic SEO can scale city/category pages.

Page template:

- City + category heading
- What the service includes
- Typical price guidance
- Popular subcategories
- Top public providers/businesses
- Recent public work proof where safe
- FAQ
- CTA to request service
- Structured data

Quality gates:

- No empty pages indexed.
- Minimum provider/business/category content threshold.
- Localized copy.
- Duplicate prevention.
- Canonical strategy.
- Manual admin override.
- No private request data.

---

## 5. Structured Data

Use schema.org where appropriate:

- LocalBusiness
- Service
- Review
- FAQPage
- BreadcrumbList
- Organization
- WebSite
- SearchAction

Rules:

- Structured data must match visible content.
- Do not fake reviews.
- Do not expose private data.
- Localized pages must use correct language metadata.

---

## 6. AI Search Readiness

Users may discover services through AI search engines and assistants.

Requirements:

- Semantic HTML
- Structured data
- Clear entity pages
- High-quality FAQ blocks
- Public trust/review signals
- Readable service descriptions
- Clear category hierarchy
- Consistent URLs
- No thin/spammy pages

Content governance:

- Admin-managed SEO metadata.
- AI-assisted draft generation allowed.
- Human review for important public content.
- Localized quality checks.

---

## 7. Search Strategy

Search targets:

- Providers
- Businesses
- Categories
- Services
- Public profiles
- Feed posts
- Work proof
- Saved searches
- Public SEO pages

Infrastructure:

- OpenSearch
- Kafka-driven indexing
- PostgreSQL source of truth
- Search documents as projections

Search filters:

- Category
- Subcategory
- Price/budget range
- Distance
- Availability
- Rating
- Verification
- Business/individual
- Language
- Service area
- Response speed
- Badges
- Open today

---

## 8. Smart Search UX

Users can search naturally.

Example:

```text
"I need a verified cleaner for tomorrow morning near Kadıköy under 1500 TL"
```

AI converts to:

```text
category = Cleaning
verification = verified
date = tomorrow morning
location = Kadıköy
budgetMax = 1500
```

UX rules:

- Show editable filter chips.
- Let users remove AI assumptions.
- Explain no-result cases.
- Suggest alternatives.
- Do not hide filters behind magic.

---

## 9. Matching Growth

Matching improves marketplace liquidity.

Matching inputs:

- Category
- Subcategory
- Location
- Budget
- Urgency
- Availability
- Rating
- Completed similar jobs
- Response speed
- Trust score
- Badges
- Risk score
- Locality score
- Profile completeness

Initial ranking:

```text
matchScore =
  categoryFit
  + distanceScore
  + availabilityScore
  + budgetFitScore
  + ratingScore
  + completedSimilarJobsScore
  + responseSpeedScore
  + trustScore
  + localityScore
  + profileCompletenessScore
  + eligibleBoostSignal
  - cancellationPenalty
  - disputePenalty
  - riskPenalty
```

Rules:

- Boost cannot bypass trust/safety gates.
- Recommendations must be explainable.
- New workers get controlled fair exposure.
- Risky providers are suppressed.

---

## 10. AI Growth Surfaces

AI can reduce friction in growth loops.

Use cases:

- Request writing
- Category suggestion
- Budget guidance
- Natural language search
- Support assistant
- Moderation assist
- SEO draft assistant
- Business profile improvement suggestions

Rules:

- AI suggestions editable.
- No irreversible AI-only decisions.
- No private messages for ad targeting.
- Avoid sending unnecessary sensitive data.
- Cost caps and rate limits required.

---

## 11. Marketplace Analytics

Funnel events:

```text
signup_started
signup_completed
request_draft_created
request_published
quote_received
quote_viewed
quote_accepted
payment_initiated
payment_authorized
work_started
work_delivered
delivery_approved
review_submitted
dispute_opened
```

Marketplace metrics:

- Request-to-offer conversion
- Time to first offer
- Offer-to-acceptance conversion
- Payment completion rate
- Delivery approval rate
- Repeat requester rate
- Worker/business earnings
- Business subscription conversion
- Dispute rate
- Review completion rate
- Trust score growth

---

## 12. Business Metrics

Track:

- GMV
- Net revenue
- Commission revenue
- Subscription MRR
- Boost/ad revenue later
- Average job value
- Take rate
- Refund rate
- Churn
- CAC later
- LTV later

---

## 13. Trust Metrics

Track:

- Report rate
- Dispute rate
- Moderation action rate
- Average rating
- Review completion rate
- Trust score distribution
- Badge distribution
- Cancellation rate
- On-time delivery rate
- Response speed

---

## 14. Supply Growth Strategy

Initial supply categories:

- Cleaning
- Repair/renovation
- Moving/delivery
- Private lessons
- Pet services
- Beauty at home
- Tech support
- Events

Supply quality levers:

- Verification
- Profile completeness
- Portfolio
- Fast first jobs
- Fair exposure for new workers
- No pay-to-quote
- Badges
- Earnings transparency
- Business subscription value

---

## 15. Demand Growth Strategy

Demand acquisition:

- SEO city/category pages
- Public provider/business profiles
- Referral later
- Local community feed
- Work proof sharing
- Social sharing
- Category guide content
- AI-assisted request creation

Demand retention:

- Fast first offer
- Trusted quote comparison
- Saved providers
- Repeat jobs
- Recurring jobs later
- Reliable support
- Clear refund/dispute process
- Personalized recommendations

---

## 16. Recommendation Ethics

Rules:

- Do not hide organic quality behind paid placement.
- Do not recommend unsafe providers because of revenue.
- Explain recommendations.
- Give new workers fair controlled exposure.
- Avoid reinforcing only early winners.
- Respect blocked users and negative feedback.
- Do not use sensitive private data for ad targeting.
- Let users adjust preferences.

---

## 17. Experimentation

Use feature flags.

Experiment examples:

- Budget guidance copy
- Quote comparison layout
- Provider ranking explanation
- Business subscription trial length
- Boost label copy
- Create flow order
- AI search assistant placement
- SEO page templates

Rules:

- Never test away safety/legal requirements.
- Payment/dispute flows require extra caution.
- No dark-pattern experiments.
- Track impact on trust and completion, not only clicks.

---

## 18. Cost Awareness

Major cost drivers:

- ECS/EKS compute
- RDS PostgreSQL
- MSK Kafka
- OpenSearch
- Cassandra
- CloudFront/S3 media traffic
- SMS OTP via Netgsm
- Email via SES
- Google Maps usage
- AI API usage
- Observability/log volume

Controls:

- Budgets and alerts
- Rate limits
- Geocoding cache
- Media size limits
- S3 lifecycle policies
- Log retention/sampling
- AI usage caps
- SMS fraud prevention
- Autoscaling
- Right-sized environments

---

## 19. SEO Admin Operations

Admin should manage:

- Category tree
- SEO slugs
- City/category metadata
- FAQ blocks
- Service guide content
- Public page visibility
- Canonical strategy
- Structured data configuration
- Localization fields

Rules:

- SEO changes audited if important.
- Low-quality pages can be noindexed.
- Private data never inserted into public content.

---

## 20. Growth Quality Gates

A growth feature is ready only when:

- It does not damage trust.
- It respects privacy.
- It is measurable.
- It has clear user value.
- It does not create dark patterns.
- It avoids sensitive targeting.
- It has cost controls if AI/SMS/maps/media involved.
- It does not bypass safety gates.
- It supports marketplace liquidity.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
