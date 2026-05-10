# Viaverse Kotlin Template

Production-shaped MVP prototype for the Viaverse mobile experience.

This template is intentionally mock-backed, but the code is organized like a real app:

- UI screens do not own backend-like mock data.
- Mock calls go through repository and service boundaries.
- Auth is gated by OTP before app surfaces are available.
- Mock users and global flags live in config.
- Splash keeps the Viaverse visual feel while using Compose-native drawing.

## Phase 1 Scope

- Splash screen
- Login
- OTP verification with `111111`
- Sign up when a user is not found
- Mock persistent storage boundary
- Centralized theme, colors, dimensions and typography

## Next Phases

1. Main navigation and Explore/Search filters
2. Personal/requester and individual Work Mode transition
3. BusinessAccount onboarding as a separate business flow
4. Profile/settings and AI-readable screen insight structures

