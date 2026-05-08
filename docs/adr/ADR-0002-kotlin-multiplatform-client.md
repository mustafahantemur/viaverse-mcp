# ADR-0002 — Kotlin Multiplatform Client

## Status

Accepted

## Context

Viaverse needs Android/iOS/Web-friendly shared client architecture without maintaining completely separate native clients early.

## Decision

Use Kotlin Multiplatform + Compose Multiplatform for the main shared app client.

Next.js + TypeScript remains the web choice for landing, SEO, admin and optional business console.

## Consequences

- Shared client modules must be carefully structured.
- API DTOs must not leak into Compose screens.
- Design system tokens must be centralized.
- Platform-specific APIs stay behind platform abstractions.
