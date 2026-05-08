# MONETIZATION_MODEL.md — Viaverse Monetization Model

This document defines Viaverse revenue strategy.

The monetization model must keep Viaverse sustainable without becoming hostile to workers, requesters, or businesses.

---

## 1. Monetization Principles

Viaverse monetization principles:

- Earn when real work succeeds.
- Do not charge workers just to try to get work.
- Do not sell low-quality leads.
- Do not force ads into critical flows.
- Do not let paid placement bypass trust.
- Keep requesters' experience simple.
- Make business subscription valuable through tools, analytics and visibility.
- Keep pricing configurable through admin.

Core principle:

> Viaverse earns when real work happens, not when workers merely attempt to get work.

---

## 2. Forbidden Monetization

Do not implement:

- Pay-to-quote
- Lead-credit burn
- Forced video ads
- Watch-to-continue ads
- Blocking interstitials
- Ads in payment/job/safety flows
- Hidden fees
- Paid placement that bypasses trust/safety
- Subscription required for basic worker access
- Charging workers before value is created

---

## 3. Revenue Streams

Initial:

```text
1. Success commission
2. Business subscription
```

Future:

```text
3. Optional individual worker Pro features
4. Boost / promoted visibility
5. Sponsored placements
6. Business analytics upgrades
7. Category sponsorships
```

---

## 4. Success Commission

Initial rate:

```text
5%
```

Applies to:

- Completed paid jobs
- After delivery approval/completion
- Subject to refund/dispute rules

Rules:

- Commission is transparent.
- Worker/business sees expected net.
- Commission is not charged for sending offers.
- Commission may be reversed/adjusted on refund/dispute.
- Commission rate admin-configurable later.

Example:

```text
Completed job value: 2,000 TL
Commission: 5% = 100 TL
Gross amount: 2,000 TL
Platform commission: 100 TL
Worker/business amount before provider/tax handling: 1,900 TL
```

Accounting/legal review required for final tax/provider-fee handling.

---

## 5. Business Subscription

Initial plan:

```text
500 TL/month
First month free
Admin-configurable
```

Target:

- Formal businesses
- Service teams
- Local shops
- Professional providers with business presence

Potential benefits:

- Advanced business profile
- More portfolio media
- Business badge
- Service packages
- Team management
- Operating hours
- Coverage area tools
- Analytics
- Customer inquiry management
- Promotions/boost tools
- Priority support later

Rules:

- Basic business access remains available.
- Subscription is not pay-to-quote.
- Subscription cannot guarantee jobs.
- Subscription cannot bypass trust, verification or moderation.
- Trial, price and plan rules configurable by admin.

---

## 6. Individual Worker Pro — Future

Not initial launch unless needed.

Potential features:

- Advanced analytics
- Profile improvement recommendations
- Portfolio expansion
- Calendar optimization
- Faster insights
- Saved response templates
- Advanced availability tools

Rules:

- Basic work access stays free.
- Sending offers remains free.
- Pro features improve tools, not basic fairness.
- Do not create a paywall that kills supply growth.

---

## 7. Boost / Promoted Visibility

Future monetization.

Allowed formats:

- Promoted provider/business in search
- Sponsored feed card
- Category sponsorship
- Local campaign card
- Boosted visibility for area/category/time window

Rules:

- Clearly labeled.
- User can hide/report irrelevant ads.
- No forced viewing.
- No blocking flows.
- No payment/job/safety interruption.
- Boost cannot bypass safety/trust/risk gates.
- Low-quality/risky providers cannot buy top placement.

Ranking rule:

```text
finalRank = organicQualityRank + eligibleBoostSignal - riskPenalty

if safetyGateFailed:
  not eligible for promotion
```

---

## 8. Sponsored Content Boundaries

Allowed targeting signals:

- City/region
- General category interest
- Public business category
- Recent search category
- Language
- Non-sensitive app context

Forbidden targeting signals:

