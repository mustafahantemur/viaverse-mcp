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

## Forbidden Terms Check

Use MCP `check_forbidden_terms` before committing documentation-heavy changes.
