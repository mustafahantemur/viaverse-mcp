# Viaverse License Policy

Viaverse prefers permissive licenses:

- MIT
- Apache-2.0
- BSD
- ISC

Avoid unless explicitly approved by ADR:

- AGPL
- GPL
- LGPL
- SSPL
- Strong copyleft.
- Network copyleft.
- Source-available infrastructure dependencies.

MCP/Codex should warn when a task introduces MinIO, AGPL, GPL, LGPL, SSPL, or non-permissive infrastructure.

## Object Storage Decision

- MinIO must not be used.
- MinIO was removed because of AGPLv3/license risk.
- For local S3-compatible object storage, SeaweedFS is allowed because it is Apache-2.0.
- Application code must use a generic object-storage abstraction, not MinIO-specific or SeaweedFS-specific APIs.
- Production object storage provider will be decided by ADR later.

Use generic names such as `ObjectStoragePort`, `ObjectStorageClient`, and `object-storage.*` configuration. Do not leak local provider names into domain or application code.

## Observability Licensing

- OpenSearch is the default log/search store because it is Apache-2.0 aligned with Viaverse's permissive-license policy.
- Graylog is not default because Graylog Open is SSPL.
- Loki/Grafana AGPL concerns must be reviewed before adding them.
- No AGPL/GPL/SSPL observability dependency may be added without ADR approval.
