# ADR-0006 — Replace MinIO with SeaweedFS for local S3-compatible storage

## Status

Accepted for local development

## Context

- Viaverse does not accept AGPL, GPL, or SSPL infrastructure dependencies in local or production-shaped foundations.
- MinIO is AGPLv3 licensed and is not acceptable for Viaverse local infrastructure.
- Viaverse still needs a local S3-compatible workflow for media and object-storage integration work.
- SeaweedFS is Apache-2.0 licensed and provides an S3-compatible API suitable for local development.

## Decision

- MinIO is removed from Viaverse local infrastructure.
- SeaweedFS is used for local S3-compatible object storage.
- Local configuration uses generic `object-storage` naming instead of provider-specific naming.
- Application code must depend on a generic object storage abstraction such as `ObjectStoragePort` or `ObjectStorageClient`.
- SeaweedFS remains a local development adapter choice only.
- Production object storage provider selection stays separate and can be decided by a future ADR.

## Alternatives Considered

- Keep MinIO for local development.
- Use LocalStack for the local S3-compatible path.

## Consequences

- Viaverse local infrastructure aligns with the permissive-license requirement.
- Local development keeps an S3-like workflow through a replaceable provider.
- Application code must not use SeaweedFS-specific APIs.
- Bucket bootstrap is handled by local infra automation so media-service work can start without manual setup.

## Related Docs / UML

- `README.md`
- `docs/blueprint/ARCHITECTURE.md`
- `docs/blueprint/TECH_STACK_DECISIONS.md`
- `docs/runbooks/local-development.md`
