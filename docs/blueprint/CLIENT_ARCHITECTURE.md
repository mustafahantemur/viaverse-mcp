# CLIENT_ARCHITECTURE.md — Viaverse Client Architecture

This document defines the architecture for Viaverse client applications:

- KMP Compose mobile app
- React/Next.js web app
- React/Next.js admin console
- Shared design system and client rules

---

## 1. Client Goals

The Viaverse client experience must support:

- Simple onboarding
- Personal / Work / Business modes
- Explore and local discovery
- Service request creation
- Budget guidance
- Offer comparison
- Job workspace
- Chat
- Wallet and payments
- Reviews and badges
- Business dashboard
- Settings/privacy/security
- Admin operations
- SEO public discovery

The UI must feel warm, trustworthy, modern and scalable.

---

## 2. Client Applications

### 2.1 Mobile App

Technology:

- Kotlin Multiplatform
- Compose Multiplatform
- Ktor Client
- SQLDelight
- Koin
- Coil 3
- Coroutines / Flow
- Platform secure storage wrappers

Primary target:

- Android first
- iOS-ready shared architecture

### 2.2 Web / Landing / SEO

Technology:

- React
- TypeScript
- Next.js
- SSR/SSG
- SEO metadata
- schema.org structured data

Used for:

- Landing page
- City/category SEO pages
- Provider profiles
- Business profiles
- Service guides
- FAQ/blog content
- Web app surfaces if needed

### 2.3 Admin Console

Technology:

- React
- TypeScript
- Next.js
- Admin RBAC
- Operational tables
- Filters
- Audit views

Used for:

- Verification
- Moderation
- Disputes
- Payments
- Categories
- SEO
- Subscriptions
- Support
- Analytics
- Audit logs

---

## 3. Mobile App Module Structure

Target KMP structure:

```text
apps/mobile-kmp/
  composeApp/
    src/
      commonMain/
        kotlin/
          app/
          core/
            design/
            navigation/
            network/
            storage/
            auth/
            logging/
            error/
            analytics/
            permissions/
          feature/
            auth/
            onboarding/
            explore/
            request/
            work/
            business/
            quote/
            chat/
            payment/
            job/
            review/
            profile/
            settings/
      androidMain/
      iosMain/
```

---

## 4. Client Layering

Each feature should follow a consistent internal structure:

```text
feature/request/
  data/
    dto/
    repository/
    mapper/
  domain/
    model/
    usecase/
  presentation/
    screen/
    component/
    state/
    viewmodel/
```

Rules:

- UI does not call raw HTTP directly.
- ViewModels call use cases/repositories.
- Repositories use typed API clients.
- DTOs are mapped to client models.
- Business logic does not live inside composables.
- Preview data is separated from runtime data.

---

## 5. Navigation Architecture

Recommended mobile bottom navigation:

```text
Explore
Jobs
Create
Inbox
Profile
```

### Explore

- Feed
- Services
- Nearby
- Businesses
- Work proof
- Recommendations

### Jobs

Adapts to active capability.

Requester:

- My requests
- Incoming offers
- Active jobs
- Completed jobs
- Disputes

Worker/business:

- Matched requests
- Sent offers
- Active jobs
- Earnings
- Reviews
- Availability

### Create

Central V action opens create sheet:

- Request a service
- Ask for local help
- Share local update
- Share work proof
- Post as a business
- Create package/service offer

### Inbox

- Chats
- Job conversations
- Offer conversations
- Support
- Notifications

### Profile

- Personal profile
- Worker profile
- Business profile
- Wallet
- Settings
- Privacy
- Security
- Reviews
- Badges

---

## 6. App Modes

### Personal Mode

Default mode.

Contains:

- Explore
- Request work
- Profile
- Wallet
- Settings
- Feed participation

### Work Mode

Enabled after worker onboarding.

Work Mode is the lightweight individual-provider path from Personal Mode. It should feel like “become a host”: same login, same personal Account, additional individual work capability, and clear onboarding before opportunities/offers are enabled.

Contains:

- Matched requests
- Offer sending
- Active jobs
- Earnings
- Availability
- Worker profile
- Reviews and badges

### Business Mode

Enabled after business profile setup.

Business Mode is a separate business workspace attached to the same Account. It must use business onboarding, verification, staff/team permissions, catalog/subscription setup and publish-ready gates instead of the lightweight Work Mode transition.

Contains:

- Business dashboard
- Services
- Packages
- Team
- Hours
- Coverage area
- Subscription
- Analytics
- Business profile
- Promotions later

Mode switching must not create duplicate accounts.
Personal and Work surfaces can be switched ergonomically after capability enablement. Business surfaces may be entered from the same account, but business actions must be scoped to a selected BusinessAccount/business profile and authorized through business policy.

---

## 7. Design System

The app must use a custom Viaverse Design System.

### 7.1 Theme

```text
ViaverseTheme
  colors
  typography
  spacing
  radius
  elevation
  motion
  icons
```

### 7.2 Brand Colors

Light mode:

