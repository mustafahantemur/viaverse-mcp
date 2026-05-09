# PRODUCT_MODEL.md — Viaverse Product Model

This document defines what Viaverse is, who it serves, what capabilities it supports, and how the product should be described across design, engineering, and business discussions.

---

## 1. Product Definition

Viaverse is a trust-first local services and work network.

It is:

- A local services marketplace
- A worker/business opportunity platform
- A safe payment and job lifecycle system
- A reputation and trust network
- A local discovery and work proof feed
- An AI-assisted matching/search platform
- A business profile and subscription platform
- An SEO-ready public discovery ecosystem

It is not:

- A generic social media app
- A pure classified listings app
- A pay-to-quote lead marketplace
- A neighborhood-only community network
- A Firebase-style prototype
- A one-role customer/provider app

---

## 2. Product Thesis

People need help locally, but trust is fragmented.

Workers and businesses need work, but many platforms charge them before value is created.

Businesses need visibility, but social media is not designed for structured service discovery.

Local communities need interaction, but generic feeds are not connected to real work, payments, and reputation.

Viaverse solves this by combining:

- Structured service requests
- Smart matching
- Offer comparison
- Safe payments
- Job tracking
- Two-sided reviews
- Badges and trust scores
- Worker/business profiles
- Business subscriptions
- Local feed and work proof
- AI-assisted discovery and support
- SEO-ready public pages

---

## 3. Brand

### Name

Viaverse

### Pronunciation

`viyavors`, inspired by the sound of “vice versa” and “metaverse”.

### Meaning

```text
Via = through / by way of / connected through
Verse = network / environment / universe
```

Viaverse means:

> The trusted environment where people, businesses, local work, services, and social proof connect.

### Brand Positioning

> Viaverse is a trusted local services and work network where people can request help, do work, operate businesses, compare offers, pay safely, and build local reputation.

### Tagline

Primary:

> Trusted local work, safely done.

Alternatives:

- Describe the job. Compare trusted offers. Get it done.
- The trusted way to local services.
- Local work. Real trust.
- Request work, do work, build trust.

---

## 4. Product Pillars

### 4.1 Local Work Marketplace

Users can:

- Request work
- Add description, budget, time, location, media
- Receive offers
- Compare price, trust, timing and provider quality
- Accept an offer
- Pay safely
- Track the job
- Approve delivery
- Review both sides

### 4.2 Worker and Business Opportunity

Individuals and businesses can:

- Enable work capability
- Create profiles
- Select categories
- Define service areas
- Set availability
- Receive matched work opportunities
- Send offers for free
- Complete jobs
- Earn money
- Build reviews and badges

### 4.3 Social Trust Layer

The feed supports:

- Work proof
- Local recommendations
- Business updates
- Community questions
- Announcements
- Events
- Local help

The feed exists to support trust and discovery. It must not bury the marketplace.

### 4.4 Safe Payments and Disputes

Viaverse supports:

- Tokenized provider payments
- No raw card storage
- Payment sessions
- Refunds
- Payout blocking during disputes
- Commission after completed paid work
- Audit logs
- Admin payment operations

### 4.5 Smart Matching and AI Assistance

Viaverse helps users:

- Create better requests
- Select categories
- Understand budget ranges
- Find relevant providers/businesses
- Search naturally
- Get support
- Discover trusted local services

AI assists, but users/admins/policies decide.

### 4.6 Scalable Platform Infrastructure

The product is built from the beginning as:

- Multi-platform
- Event-driven
- Search-ready
- Moderation-ready
- Payment-safe
- SEO-aware
- AWS-native
- Provider-abstraction based

---

## 5. Core Product Principle

```text
One account.
Multiple capabilities.
Marketplace-first.
Social trust layer.
Safe payments.
Smart matching.
Scalable architecture.
```

---

## 6. Account and Capability Model

Viaverse should not force users into rigid permanent identities like “customer” and “provider”.

Instead:

```text
Account
  ├── REQUEST_WORK
  ├── DO_WORK_INDIVIDUALLY
  ├── OPERATE_BUSINESS
  ├── SOCIAL_PARTICIPATION
  └── ADMIN_* capabilities
```

A person may:

- Request cleaning today
- Do courier work tomorrow
- Create a business profile later
- Share work proof after completing jobs
- Participate in the feed

These are capabilities, not separate accounts.

Important distinction:

- `REQUEST_WORK` is the default personal marketplace capability.
- `DO_WORK_INDIVIDUALLY` is a lightweight individual earning capability on the same Account.
- `OPERATE_BUSINESS` enables a separate business workspace/profile attached to the same Account, not a third casual user mode.

The transition between requesting work and doing individual work should feel like an Airbnb-style “become a host” path: the user keeps the same identity, completes worker onboarding, and can move between personal/requester and individual worker surfaces.

