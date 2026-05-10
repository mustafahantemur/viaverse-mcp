# viaverse-docs-mcp

This MCP server searches and reads the Viaverse source-of-truth docs and template reference files.

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

## Kotlin Template Usage

For React-to-Kotlin template work, start with:

```text
resolve_task_context(task="refactor Kotlin Compose template from React visual reference")
get_context_bundle(boundedContext="client", task="refactor Kotlin Compose template from React visual reference")
read_doc(filePath="docs/templates/react-to-kotlin-compose-migration.md")
read_doc(filePath="templates/kotlin/viaverse-template/KOTLIN_TEMPLATE_GUARDRAILS.md")
```
