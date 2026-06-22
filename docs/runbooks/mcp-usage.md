# MCP Usage Runbook

## Setup

```powershell
cd tools\viaverse-docs-mcp
npm install
npm run build
```

## Development Run

```powershell
$env:VIAVERSE_REPO_ROOT="C:\Projects\Viaverse\viaverse-platform"
npm run dev
```

## Codex Config

`.codex/config.toml`:

```toml
[mcp_servers.viaverse-docs]
enabled = true
command = "node"
args = ["C:\\Projects\\Viaverse\\viaverse-platform\\tools\\viaverse-docs-mcp\\dist\\index.js"]

[mcp_servers.viaverse-docs.env]
VIAVERSE_REPO_ROOT = "C:\\Projects\\Viaverse\\viaverse-platform"
```

## Expected Tools

```text
resolve_task_context
get_context_bundle
search_docs
read_doc
list_docs
get_related_umls
get_context_pack
pre_coding_brief
check_forbidden_terms
```

For coding tasks, call `resolve_task_context`, `get_context_bundle`, and `pre_coding_brief` before writing code. The brief must fill every field or explicitly say `Unknown / must inspect repo` or `Not applicable`.
