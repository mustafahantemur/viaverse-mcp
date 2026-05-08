# GAMIFICATION_AND_TRUST.md — Viaverse Gamification and Trust Model

This document defines reviews, trust score, badges, reputation, risk signals, and gamification principles.

Trust is one of Viaverse's core product advantages.

---

## 1. Trust Principles

Viaverse trust principles:

- Trust must be earned through real behavior.
- Reviews should be linked to completed jobs.
- Badges should reflect meaningful signals.
- Trust score should be explainable.
- Gamification must improve quality, not create fake points.
- Both requester and worker/business behavior matters.
- Reports, disputes and cancellations affect trust.
- New workers need fair exposure without compromising safety.

---

## 2. Trust Subjects

Trust can apply to:

```text
Account
WorkerProfile
BusinessProfile
Requester behavior
```

Subject types:

```text
ACCOUNT
WORKER_PROFILE
BUSINESS
REQUESTER
```

---

## 3. Review Model

Review entity:

```text
Review
  id
  jobId
  reviewerAccountId
  revieweeAccountId?
  revieweeBusinessId?
  role
  rating
  text
  tags
  status
  createdAt
```

Review roles:

```text
REQUESTER_TO_WORKER
REQUESTER_TO_BUSINESS
WORKER_TO_REQUESTER
BUSINESS_TO_REQUESTER
```

Rules:

- Reviews only after completed jobs.
- One review per side per job.
- Review must link to real completed work.
- Abusive review can be moderated.
- Review visibility policy should be fair.
- Review edits may be time-limited.
- Deleted accounts may anonymize reviews by policy.

---

## 4. Review Tags

Requester reviewing worker/business:

- On time
- Professional
- Clean work
- Good communication
- Fair price
- Would hire again
- Problem solved
- Late
- Poor communication
- Unclear pricing

Worker/business reviewing requester:

- Clear brief
- Respectful
- Fast approval
- Paid safely
- Good communication
- Unclear request
- Late response
- Scope changed
- Difficult communication

Tags should help trust scoring and UX summaries.

---

## 5. Trust Score

TrustScore entity:

```text
TrustScore
  subjectType
  subjectId
  score
  components
  updatedAt
```

Potential components:

- Average rating
- Review count
- Completed jobs
- Similar completed jobs
- Response speed
- On-time delivery
- Cancellation rate
- Dispute rate
- Report rate
- Verification status
- Payment reliability
- Locality score
- Review quality
- Badge count/quality

Rules:

- Trust score should be explainable.
- Trust score is not only average rating.
- Low sample size should be handled carefully.
- New users should not be unfairly buried forever.
- Serious reports/disputes may apply risk penalty.

---

## 6. Trust Score UX

Show explanations such as:

- “Completed 42 similar jobs”
- “Usually responds within 15 minutes”
- “Verified business”
- “Highly rated in your area”
- “On-time delivery badge”
- “New provider with complete profile”
- “Local favorite”

Avoid:

- Opaque numbers without explanation
- Publicly shaming risk labels
- Showing internal fraud reasons to other users

---

## 7. Badge Model

### BadgeDefinition

```text
BadgeDefinition
  id
  code
  title
  description
  subjectType
  criteriaVersion
  active
```

### AwardedBadge

```text
AwardedBadge
  id
  badgeCode
  subjectType
  subjectId
  awardedAt
  revokedAt?
  reason
```

Rules:

- Badges are earned from real behavior.
- Badge rules are versioned.
- Badges can be revoked.
- Badges should not be too easy or meaningless.
- Badges must be readable in UI.

---

## 8. Worker / Business Badges

Initial candidates:

- Verified Identity
- Verified Business
- Fast Responder
- On-Time Delivery
- Top Rated
- Experienced
- Local Favorite
- Super Worker
- New Talent
- Great Communication
- Repeat Customer Favorite

Example criteria:

```text
Fast Responder
  response median under threshold
  minimum completed quotes/jobs
  low report rate

On-Time Delivery
  high delivery-on-time ratio
  minimum completed jobs
  low dispute rate

Local Favorite
  high rating in area
  repeat customers
  completed local jobs
```

---

## 9. Requester Badges

