# MCP_SETUP_AND_USAGE.md

## Goal

Use `viaverse-docs-mcp` so AI agents can search and read only relevant Viaverse docs instead of loading all markdown files.

## Install

```powershell
cd tools\viaverse-docs-mcp
npm install
npm run build
```

## Development Run

```powershell
cd tools\viaverse-docs-mcp
$env:VIAVERSE_REPO_ROOT="C:\Projects\Viaverse\viaverse-platform"
npm run dev
```

The server waits on stdio. This is normal.

## Build Docs Index

```powershell
npm run docs:index
```

This generates:

```text
docs/docs-index.generated.json
```

## Forbidden Terms Check

```powershell
npm run docs:check
```

## Codex MCP Config

Create:

```text
.codex/config.toml
```

Recommended build-based config:

```toml
[mcp_servers.viaverse-docs]
enabled = true
command = "node"
args = ["C:\\Projects\\Viaverse\\viaverse-platform\\tools\\viaverse-docs-mcp\\dist\\index.js"]

[mcp_servers.viaverse-docs.env]
VIAVERSE_REPO_ROOT = "C:\\Projects\\Viaverse\\viaverse-platform"
```

Development config:

```toml
[mcp_servers.viaverse-docs]
enabled = true
command = "npx"
args = [
  "tsx",
  "C:\\Projects\\Viaverse\\viaverse-platform\\tools\\viaverse-docs-mcp\\src\\index.ts"
]

[mcp_servers.viaverse-docs.env]
VIAVERSE_REPO_ROOT = "C:\\Projects\\Viaverse\\viaverse-platform"
```

## Expected MCP Tools

```text
search_docs
read_doc
list_docs
get_related_umls
get_context_pack
pre_coding_brief
check_forbidden_terms
```

## Typical AI Flow

```text
get_context_pack("business", "implement business publish flow")
read_doc("docs/blueprint/PRODUCT_MODEL.md")
read_doc("docs/blueprint/MONETIZATION_MODEL.md")
get_related_umls("business")
pre_coding_brief("business", "implement business publish flow")
```