```text
Primary Action Orange: #F97316
Deep Text / Ink: #0F172A or #022C22
Trust Green: #064E3B / #10B981
Warm Ivory: #FFF7ED / #FFFBF5
Neutral Surface: #F9FAFB
```

Dark mode:

```text
Dark Green Background: #022C22
Elevated Surface: #052E2B
Trust Green Accent: #10B981
Primary CTA Orange: #F97316
Muted Text: #D1FAE5 / #E5E7EB
```

### 7.3 Core Components

- ViaverseButton
- ViaverseTextField
- ViaverseCard
- ViaverseSheet
- ViaverseTabs
- ViaverseChip
- ViaverseAvatar
- ViaverseBadge
- ViaverseBottomNav
- ViaverseSearchBar
- ViaverseQuoteCard
- ViaverseJobTimeline
- ViaverseTrustScore
- ViaversePaymentCard
- ViaverseSettingsGroup

Rules:

- Do not scatter raw Material components across screens.
- Wrap primitives into Viaverse components.
- No raw colors in feature screens.
- Use semantic colors.

---

## 8. Screen Inventory

### Auth / Onboarding

- Splash
- Welcome
- Login
- Phone/email OTP
- OAuth linking
- Consent
- Language
- Intent selection
- Location permission
- Notification permission
- Category interests

### Explore

- Explore home
- Nearby
- Services
- Work proof
- Feed post detail
- Comments
- Report/block

### Services/Search

- Category list
- Subcategory screen
- Provider list
- Business list
- Map search
- Provider detail
- Business detail
- Saved searches

### Create

- Create sheet
- Request wizard
- AI category suggestion
- Budget guidance
- Media upload
- Location/time
- Preview
- Publish success

### Jobs

- My requests
- Incoming offers
- Quote comparison
- Active job
- Delivery approval
- Review
- Dispute

### Work Mode

- Worker onboarding
- Opportunity queue
- Request detail
- Send offer
- Sent offers
- Active jobs
- Earnings
- Availability
- Worker profile

### Business Mode

- Business setup
- Dashboard
- Services
- Packages
- Hours
- Team
- Portfolio
- Analytics
- Subscription
- Promotions later

### Inbox

- Chat list
- Job chat
- Support chat
- Notifications

### Profile / Settings

- Personal profile
- Worker profile
- Business profile
- Wallet
- Saved cards
- Payment history
- Payouts
- Security
- Privacy/data
- Language/region
- Appearance
- Support/legal

---

## 9. State Management

Recommended approach:

- ViewModel per screen/flow
- Immutable UI state
- Coroutines/Flow
- Sealed UI events
- Explicit loading/error/success states
- No hidden global mutable state

Example state:

```kotlin
data class RequestCreateState(
    val step: RequestCreateStep,
    val title: String,
    val description: String,
    val categorySuggestion: CategorySuggestion?,
    val budgetMode: BudgetMode?,
    val budgetRange: BudgetRange?,
    val media: List<MediaDraft>,
    val isSubmitting: Boolean,
    val error: UiError?
)
```

---

## 10. Network Layer

Use Ktor Client.

Rules:

- Typed API clients
- Auth interceptor
- Refresh token handling
- Correlation ID header
- Error mapping to client error model
- Timeout configuration
- Retry only where safe
- No API calls directly from UI components

API package example:

```text
core/network/
  HttpClientFactory
  AuthInterceptor
  ApiErrorMapper
  ViaverseApiClient

feature/request/data/
  RequestApi
  RequestRepository
```

---

## 11. Local Storage

Use SQLDelight for local structured storage.

Use cases:

- Draft request
- Cached profile
- Recent searches
- Saved local UI preferences
- Offline-friendly read cache later

Secure storage for:

- Refresh token
- Device/session secrets
- Sensitive auth state

Rules:

- Access tokens may be memory only or short-lived.
- Refresh tokens stored securely.
- Do not store card data locally.
- Do not store unnecessary private chat content without explicit design.

---

## 12. Media Handling

Client supports:

- Image picker
- Video picker
- Camera capture
- Compression where appropriate
- Upload progress
- Retry
- Cancel upload
- Attachment preview
- Media moderation pending state

Upload flow:

```text
Client requests upload URL
-> media-service returns presigned URL
-> client uploads directly to S3
-> client confirms upload
-> media-service validates/moderates
```

---

## 13. Payment UX

Payment UI must be trust-centered.

Rules:

- Show final amount.
- Show commission/service fee transparently.
- Explain provider-tokenized saved cards.
- Explain Viaverse does not store raw card data.
- No ads in payment flow.
- Payment failure states must be clear.
- Refund/dispute status must be understandable.

---

## 14. Chat UX

Chat is job/quote-linked.

Required elements:

- Context card
- Quote card
- Payment status
- Schedule proposal
- Attachment button
- Safety notice
- Report/block
- Support escalation
- AI suggested replies later

Rules:

- Detect direct contact sharing before allowed state.
- Show warning clearly.
- Do not use private chat content for ads.
- Admin/support access is controlled and audited.

