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
- Step-based request draft creation with category, schedule, location, budget, review and matching states.
- Individual service-provider onboarding as a lightweight personal capability flow gated before the provider dashboard.
- Business workspace as a separate verification, merchant, staff/team, catalog, subscription and publish-readiness flow.
- Customer job workspace with offer comparison, safe-payment transition, timeline and support/safety notes.
- Wallet, reviews, support center and public profile mock workflows.
- Profile settings grouped by identity, security, payment, privacy and support.
- Conversation detail with policy-aware safety guidance and local mock message sending.
- AI-readable Turkish insight structures on main workflow surfaces.

## Mock Accounts

All credentials live in `AppConfig`.

| Identifier | Password | 2FA | Capabilities |
| --- | --- | --- | --- |
| `zehra@viaverse.test` | `zehra123` | No | Request work |
| `ahmet@viaverse.test` | `ahmet123` | Yes | Request work, do work individually |
| `merve@viaverse.test` | `merve123` | No | Request work, operate business |
| `can@viaverse.test` | `can123` | Yes | Request work, do work individually, operate business |

OTP code is `111111`.

## Validation

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat :composeApp:assembleDebug
.\gradlew.bat :composeApp:check
```

Install and launch on a running emulator:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 install -r composeApp\build\outputs\apk\debug\composeApp-debug.apk
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 shell monkey -p app.viaverse.template -c android.intent.category.LAUNCHER 1
```
