# Viaverse MCP V2 Context Intelligence Patch

## What This Patch Adds

### New MCP Tools

```text
get_project_orientation
resolve_task_context
get_context_bundle
get_doc_outline
read_doc_sections
```

Existing tools remain:

```text
search_docs
read_doc
list_docs
get_related_umls
get_context_pack
pre_coding_brief
check_forbidden_terms
```

### Why

The AI should not guess which document to read. It should ask MCP:

```text
resolve_task_context(task)
get_context_bundle(boundedContext, task)
```

### README / Runbook Improvements

Adds:

```text
README_AI_DEVELOPMENT_SECTION.md
docs/runbooks/ai-development-with-mcp.md
```

Use the README section to update the project README before pushing to the private repo.

### New Files

```text
tools/viaverse-docs-mcp/src/task-router.ts
tools/viaverse-docs-mcp/src/sections.ts
```

### Updated Files

```text
tools/viaverse-docs-mcp/src/index.ts
tools/viaverse-docs-mcp/src/smoke-test.ts
tools/viaverse-docs-mcp/package.json
```

## Recommended Verification

```powershell
cd C:\Projects\Viaverse\viaverse-platform
npm run viaverse:bootstrap
npm run viaverse:verify
```
