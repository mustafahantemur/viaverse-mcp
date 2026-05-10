# Viaverse Kotlin Template

Production-shaped launch prototype for the Viaverse mobile experience.

This template is intentionally mock-backed, but the code is organized like a real app:

- UI screens do not own backend-like mock data.
- Mock calls go through repository and service boundaries.
- Auth is gated by OTP/password rules before app surfaces are available.
- Mock users, credentials, API placeholders and global flags live in config.
- Splash keeps the Viaverse visual feel while using Compose-native drawing.

## Current Scope

- Splash screen, login, OTP verification with `111111`, password verification and sign up.
- Mock persistent storage boundary for locally created auth records.
- Centralized theme, colors, dimensions and typography.
- Main navigation with Explore, Requests, Work, Messages and Profile.
- Search/filter discovery backed by `DiscoveryRepository` and `MockDiscoveryService`.
- Request draft creation with category, schedule, location, budget and AI guidance.
- Individual service-provider onboarding as a lightweight personal capability flow.
- Business workspace as a separate verification, merchant, staff, catalog and publish-readiness flow.
- Profile settings grouped by identity, security, payment, privacy and support.
- Conversation detail with policy-aware safety guidance before job acceptance.
- AI-readable Turkish insight structures on main workflow surfaces.

## Validation

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat :composeApp:assembleDebug
```
