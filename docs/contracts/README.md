# Contracts Index

Contracts live here.

Use this folder for REST/OpenAPI contracts, event schemas and provider-facing contract documentation.

## Structure

```text
docs/contracts/
  README.md
  rest/
    openapi.md
  events/
    README.md
  providers/
    payment-providers.md
```

## Rules

- REST contracts are versioned.
- Event contracts are versioned.
- Breaking changes require ADR or migration plan.
- Backend, BFF and KMP client must not drift from contracts.
- Raw provider strings must not leak into domain.
