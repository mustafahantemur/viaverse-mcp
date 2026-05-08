# REST / OpenAPI Contracts

OpenAPI contracts should be stored here or linked from generated service contracts.

## Rules

- Use versioned APIs.
- Define enum values explicitly.
- Do not model role/status/type as arbitrary free strings.
- Breaking changes require ADR or migration plan.
- Client DTOs are generated/mapped and must not leak into domain or Compose screens.