Business is different. A business flow creates and manages a BusinessAccount/business profile with merchant onboarding, verification, catalog, team/staff roles, subscription/billing, and publish-ready checks. BusinessAccount is not a separate login identity, but it is a separate business resource with its own state and policies.

---

## 7. User-Facing Modes

### 7.1 Personal Mode

Default experience for:

- Exploring
- Requesting services
- Managing profile
- Paying
- Reviewing
- Social participation

### 7.2 Work Mode

For individuals who want to earn.

Work Mode is for non-business individual service providers. It should be easy to discover from Personal Mode and easy to enter after required onboarding, while still enforcing eligibility, safety and category-specific verification.

Features:

- Worker onboarding
- Category selection
- Service area
- Availability
- Matched requests
- Offer sending
- Active jobs
- Earnings
- Reviews
- Badges

### 7.3 Business Mode

Professional workspace for businesses.

Business Mode must not be treated as the same flow as Work Mode. It is an operator workspace for a business profile and may require business verification, staff permissions, service catalog setup, subscription state and publish-ready policy before public visibility or lead routing.

Features:

- Business profile
- Verification
- Services
- Packages
- Operating hours
- Team
- Portfolio
- Reviews
- Badges
- Subscription
- Analytics
- Promotions later

Business Mode may later become a separate Viaverse Business app, but initially it should exist inside the main app.

### 7.4 Admin Mode

Separate operations console.

Features:

- Accounts
- Workers
- Businesses
- Verification
- Requests
- Quotes
- Jobs
- Payments
- Refunds
- Disputes
- Reports
- Moderation
- Categories
- Badges
- Subscriptions
- Promotions
- Support
- Analytics
- Audit logs

---

## 8. Navigation Model

Recommended mobile bottom navigation:

```text
Explore
Jobs
Create
Inbox
Profile
```

### Explore

Discovery surface for:

- Feed
- Services
- Businesses
- Work proof
- Local updates
- Recommendations

### Jobs

Operational work hub.

Requester view:

- My requests
- Incoming offers
- Active jobs
- Completed jobs
- Disputes

Worker/business view:

- Matched requests
- Sent offers
- Active jobs
- Earnings
- Availability
- Reviews

### Create

Central action.

Create sheet options:

- Request a service
- Ask for local help
- Share a local update
- Share work proof
- Post as a business
- Create package/service offer

### Inbox

- Chats
- Offer conversations
- Job conversations
- Support conversations
- Notifications

### Profile

- Personal profile
- Worker profile
- Business profiles
- Wallet
- Settings
- Privacy
- Verification
- Badges
- Reviews

---

## 9. Request Work Model

### 9.1 Request Creation Fields

A service request may include:

- Title
- Description
- Category
- Subcategory
- Budget mode
- Budget range
- Urgency
- Preferred time
- Location
- Media
- Visibility/matching preference
- Special requirements

### 9.2 Budget Modes

```text
RANGE
OPEN_TO_OFFERS
NEED_GUIDANCE
URGENT_FLEXIBLE
```

User-facing labels:

- I have a budget range
- I am open to offers
- I need price guidance
- Urgent job, price is flexible

### 9.3 Budget Rules

- Budget helps requesters set expectations.
- Budget helps providers judge fit.
- Budget helps matching rank relevant providers.
- AI can suggest a local price range.
- Guidance is not a fixed price.
- Providers can still quote independently.
- Providers should see a range or budget level, not a manipulative exact maximum.

---

## 10. Offer / Quote Model

### 10.1 Quote Fields

- Price
- Currency
- Estimated start
- Estimated duration
- Message
- Included services
- Excluded services
- Required materials
- Revision note
- Expiration time

### 10.2 Quote Statuses

```text
DRAFT
SENT
QUESTIONED
REVISION_REQUESTED
REVISED
ACCEPTED
REJECTED
WITHDRAWN
EXPIRED
```

### 10.3 Quote Rules

- Sending offers is free.
- No pay-to-quote.
- No lead credits.
- Workers cannot see other workers' private quotes.
- Requesters can compare offers.
- Quote revisions are tracked.

---

## 11. Job Lifecycle

```text
Request draft
Published
Collecting offers
Quote selected
Payment pending
Payment authorized
Scheduled
In progress
Delivered
Approved
Completed
Cancelled
Disputed
Refunded
```

End-to-end flow:

```text
Requester publishes request
-> Workers/businesses send offers
-> Requester selects offer
-> Payment session starts
-> Payment authorized or collected
-> Job scheduled
-> Worker starts work
-> Worker marks delivered
-> Requester approves
-> Payment released / commission applied
-> Both sides review
-> Badges and trust signals update
```

---

## 12. Worker Model

An individual worker can:

