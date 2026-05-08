# ADR-0004 — No Hardcoded Business Strings

## Status

Accepted

## Context

Hardcoded role/status/type/provider string checks cause brittle logic, hidden bugs and AI-generated shortcuts.

## Decision

Business concepts must use enums, value objects, sealed classes, policies or generated contract types.

Forbidden examples:

```text
if userType == "customer"
if role == "provider"
if status == "ACTIVE"
if paymentProvider == "iyzico"
```

## Consequences

- Domain states must be explicitly modeled.
- Provider raw strings must be mapped at adapter boundaries.
- Architecture/quality checks should catch forbidden patterns.
