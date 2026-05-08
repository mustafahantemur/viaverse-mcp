# SECURITY_MODEL.md — Viaverse Security Model

This document defines the security model for Viaverse.

Security is not a later hardening task. It is part of the product foundation because Viaverse handles identity, local trust, payments, chat, media, moderation, reviews, business profiles, and admin operations.

---

## 1. Security Principles

Viaverse security principles:

- Deny by default
- One real account, multiple capabilities
- JWT subject must be the persisted `Account.id`
- Never trust the client
- Backend authorization required for every protected resource
- No raw card storage
- No secrets in code
- No sensitive data in logs/events
- Admin actions are audited
- Payment callbacks are idempotent
- Rate limiting on abuse-prone flows
- Privacy and consent are first-class
- AI can assist, but must not make irreversible sensitive decisions alone

---

## 2. Threat Model Summary

Viaverse must protect against:

- Fake accounts
- OTP abuse
- Credential stuffing
- Session theft
- Cross-account resource access
- Quote spam
- Payment fraud
- Refund/payout abuse
- Chat contact-sharing bypass
- Harassment and unsafe content
- NSFW/media abuse
- Review manipulation
- Business impersonation
- Admin account misuse
- Provider callback spoofing
- Search/SEO private data leakage
- Secret leakage
- Data export/deletion abuse
- DDoS and bot traffic

---

## 3. Identity Model

Core identity object:

```text
Account
  id: UUID
  status: ACTIVE | SUSPENDED | DELETED_PENDING | DELETED
  displayName
  primaryEmail?
  primaryPhone?
  locale
  country
  riskLevel
  tokenVersion
```

Rules:

- `Account.id` is the JWT subject.
- Do not use random user IDs in tokens.
- Account status is checked on authenticated requests.
- Deleted/suspended accounts cannot perform sensitive actions.
- Risk level may trigger step-up auth or restrictions.

---

## 4. Capability Model

Capabilities:

```text
REQUEST_WORK
DO_WORK_INDIVIDUALLY
OPERATE_BUSINESS
SOCIAL_PARTICIPATION
ADMIN_SUPPORT
ADMIN_VERIFICATION
ADMIN_PAYMENT
ADMIN_MODERATION
ADMIN_CATEGORY
ADMIN_ANALYTICS
ADMIN_SUPER
```

Rules:

- Capabilities are explicit.
- Capabilities can be enabled, pending, suspended, or revoked.
- Suspension of one capability does not automatically delete the account.
- Admin capabilities are internal only.
- Authorization checks must include capability status.

Example:

```text
A user with REQUEST_WORK can create service requests.
A user without DO_WORK_INDIVIDUALLY cannot send worker offers.
A business team manager cannot perform payment refunds unless they also have payment permission.
```

---

## 5. Authentication

Supported authentication methods:

- Phone OTP
- Email OTP
- Google OAuth
- Apple OAuth
- Meta/Instagram account linking where allowed by policy

Rules:

- OTP codes are never stored in plaintext.
- OTP targets should be normalized and hashed where possible.
- OTP attempts are rate-limited.
- OAuth identities are linked to accounts through verified provider subject.
- Duplicate account detection uses verified email/phone/provider identity.
- Account merge requires explicit confirmation.

---

## 6. OTP Security

OTP challenge model:

```text
OtpChallenge
  id
  channel: EMAIL | SMS
  targetHash
  purpose: SIGN_IN | VERIFY_CONTACT | STEP_UP | RECOVERY
  codeHash
  expiresAt
  attemptCount
  maxAttempts
  status
  ipHash
  deviceFingerprintHash?
```

Rules:

- Store only code hash.
- Use short expiration.
- Limit attempts.
- Rate-limit by target, account, IP, device, and purpose.
- Do not expose OTP in production logs.
- Local development may use safe local-only debugging through Mailpit/logging, never production.

---

## 7. Session and Token Model

### Access Token

JWT access token claims:

```text
sub = persisted Account.id
sid = Session.id
cap = active capabilities snapshot
roles = admin/user role snapshot if needed
iss = viaverse-identity
iat
exp
tokenVersion
```

Rules:

- Short-lived access tokens.
- Validate issuer and expiration.
- Validate account exists.
- Validate account status.
- Validate tokenVersion where needed.

