# viaverse-docs-mcp

This MCP server searches the Viaverse source-of-truth docs and returns the smallest sufficient context for a task.

## Setup

```bash
cd tools/viaverse-docs-mcp
npm install
npm run build
```

## Development Run

```powershell
$env:VIAVERSE_REPO_ROOT="C:\Projects\Viaverse\viaverse-platform"
npm run dev
```

## Localhost / Inspector Run

```powershell
$env:VIAVERSE_REPO_ROOT="C:\Projects\Viaverse\viaverse-platform"
npm run start:http
```

Streamable HTTP endpoint:

```text
http://127.0.0.1:6275/mcp
```

Health endpoint:

```text
http://127.0.0.1:6275/health
```

Tool exchanges are logged as JSONL:

```text
tools/viaverse-docs-mcp/logs/mcp-exchanges.jsonl
```

## Tools

```text
get_project_orientation
resolve_task_context
get_context_bundle
get_doc_outline
read_doc_sections
search_docs
read_doc
list_docs
get_related_umls
get_context_pack
pre_coding_brief
check_forbidden_terms
```

## Current Mode

The MCP is optimized for greenfield implementation, not template completion.

- Default mode is compact.
- Max bounded contexts per task: `2`
- Max canonical docs per task: `3`
- Max snippet per doc: `1`
- React/template references are included only for UI/screen tasks.
- Foundation/auth/profile tasks must exclude marketplace, payment, chat, review, support, business workspace, and full social flows unless explicitly requested.

## Expected First Real Task

```text
Create Viaverse greenfield Gradle Kotlin DSL monorepo skeleton with:
- backend Spring Boot platform-service
- mobile KMP/CMP app foundation
- backend health endpoint
- mobile-to-backend health check
```
