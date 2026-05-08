# PRIVACY_AND_KVKK.md — Viaverse Privacy and KVKK Model

This document defines Viaverse privacy, consent, data rights, and KVKK/GDPR-style design requirements.

This is a product and engineering planning document, not legal advice. A qualified legal review is required before launch.

---

## 1. Privacy Principles

Viaverse privacy principles:

- Collect only what is needed.
- Explain why data is needed.
- Keep private requests private.
- Hide exact address until appropriate job state.
- Do not use private chat for advertising targeting.
- Do not expose raw sensitive data in logs/events.
- Give users privacy controls.
- Support data export and deletion/anonymization.
- Keep consent records versioned.
- Audit sensitive admin access.
- Design for KVKK/GDPR-style rights from day one.

---

## 2. Data Categories

### Public Data

May be visible publicly if approved and policy-compliant:

- Public business profile
- Public provider profile
- Public work proof
- Public reviews where policy allows
- Public category/SEO pages
- Public business service packages

### User-Controlled Data

Visible depending on settings/context:

- Display name
- Avatar
- Bio
- Service area
- Portfolio
- Badges
- Reviews
- Feed posts

### Private Data

Requires authorization:

- Email
- Phone
- Exact address
- Private service request details
- Private chat
- Payment method token references
- Dispute evidence
- Support tickets
- Verification status details

### Highly Sensitive / Secret Data

Never exposed to normal users/admins except strictly controlled systems:

- JWT signing keys
- OAuth secrets
- Payment provider secrets
- SMS/email credentials
- Database credentials
- Raw provider secrets

---

## 3. Consent Model

Consent record:

```text
ConsentRecord
  id
  accountId
  consentType
  version
  granted
  grantedAt
  revokedAt?
  ipHash
  locale
```

Consent types:

```text
TERMS
PRIVACY_POLICY
KVKK
DATA_PROCESSING
MARKETING
LOCATION
PUSH
COOKIES
AI_ASSIST_OPTIONAL
```

Rules:

- Required consents block relevant flows if not granted.
- Optional consents can be revoked.
- Consent version must be stored.
- New legal version may require re-consent.
- Marketing consent separate from operational notifications.

---

## 4. Onboarding Privacy Requirements

During onboarding, user should see:

- Terms of service
- Privacy policy
- KVKK clarification
- Required data processing consent
- Optional marketing consent
- Location permission explanation
- Notification permission explanation
- Language selection

Rules:

- Do not bury legal consent in unreadable UI.
- Required vs optional must be clear.
- The app should remain useful where optional consent is skipped, unless feature depends on it.

---

## 5. Location Privacy

Viaverse uses location for:

- Nearby services
- Matching
- Service area
- Map search
- Local feed relevance
- Public SEO city/category pages where applicable

Rules:

- Exact address hidden until appropriate job state.
- Public pages do not expose private exact address.
- Work proof should use approximate location if public.
- Users can control location visibility where possible.
- Admin access to exact location must be permissioned and audited.

---

## 6. Service Request Privacy

Service requests can contain private information.

Rules:

- Drafts are private.
- Published requests are visible only according to marketplace rules.
- Matched workers/businesses may see summarized relevant details.
- Exact address is hidden until accepted job/payment state where needed.
- Private service requests are not indexed in public SEO.
- Media attached to requests must respect visibility and moderation.
- Sensitive categories may require stricter privacy defaults.

---

## 7. Chat Privacy

Chat is private to participants, but Viaverse may need safety and support controls.

Rules:

- Only participants can read normal conversation.
- Admin/support access must be scoped and audited.
- Reports should use controlled excerpts or report packages.
- Private messages are not used for advertising targeting.
- Contact-sharing detection may process messages for safety.
- Future E2EE design must preserve abuse reporting path.

Before accepted job/payment:

- Detect phone/email/address/payment bypass attempts.
- Warn or restrict.

After accepted job:

- Allow job-relevant contact/address sharing.

---

## 8. Payment Privacy

Rules:

- Viaverse never stores raw card PAN/CVV.
- Saved cards are provider tokens.
- Admin cannot see raw card data.
- Payment provider secrets are stored in Secrets Manager.
- Payment logs/events do not include card data.
- Payment history visible only to authorized participants/admin payment role.
- Refund/payout actions audited.

---

## 9. Media Privacy

Media contexts:

- Profile photo
- Request image/video
- Work proof
- Business portfolio
- Feed post media
- Chat attachment
- Dispute evidence
- Verification document

Rules:

- Context determines visibility.
- Public visibility requires moderation.
- Private media requires authorization.
- EXIF should be stripped.
- Work proof must protect requester privacy.
- Verification documents are confidential.
- Dispute evidence is confidential and scoped.
- CDN access for private media must be signed/controlled.

---

## 10. Public Profile Privacy

Public profiles may include:

- Display name/business name
- Avatar/logo
- Service categories
- Service area
- Rating
- Badges
- Public reviews
- Portfolio
- Work proof
- Response speed summary

Public profiles must not expose:

