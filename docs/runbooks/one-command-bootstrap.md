# One-Command Local Bootstrap

## Purpose

Before AI-assisted development starts, every developer should be able to prepare Viaverse docs + MCP with one command.

## Command

From repository root:

```powershell
npm run viaverse:bootstrap
```

This command:

1. generates `.env.local`
2. keeps local object-storage defaults provider-agnostic for SeaweedFS-backed S3 testing
3. generates `.codex/config.toml`
4. installs MCP dependencies
5. builds MCP
6. generates `docs/docs-index.generated.json`
7. runs forbidden-term checks
8. runs MCP smoke test

## Stable Local Ports

```text
backend: 8090
landing web: 3000
admin web: 3001
business web: 3002
postgres: 5432
redis: 6379
kafka: 9092
opensearch: 9200
mailpit smtp: 1025
mailpit web: 8025
mcp inspector: 6274
seaweedfs master: 9333
seaweedfs volume: 8080
seaweedfs filer: 8888
seaweedfs s3: 8333
```

## Stable Local Keys

`.env.local` contains stable local-only development keys.

These keys are intentionally deterministic for local developer consistency, but they must never be used in staging or production.

To start the local core infrastructure:

```powershell
./scripts/dev/start-core-infra.ps1
```
