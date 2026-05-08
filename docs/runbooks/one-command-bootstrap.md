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
2. generates `.codex/config.toml`
3. installs MCP dependencies
4. builds MCP
5. generates `docs/docs-index.generated.json`
6. runs forbidden-term checks
7. runs MCP smoke test

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
minio: 9000
mailpit smtp: 1025
mailpit web: 8025
mcp inspector: 6274
```

## Stable Local Keys

`.env.local` contains stable local-only development keys.

These keys are intentionally deterministic for local developer consistency, but they must never be used in staging or production.