### Refresh Token

Rules:

- Opaque token.
- Stored hashed.
- Rotated on use.
- Revocable.
- Refresh token family tracks reuse.
- Reuse detection revokes the token family.

### Session

```text
Session
  id
  accountId
  deviceId
  refreshTokenFamilyId
  createdAt
  lastSeenAt
  revokedAt?
  ipHash
  userAgentHash
  platform
```

---

## 8. Authorization Model

Authorization decision inputs:

```text
accountId
activeCapabilities
resourceType
resourceId
resourceOwnerId
businessRole
adminCapability
jobState
paymentState
riskLevel
requestedAction
```

Rules:

- Deny by default.
- Do not rely on frontend hiding buttons.
- Controllers do not own authorization logic.
- Application policy services make authorization decisions.
- Resource ownership is checked for every protected resource.
- Admin access is limited by role and audited.

---

## 9. Authorization Matrix

### Service Request

| Action | Allowed |
|---|---|
| Create draft | Account with `REQUEST_WORK` |
| Edit draft | Request owner |
| Publish | Request owner |
| Cancel | Request owner, subject to state |
| View published summary | Matched worker/business, admin where authorized |
| View exact private details | Only after allowed job state or by audited admin |
| Delete/private purge | Owner or admin policy process |

### Quote

| Action | Allowed |
|---|---|
| Create quote | Worker/business with eligible capability |
| Revise quote | Quote owner |
| Withdraw quote | Quote owner |
| View own quote | Quote owner |
| View quotes for request | Request owner |
| Accept quote | Request owner |
| View competitor quote | Not allowed |

### Job

| Action | Allowed |
|---|---|
| View job | Participants and authorized admin |
| Start work | Worker/business participant |
| Mark delivered | Worker/business participant |
| Approve delivery | Requester participant |
| Open dispute | Participant |
| Cancel | Participant depending on state/policy |
| Force state change | Authorized admin only, audited |

### Payment

| Action | Allowed |
|---|---|
| Initiate payment | Requester participant |
| View payment status | Participants |
| Refund | `ADMIN_PAYMENT`, audited |
| Release payout | `ADMIN_PAYMENT`, audited |
| Reconcile | `ADMIN_PAYMENT`, audited |
| View raw card | Never allowed |

### Messaging

| Action | Allowed |
|---|---|
| Send message | Conversation participant |
| Read message | Conversation participant |
| Attach media | Participant and context policy |
| Report message | Participant |
| Admin review | Audited and scoped, preferably through report package |

### Business

| Action | Allowed |
|---|---|
| Create business | Account with `OPERATE_BUSINESS` flow |
| Edit profile | Business owner/manager |
| Manage team | Business owner |
| Manage services/packages | Owner/manager |
| View payouts | Owner/finance role |
| Change subscription | Owner/authorized role |

---

## 10. Admin RBAC

Admin capabilities:

```text
ADMIN_SUPPORT
ADMIN_VERIFICATION
ADMIN_PAYMENT
ADMIN_MODERATION
ADMIN_CATEGORY
ADMIN_ANALYTICS
ADMIN_SUPER
```

Rules:

- Least privilege.
- No shared admin accounts.
- MFA/step-up required.
- Sensitive actions require reason.
- Admin access is audited.
- `ADMIN_SUPER` should be rare and monitored.

Audited admin actions:

- View sensitive profile
- Change verification status
- Issue refund
- Release payout
- Moderate content
- Suspend capability
- Change category
- Change monetization config
- Resolve dispute

---

## 11. Rate Limiting

Apply rate limits to:

- OTP request
- OTP verify
- Login
- Token refresh
- Search
- Request creation
- Quote sending
- Chat sending
- Media upload
- Report/block
- Payment session creation
- Provider callback processing
- Admin login

Technology:

- Valkey/Redis

Rate limit keys may include:

```text
accountId
ipHash
deviceFingerprint
phoneHash
emailHash
route
purpose
```

---

## 12. Payment Security

Rules:

- No PAN.
- No CVV.
- No raw card storage.
- Stored payment methods are provider tokens.
- Provider callbacks must be verified.
- Callback handling must be idempotent.
- Payment state transitions are audited.
- Refunds/payouts require admin payment permission.
- Payout blocked during dispute.
- Payment provider secrets stored in Secrets Manager.

