# REST API Conventions

## Base Principles

- REST APIs are versioned.
- Request/response DTOs are contract-bound.
- DTOs do not leak into domain.
- Business role/status/type values are enums, not arbitrary strings.
- Breaking changes require ADR or migration plan.

## Common Headers

```text
Authorization: Bearer <access-token>
X-Request-Id: <uuid>
Idempotency-Key: <key>    // for payment / critical commands
Accept-Language: tr-TR | en-US
```

## Response Envelope

Use explicit success/error models. Avoid ambiguous generic maps.

## Pagination

Preferred:

```text
page
size
sort
cursor where needed
```

Search-heavy endpoints may use cursor/search-after depending on OpenSearch design.

## Error Model

Errors must include:

```text
code
message
details
requestId
timestamp
```

Business error codes should be stable and documented.
