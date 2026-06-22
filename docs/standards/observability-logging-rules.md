# Viaverse Observability and Logging Rules

These rules apply to every backend service.

## Application Logs

- Application logs go to stdout/stderr as structured JSON.
- Spring Boot services should use ECS structured console logs unless an ADR changes it.
- Do not write app logs to local text files.
- Do not write app logs to `audit_log`.
- Do not create `app.log`, `debug.log`, `local.txt`, `errors.txt`, or similar file-based default logs.
- Logs must be machine-searchable and collector-friendly.

Safe log metadata:

- `service.name`
- `deployment.environment`
- `trace.id`
- `span.id`
- `correlation.id`
- `request.id`
- `error.code`
- `event.action`
- `event.outcome`

Never log:

- Access tokens.
- Refresh tokens.
- Authorization headers.
- Passwords.
- OTP values.
- API keys.
- Client secrets.
- Raw phone numbers unless masked.
- Raw emails unless masked or there is a clear documented reason.
- KVKK/legal sensitive personal data.

## Audit Logs

`audit_log` is not application logging.

Audit log is for security, legal, and account-critical events. Audit events must be typed. Do not pass random string actions such as `identity-login`.

Valid audit examples:

- `LOGIN_SUCCEEDED`
- `LOGIN_FAILED`
- `OTP_VERIFIED`
- `OTP_FAILED`
- `REFRESH_TOKEN_REUSED`
- `SESSION_REVOKED`
- `CONSENT_ACCEPTED`
- `ACCOUNT_CREATED`
- `ACCOUNT_DISABLED`

Use typed `AuditAction` values or typed audit event factories.

## Central Log Direction

- OpenSearch is the preferred central log/search store because it is Apache-2.0 aligned with Viaverse's permissive-license policy.
- OpenSearch is also the initial search/discovery infrastructure candidate.
- OpenSearch Dashboards may be used for local/dev visualization.
- Log collection should use OpenTelemetry Collector or Fluent Bit.
- Graylog is not the default choice because Graylog Open is SSPL.
- Loki/Grafana AGPL concerns must be considered before adding them.
- Do not add AGPL/GPL/SSPL observability dependencies unless an ADR explicitly approves them.

Business code must not depend directly on OpenSearch for logging. Emit structured logs to stdout first; a collector pipeline can forward logs to OpenSearch.

## Staged Integration

Stage 1:

- Structured stdout/stderr ECS JSON logs.
- CorrelationId/requestId MDC.
- Audit separation.

Stage 2:

- OpenTelemetry Collector.
- OpenSearch log ingestion.
- OpenSearch Dashboards.

Stage 3:

- Trace/log correlation and dashboards.

## Metrics and Tracing

- Services must be OpenTelemetry-ready.
- Preserve `correlationId` and `requestId`.
- Include `traceId` and `spanId` when available.
- Spring Boot Actuator/Micrometer should be enabled.
- A Prometheus-compatible metrics endpoint can be exposed.
- Basic service tests must not require the full observability stack.
