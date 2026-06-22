# React Template to Kotlin Compose Migration Guide

## Purpose

This guide is MCP context for converting the visual React template into the Kotlin/Compose template without treating the React prototype as the technical foundation.

The React template is a visual and interaction reference only. Kotlin implementation must follow Viaverse client rules, Compose Multiplatform structure, and the Kotlin template guardrails.

## Required MCP Flow

Before Kotlin template work:

1. `resolve_task_context` with the actual task text.
2. `get_context_bundle` using `client` or `template`.
3. `read_doc` for this guide.
4. `read_doc` for `templates/kotlin/viaverse-template/KOTLIN_TEMPLATE_GUARDRAILS.md`.
5. Inspect only the relevant React and Kotlin files for the current phase.

## Source References

- React visual reference: `templates/react/viaverse-ui-template`
- Kotlin target: `templates/kotlin/viaverse-template`
- Kotlin guardrails: `templates/kotlin/viaverse-template/KOTLIN_TEMPLATE_GUARDRAILS.md`
- Client architecture: `docs/blueprint/CLIENT_ARCHITECTURE.md`
- Repository rules: `AGENTS.md`

## Migration Rules

- Preserve the visual feel when the user asks for parity, especially splash, brand assets, spacing, and color intent.
- Do not copy React component structure blindly. Compose files should be small, reviewable, and single responsibility.
- UI screens must not own mock data. Mock responses belong in service/repository layers.
- Use `ViaverseTheme`, shared colors, typography, and dimensions.
- Keep auth flow realistic: identifier, password when required, OTP when configured, sign-up persistence through the mock storage service.
- Use enums or typed models for business concepts. Do not add raw business role/status/type string comparisons.
- SVG assets must not be loaded through Compose Multiplatform `painterResource` on Android. Prefer PNG resources, `ImageVector`, or Android-safe vector drawable strategy.
- Category and brand PNG assets may be reused after naming cleanup.

## Phase Checklist

For each Kotlin template phase:

- Identify the React files that visually inform the phase.
- Identify the Kotlin target files and create new small composables when a file starts to mix responsibilities.
- Keep state transition logic outside leaf composables.
- Move hardcoded style values into theme/dimension tokens when they are reused.
- Build with `./gradlew :composeApp:assembleDebug`.
- Install and launch on emulator when UI/resources changed.
- Check logcat for `FATAL EXCEPTION`, `AndroidRuntime`, and `ComposeInternal`.

## Auth-Specific Rules

- `AuthScreen.kt` is a container for state transitions.
- Identifier, password, OTP, and sign-up screens stay in separate files.
- OTP uses one hidden real input; visual boxes are not separate input fields.
- Mock credentials, password, OTP, and 2FA flags stay in `AppConfig`.
- The user must not see debug/mock credential hints inside the runtime UI.

## AI Context Notes

Kotlin template structures should be easy for an AI agent to interpret:

- screen files map to user-visible states,
- service/repository files describe simulated backend behavior,
- config files describe fixture users and global flags,
- Turkish insight/help copy should be structured as data where possible instead of buried inside large UI functions.