- Enable Work Mode
- Create worker profile
- Select categories
- Set service area
- Set availability
- Add portfolio
- Receive matched requests
- Send offers
- Manage active jobs
- Track earnings
- Receive reviews
- Earn badges

Worker profile signals:

- Rating
- Completed jobs
- Response speed
- On-time delivery
- Badges
- Verification
- Service categories
- Service area
- Portfolio
- Work proof

---

## 13. Business Model

A business can:

- Create a business profile
- Verify business identity
- Add services
- Add packages
- Set operating hours
- Set service area
- Add team members
- Add portfolio
- Receive requests
- Send offers
- Track analytics
- Subscribe
- Promote later

Business subscription initial plan:

- 500 TL/month
- First month free
- Admin-configurable

Business subscription must not become pay-to-quote.

---

## 14. Social Feed Model

The feed supports:

- Work proof
- Local recommendations
- Help requests
- Announcements
- Business updates
- Events
- Questions
- Sponsored cards later

Feed ranking signals:

- Location proximity
- Category interests
- Saved/followed providers
- Trust score of author
- Freshness
- Engagement
- Media quality
- Moderation score
- Relationship to past jobs

Feed rules:

- No NSFW
- No harassment
- No spam
- Report/block available
- Moderation queue
- Sponsored content clearly labeled
- Work proof can link to completed jobs

---

## 15. Chat Model

Chat is a commerce workflow, not generic messaging.

Chat should support:

- Job/quote context card
- Quote card
- Schedule proposal
- Payment status
- Quick replies
- Attachments
- AI suggested replies
- Safety notices
- Report/block
- Support escalation

Before accepted job/payment:

- Detect phone/email/address sharing
- Warn or restrict depending on policy
- Create risk signal for repeated bypass attempts

After accepted job:

- Allow job-relevant contact/address sharing

Future:

- E2EE-ready architecture
- No custom crypto
- Matrix/Vodozemac evaluation path
- Avoid libsignal as default

---

## 16. Payments and Wallet

Viaverse never stores raw card data.

Supported direction:

- iyzico primary for Turkey
- Stripe adapter included for global expansion
- Masterpass adapter-ready
- Apple Pay / Google Pay through provider-supported flows

Wallet:

- Saved cards through provider tokens
- Payment history
- Refunds
- Payout account
- Payout history
- Business subscription billing
- Invoices/receipts

Payment rules:

- No PAN/CVV storage
- Idempotent provider callbacks
- Refund support
- Dispute-aware payout blocking
- Commission after completed paid job

---

## 17. Reviews, Trust and Badges

### Reviews

- Requester reviews worker/business
- Worker/business reviews requester
- Only after completed jobs
- One review per side per job

### Worker / Business Badges

- Verified Identity
- Verified Business
- Fast Responder
- On-Time Delivery
- Top Rated
- Experienced
- Local Favorite
- Super Worker
- New Talent

### Requester Badges

- Clear Brief
- Fast Approver
- Reliable Payer
- Respectful Communicator
- Trusted Requester
- Community Contributor

Trust score may include:

- Rating
- Completed jobs
- Response speed
- Dispute rate
- Cancellation rate
- Verification
- Review quality
- Report history
- Locality score
- Payment reliability

---

## 18. Monetization Model

Primary revenue:

- 5% success commission after completed paid work
- Business subscription: 500 TL/month, first month free

Future revenue:

- Optional worker/business Pro features
- Boost/promoted visibility
- Non-intrusive sponsored placements

Forbidden:

- Pay-to-quote
- Lead-credit burn
- Forced ads
- Watch-to-continue ads
- Blocking payment/job/safety flows with ads

---

## 19. SEO and Public Discovery

Public surfaces:

- City/category pages
- Provider public profiles
- Business public profiles
- Service category guides
- FAQ pages
- Blog/content pages

Rules:

- Next.js SSR/SSG
- schema.org structured data
- Canonical URLs
- Sitemap
- OpenGraph
- Turkish and English localization
- Private service requests not indexed

---

## 20. AI Product Surfaces

AI can help with:

- Request writing
- Category suggestion
- Budget guidance
- Search/filter assistant
- Support assistant
- Moderation assist
- Fraud/risk assist
- SEO/content assist

AI rules:

- AI suggests; user/admin/policy decides.
- User can edit AI suggestions.
- No irreversible AI-only decisions.
- Do not use private messages for ad targeting.
- Do not send unnecessary sensitive data to AI providers.

---

## 21. Core Product Quality Gates

- User can understand which mode they are in.
- User can create a request without confusion.
- Worker can send offers without paying.
- Requester can compare offers based on trust, not only price.
- Payment safety is clear.
- Chat safety is enforced.
- Reviews are linked to real completed jobs.
- Admin can moderate and audit.
- Public SEO pages do not leak private data.
- AI suggestions remain editable.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
