# Local Development Runbook

## Minimum Checks

```powershell
node -v
npm -v
java -version
```

## MCP

```powershell
cd tools\viaverse-docs-mcp
npm install
npm run build
```

## Core Local Infra

```powershell
./scripts/dev/start-core-infra.ps1
docker compose --env-file infra/docker-compose/.env.example -f infra/docker-compose/docker-compose.local.yml ps
```

Core local services:

- PostgreSQL
- Valkey
- Kafka
- Mailpit
- SeaweedFS with S3-compatible endpoint at `http://localhost:8333`
- OpenSearch may run under search or observability profile

## Service Boot Rules

- Check Flyway migrations before `bootRun` or debug.
- Keep Hibernate `ddl-auto=validate`.
- Do not use `ddl-auto=update` or `ddl-auto=create` for normal development.
- Services must emit structured logs to stdout/stderr.
- Do not create local text-file app logs.
- OpenSearch/log collection must not be required for basic identity-service tests unless the task explicitly targets observability.

## License Guardrails

- Do not add MinIO.
- SeaweedFS is the local S3-compatible object-storage provider.
- Avoid AGPL/GPL/LGPL/SSPL infrastructure unless an ADR explicitly approves it.

## Forbidden Terms Check

Use MCP `check_forbidden_terms` before committing documentation-heavy changes.
