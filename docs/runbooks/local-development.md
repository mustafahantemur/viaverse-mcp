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

## Forbidden Terms Check

Use MCP `check_forbidden_terms` before committing documentation-heavy changes.