Requester behavior matters.

Candidate badges:

- Clear Brief
- Fast Approver
- Reliable Payer
- Respectful Communicator
- Trusted Requester
- Community Contributor
- Repeat Requester

Why:

- Workers should trust requesters too.
- Good requester behavior improves marketplace quality.
- Two-sided trust reduces abuse.

---

## 10. Gamification Activity

GamificationActivityLog:

```text
GamificationActivityLog
  id
  subjectType
  subjectId
  activityType
  sourceEventId
  points? optional internal
  metadata
  occurredAt
```

Activity examples:

- Job completed
- Review submitted
- Badge awarded
- Work proof shared
- Fast response achieved
- Delivery approved
- Report validated
- Dispute lost/won
- Cancellation occurred

Rules:

- Activity log may be stored in Cassandra for scale.
- Points, if used, should be internal or carefully designed.
- Avoid meaningless public points.

---

## 11. Risk Signals

RiskSignal:

```text
RiskSignal
  id
  accountId?
  businessId?
  signalType
  severity
  source
  metadata
  createdAt
```

Signals:

- Too many OTP attempts
- Quote spam
- Repeated cancellations
- High dispute rate
- Reports
- Payment failures
- Contact-sharing bypass attempts
- Duplicate accounts
- Suspicious device/IP patterns
- Abusive messages
- Media moderation failure

RiskScore:

```text
RiskScore
  subjectType
  subjectId
  level
  score
  reasons
  updatedAt
```

Risk levels:

```text
LOW
MEDIUM
HIGH
BLOCKED
```

Rules:

- Risk does not automatically ban users unless policy allows.
- High-risk actions may require step-up auth or admin review.
- Risk reasons are admin-visible, not publicly exposed.

---

## 12. New Worker Fairness

Challenge:

- If ranking only rewards completed jobs, new workers never get work.

Solution:

- Controlled exploration slots
- New Talent badge
- Profile completeness boost
- Verification boost
- Lower-risk categories first
- Budget-fit opportunities
- Limited exposure to suitable requests

Rules:

- New worker fairness must not override safety.
- Boosting new workers should be explainable.
- Poor behavior quickly reduces exposure.

---

## 13. Work Proof

Work proof links social layer with real marketplace activity.

Flow:

```text
Job completed
-> Worker/business chooses Share work proof
-> Adds before/after media and caption
-> Requester privacy/consent policy checked
-> Media moderated
-> Feed post published
```

Rules:

- Protect requester privacy.
- Hide exact location.
- Sensitive media requires careful moderation.
- Work proof can link to category/provider profile.
- Work proof improves trust and SEO/social discovery.

---

## 14. Trust Events

Events:

```text
ReviewRequested
ReviewSubmitted
BothReviewsCompleted
TrustScoreRecalculated
BadgeEligibilityChanged
BadgeAwarded
BadgeRevoked
LocalityScoreUpdated
RiskSignalCreated
RiskScoreUpdated
WorkProofPublished
```

Consumers:

- search-service
- notification-service
- marketplace-service projections
- feed projection
- analytics later

---

## 15. Moderation Impact on Trust

Moderation outcomes may affect trust.

Examples:

- Validated harassment report reduces trust/risk score.
- Repeated spam quotes reduce offer exposure.
- NSFW media rejection creates risk signal.
- False report patterns create requester risk signal.
- Severe policy violation suspends capability.

Rules:

- Serious enforcement requires audit.
- Appeals should exist for severe decisions.
- AI can assist but not be sole authority for serious enforcement.

---

## 16. Trust in Matching

Ranking may use:

- Trust score
- Completed similar jobs
- Response speed
- Badges
- Verification
- Locality score
- Cancellation/dispute penalties
- Risk penalties

Rule:

> Boost/subscription signal can apply only after trust, safety, risk and relevance gates.

---

## 17. Trust Quality Gates

A trust feature is ready only when:

- It is based on real behavior.
- It is explainable.
- It is not easily gamed.
- It respects privacy.
- It has moderation path.
- It handles new users fairly.
- It supports two-sided trust.
- It updates search/matching where appropriate.
- It is tested against abuse cases.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