- Exact private address
- Private phone/email unless user/business explicitly chooses public contact policy
- Private chat
- Payment data
- Verification documents
- Internal risk scores
- Admin notes

---

## 11. SEO Privacy

Public SEO pages:

- City/category pages
- Provider profiles
- Business profiles
- Service guides
- FAQ/blog pages

Rules:

- Private service requests are not indexed.
- Public pages use safe public fields.
- No exact private address.
- No private phone/email unless explicitly public.
- Canonical and sitemap generated carefully.
- Deleted/anonymized public profiles removed from index/projections.

---

## 12. Notification Privacy

Notification channels:

- In-app
- Push
- Email
- SMS

Rules:

- Push notifications should avoid sensitive details on lock screen.
- SMS reserved for auth/security/high-priority.
- Marketing notifications require consent.
- Users can manage preferences.
- Security/transactional notifications may be required by policy.
- Notification content should not reveal private dispute/payment details unnecessarily.

---

## 13. AI Privacy

AI may assist with:

- Request writing
- Category suggestion
- Budget guidance
- Search
- Support
- Moderation
- SEO drafts

Rules:

- Send minimal necessary data.
- Avoid sending private messages unless required for authorized support/moderation purpose.
- AI suggestions are editable.
- AI cannot make irreversible refund/dispute/suspension decisions alone.
- Private messages are not used for ad targeting.
- AI usage must respect user consent and policy.
- AI output is suggestion, not truth.

---

## 14. Analytics Privacy

Analytics should track product health without leaking private data.

Allowed:

- Event names
- IDs where needed
- Aggregated funnel data
- Category/location at safe granularity
- Device/app version
- Non-sensitive context

Avoid:

- Raw private messages
- Card data
- Exact addresses
- Verification document data
- Full dispute evidence
- Sensitive support content
- Secrets/tokens

Analytics taxonomy must be versioned.

---

## 15. Data Export

User can request data export.

Flow:

```text
User requests export
-> identity-service validates account
-> export job created
-> relevant services provide scoped data
-> export package generated
-> temporary signed download URL
-> user notified
-> export expires
```

Rules:

- Export only requesting user's data.
- Include clear sections.
- Do not include other users' private data beyond legally allowed/necessary context.
- Export files stored temporarily.
- Access must be authenticated or signed securely.

---

## 16. Account Deletion / Anonymization

Deletion flow:

```text
User requests deletion
-> confirm identity / step-up
-> pending deletion period
-> revoke sessions
-> disable capabilities
-> anonymize/delete personal data where possible
-> retain legally required financial/audit records
-> remove public profile/search projections
-> notify completion
```

Rules:

- Payment/legal records may need retention.
- Public content may be deleted or anonymized by policy.
- Reviews may be anonymized depending on policy.
- Search projections must update.
- Deletion must propagate to read models where required.

---

## 17. Admin Privacy

Admin access to personal data must be controlled.

Rules:

- Least privilege.
- Sensitive profile access audited.
- Admin action reason required for sensitive operations.
- No shared admin accounts.
- Admin cannot see raw card data.
- Support should see only necessary context.
- Dispute admins see scoped evidence.

Admin audit examples:

```text
AdminViewedSensitiveProfile
AdminOpenedDisputeEvidence
AdminChangedVerificationStatus
AdminIssuedRefund
AdminReleasedPayout
AdminModeratedContent
```

---

## 18. Data Retention

Initial direction:

- OTP challenges: short retention.
- Sessions: active lifetime plus security audit period.
- Payment records: legally required retention.
- Dispute records: safety/legal retention.
- Moderation records: safety/legal retention.
- Private messages: define with legal/privacy review.
- Public posts: until deletion/moderation policy applies.
- Deleted account data: anonymized where possible.

Legal review required.

---

## 19. Cookies and Web Tracking

For web/SEO pages:

- Cookie banner where legally required.
- Essential cookies separated from analytics/marketing.
- Consent for marketing/tracking cookies.
- Public SEO pages should avoid excessive tracking.
- Admin console cookies are functional/security-oriented.

---

## 20. KVKK Launch Checklist

Before launch:

- Terms of service drafted/reviewed.
- Privacy policy drafted/reviewed.
- KVKK clarification text drafted/reviewed.
- Cookie policy drafted/reviewed if web tracking used.
- Consent records implemented.
- Data export implemented or scheduled with clear policy.
- Account deletion/anonymization implemented.
- Admin access audited.
- Payment data processing reviewed.
- SMS/email provider data processing reviewed.
- AI provider data processing reviewed.
- Data retention policy reviewed.
- Incident response process defined.

---

## 21. Privacy Quality Gates

A feature is privacy-ready only when:

- Data collected is necessary.
- Visibility rules are clear.
- Consent need is known.
- Public/private distinction is clear.
- Logs/events do not leak sensitive data.
- Data export impact considered.
- Deletion/anonymization impact considered.
- Admin access scoped.
- SEO indexing impact checked.
- AI/analytics data usage checked.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
