# AI_CONTEXT_AND_MCP_STRATEGY.md

## Goal

MCP must be fed from the original detailed Viaverse markdown files, not shortened summaries.

## MCP Server

`viaverse-docs-mcp` exposes:

```text
search_docs
read_doc
list_docs
get_related_umls
get_context_pack
pre_coding_brief
check_forbidden_terms
```

## AI Flow

```text
1. read DOCS_INDEX.md
2. get_context_pack(boundedContext, task)
3. read only relevant documents
4. read related UML README/index
5. produce Pre-Coding Brief
6. implement
```

## Safety

- read-only
- no shell execution
- no file writing
- no absolute path
- no path traversal
- ignore `.git`, `node_modules`, `dist`, `.env`, `.pem`, `.key`, `secrets`, `credentials`