- Private messages
- Payment details
- Verification documents
- Dispute evidence
- Raw exact address
- Private support content
- Sensitive categories where policy restricts targeting

---

## 9. Ads UX Rules

Ads must be:

- Non-intrusive
- Clearly labeled
- Skippable/ignorable
- Relevant
- Trust-gated
- Reportable/hideable

Ads must not appear:

- During payment confirmation
- During refund/dispute flow
- During safety/report flow
- During OTP/auth critical flow
- In a way that blocks job completion
- As forced video

---

## 10. Marketplace Health Before Monetization

Do not over-monetize before liquidity.

Priority order:

1. Trust
2. Supply quality
3. Demand quality
4. Completed jobs
5. Repeat usage
6. Reviews/badges
7. Subscription value
8. Boost/ads

If monetization damages trust or liquidity, it is too early or wrong.

---

## 11. Business Subscription Value Metrics

Track:

- Profile views
- Search impressions
- Request inquiries
- Offer views
- Offer acceptance rate
- Completed jobs
- Revenue from platform
- Response time
- Reviews
- Badges
- Repeat customers
- Service area performance

Business dashboard should answer:

> Is Viaverse bringing value to my business?

---

## 12. Commission Metrics

Track:

- GMV
- Completed paid job volume
- Average job value
- Commission revenue
- Refund rate
- Dispute rate
- Take rate
- Payout volume
- Commission reversal
- Provider fee impact

---

## 13. Subscription Metrics

Track:

- Active business profiles
- Trial starts
- Trial conversion
- Active paid subscriptions
- Past due subscriptions
- Subscription churn
- MRR
- ARPA
- Feature usage by subscribed businesses

---

## 14. Boost / Ads Metrics

Track later:

- Sponsored impressions
- Sponsored clicks
- Conversion to request
- Conversion to job
- Cost per completed job
- Hide/report rate
- Trust impact
- Organic vs sponsored performance
- User satisfaction

---

## 15. Revenue Scenarios

These are planning assumptions, not guarantees.

Base assumptions:

```text
Commission: 5%
Business subscription: 500 TL/month
First month free
Average job value assumption: 1,250 TL base case
Turkey nationwide first
Growth starts strongest in major metros
Boost/ads later-stage upside
```

Formula:

```text
Commission revenue = completed paid job volume * average job value * 5%

Subscription revenue = active paying businesses * 500 TL/month

Boost/ad revenue = later-stage upside
```

---

## 16. Yearly Scenario Direction

Conservative:

- Slow trust-building
- Lower request conversion
- Lower supply density
- Subscription slower

Base:

- Strong major metro adoption
- SEO begins compounding
- Good supply acquisition
- Healthy repeat usage

Aggressive:

- Strong supply/demand liquidity
- Viral work proof
- High SEO performance
- Business adoption strong
- Repeat jobs strong

Financial model should be updated with real metrics after launch.

---

## 17. Admin Configuration

Admin should configure:

- Commission rate
- Category-specific commission later
- Business subscription plan
- Trial length
- Promotions
- Boost eligibility
- Sponsored placement rules
- Free periods
- Launch campaigns

Rules:

- Monetization config changes audited.
- Dangerous changes require high permission.
- Pricing history retained.

---

## 18. User-Friendly Monetization Copy

Good copy direction:

- “Send offers for free.”
- “Viaverse only earns when work is completed successfully.”
- “Business subscription helps you manage and grow your profile.”
- “Sponsored placements are clearly labeled.”
- “Payments are processed securely through payment providers.”

Avoid:

- Confusing lead credit language
- Hidden fee language
- Aggressive upsell during critical flows
- Fear-based subscription prompts

---

## 19. Monetization Quality Gates

A monetization feature is allowed only if:

- It does not charge workers to send offers.
- It does not bypass trust/safety.
- It is transparent to users.
- It does not interrupt payment/job/safety flows.
- It is admin-configurable.
- It is measurable.
- It has audit logging where configuration or money changes.
- It has a user-friendly explanation.
- It does not damage marketplace liquidity.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