Provider callback validation:

- Validate signature if provider supports it.
- Validate expected payment session.
- Validate amount/currency.
- Validate provider state.
- Deduplicate callback.
- Store callback record safely without secrets.

---

## 13. Chat Safety

Before accepted job/payment:

- Detect phone numbers.
- Detect email addresses.
- Detect address patterns.
- Detect external social handles.
- Detect payment bypass attempts.

Policy options:

- Warn
- Soft block
- Hard block
- Create risk signal
- Escalate if repeated

After accepted job:

- Allow job-relevant contact and address sharing.

Rules:

- Do not use private messages for ad targeting.
- Admin access to messages is controlled and audited.
- Reports should use controlled excerpts/report packages.
- Future E2EE must preserve abuse reporting path.
- No custom cryptography.

---

## 14. Media Security

Media pipeline:

```text
Upload requested
-> presigned S3 upload
-> upload confirmed
-> metadata validation
-> malware/virus scan where possible
-> EXIF stripping
-> thumbnail/transcode
-> NSFW/moderation scan
-> approved/rejected/quarantined
-> CDN delivery
```

Rules:

- Public visibility requires moderation status.
- Private media requires authorization or signed URL.
- Chat attachments are not public.
- Work proof must protect requester privacy.
- CDN URLs must not expose private media.
- Size/type limits required.
- Report media action required.

---

## 15. Moderation Security

Reportable objects:

- User
- Worker profile
- Business profile
- Service request
- Quote
- Chat message
- Feed post
- Comment
- Review
- Media

Moderation actions:

- Hide content
- Remove content
- Warn user
- Rate limit
- Suspend capability
- Suspend account
- Escalate to support
- Open dispute

Rules:

- AI can assist but serious enforcement requires policy/human decision.
- Admin moderation actions are audited.
- Appeals should exist for serious enforcement.

---

## 16. Secrets Management

Production secrets live in:

```text
AWS Secrets Manager
```

EKS later:

```text
External Secrets Operator
```

Secret groups:

- Database credentials
- Kafka/MSK credentials
- OpenSearch credentials
- iyzico keys
- Stripe keys
- OAuth client secrets
- JWT signing keys
- Netgsm credentials
- Amazon SES credentials
- S3 credentials if needed
- AI provider keys
- Admin bootstrap secret

Rules:

- No secrets committed to git.
- `.env` ignored.
- `.env.example` contains placeholders only.
- Rotate secrets periodically.
- Rotate immediately after suspected leak.

---

## 17. Logging and Privacy

Do not log:

- Access tokens
- Refresh tokens
- OTP codes
- Card numbers
- CVV
- Provider secrets
- Raw private messages
- Verification document contents
- Sensitive personal data unless explicitly required and protected

Do log safely:

- correlationId
- accountId where allowed
- eventId
- operation
- status
- duration
- safe error code
- provider reference ID where safe

---

## 18. Security Testing

Required tests:

- JWT subject/account existence
- Token expiration
- Refresh rotation
- Refresh reuse detection
- OTP rate limits
- Cross-account access denial
- Capability enforcement
- Admin RBAC
- Payment callback idempotency
- Refund permission
- Payout block during dispute
- Private request not indexed
- File upload authorization
- Chat contact-sharing detection
- Moderation/report authorization
- PII logging checks

---

## 19. Security Incident Runbooks

Required runbooks:

- Secret leak
- Payment callback failure
- Suspicious refund/payout
- OTP abuse
- Account takeover
- Admin account compromise
- Search private data leak
- Media abuse incident
- DDoS/bot spike
- Provider outage
- Data deletion request failure

---

## 20. Security Quality Gates

A feature is not security-ready unless:

- Authenticated access validated.
- Authorization policy implemented.
- Ownership checked.
- Rate limits considered.
- Sensitive data classification known.
- Logs/events safe.
- Admin actions audited.
- Payment actions idempotent if applicable.
- Tests cover denial cases.
- Privacy impact considered.


---

> Typed-domain addendum: all role/status/type/provider concepts must use enums, value objects, policies, or generated contract types. Raw business string comparisons are forbidden. Viaverse is greenfield; prototype screenshots are visual reference only.