---

## 15. Accessibility

Requirements:

- Readable text sizes
- Sufficient contrast
- Touch targets large enough
- Screen reader labels
- Do not rely on color alone
- Focus states on web/admin
- Reduce motion support
- Clear form errors
- Accessible OTP input
- Accessible modals/sheets

---

## 16. Localization

Initial languages:

- Turkish
- English

Rules:

- All copy uses localization keys.
- No hardcoded strings.
- Currency/date/time formatted by locale.
- Text expansion supported.
- SEO pages localized.
- Admin category/public copy localized.

---

## 17. React / Next.js Architecture

Suggested structure:

```text
apps/web-next/
  app/
  components/
  features/
  lib/
  styles/
  public/

apps/admin-next/
  app/
  components/
  features/
  lib/
  styles/
```

Rules:

- Feature-based boundaries.
- Typed API clients.
- SSR/SSG for public pages.
- Admin requires auth.
- Admin RBAC enforced server-side.
- No private data in public pages.
- Public SEO pages include structured data.

---

## 18. Admin UI

Admin design is operational.

Required:

- Fast tables
- Filters
- Status labels
- Audit trails
- Reason-required mutations
- RBAC-aware views
- Payment/refund controls
- Verification queues
- Moderation queues
- Dispute queues

Admin must prioritize safety over visual flair.

---

## 19. Client Testing

Mobile:

- ViewModel tests
- Repository tests
- API fake tests
- Navigation flow tests
- Critical UI state tests
- Screenshot tests later

Web/admin:

- Component tests
- Page tests
- API contract tests
- Accessibility checks
- Admin RBAC tests
- SEO metadata tests

E2E:

- Login -> request -> quote -> payment -> delivery -> review
- Worker onboarding -> send quote -> active job
- Business onboarding -> subscription -> requests
- Dispute
- Admin moderation/payment

---

## 20. Client Quality Gates

A client task is complete only when:

- Screen uses design system components.
- State is explicit and testable.
- No business logic in UI.
- Errors/loading/empty states implemented.
- Authorization-sensitive actions rely on backend, not hidden UI.
- Localization keys used.
- Accessibility labels added.
- API errors mapped.
- Real backend integration or clearly marked fixture-only preview.
- UI works in light and dark mode.


---

# Canonical Addendum — Typed Domain Rules / No Hardcoded Business Strings

## Status

Accepted and mandatory.

## Greenfield Language Rule

Viaverse is a greenfield implementation. Prototype screenshots are visual/product references only.

Forbidden wording in implementation instructions:

```text
old MVP
previous MVP
legacy MVP
eski MVP
mevcut MVP
reuse MVP structure
continue from MVP
```

Correct wording:

```text
greenfield implementation
from-scratch Viaverse build
prototype screenshots are visual/product reference only
production-shaped launch slice
```

## No Hardcoded Business Role / Status / Type Strings

Business concepts must not be implemented with raw string comparisons.

Forbidden:

```kotlin
if (userType == "customer") { }
if (role == "provider") { }
if (status == "ACTIVE") { }
if (paymentProvider == "iyzico") { }
```

```java
if ("customer".equals(userType)) { }
if ("ACTIVE".equals(status)) { }
```

```ts
if (user.type === "customer") { }
if (job.status === "completed") { }
```

Required:

```kotlin
enum class AccountCapability {
    REQUEST_WORK,
    DO_WORK,
    OPERATE_BUSINESS
}

enum class JobStatus {
    DRAFT,
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    DISPUTED
}
```

```java
public enum BusinessStatus {
    DRAFT,
    PENDING_VERIFICATION,
    VERIFIED,
    PUBLISH_READY,
    PUBLISHED,
    SUSPENDED,
    ARCHIVED
}

@Enumerated(EnumType.STRING)
private BusinessStatus status;
```

```ts
export enum ConversationParticipantRole {
  Requester = "REQUESTER",
  Provider = "PROVIDER",
  BusinessStaff = "BUSINESS_STAFF",
  Support = "SUPPORT"
}
```

## Adapter Boundary Exception

Raw strings are allowed only at adapter boundaries:

- external provider raw status
- HTTP query raw input before validation/mapping
- translation keys
- slugs
- user-generated content

Raw provider values must be mapped immediately:

```java
PaymentProviderStatus mapped = iyzicoStatusMapper.map(rawStatus);
```

Provider raw strings must not leak into domain or application use cases.

## Policy Over If-Soup

Wrong:

```java
if ("provider".equals(role) && "OPEN".equals(status)) {
    // business rule
}
```

Correct:

```java
if (jobPolicy.canSubmitOffer(actor, job)) {
    // business rule
}
```

## Architecture Test Requirement

The codebase must include checks for:

- domain does not depend on Spring/JPA/Hibernate/provider SDKs
- controllers do not depend directly on repositories
- application layer does not depend on adapter implementations
- DTOs do not leak into domain
- forbidden hardcoded business string comparisons
- enum ordinal persistence is not used
