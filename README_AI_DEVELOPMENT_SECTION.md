# README.md — Add this section

## AI-Assisted Development

Viaverse is designed to be developed with AI assistance, but AI must use the repository knowledge system correctly.

### One-Time Setup

From repository root:

```powershell
npm run viaverse:bootstrap
```

This installs/builds the Viaverse docs MCP server, generates the docs index, checks forbidden terms, creates `.env.local`, creates `.codex/config.toml`, and runs the MCP smoke test.

### Before Any AI Coding Task

The AI agent must use MCP first.

Required MCP flow:

```text
get_project_orientation
resolve_task_context
get_context_bundle
pre_coding_brief
```

The AI must not start coding until it has produced the Pre-Coding Brief.

### Required Prompt for AI Agents

```text
You are working on Viaverse, a greenfield implementation.

Use viaverse-docs MCP before coding.
Do not load all docs manually.
First call get_project_orientation.
Then call resolve_task_context for the task.
Then call get_context_bundle for the bounded context.
Before editing code, produce the mandatory Pre-Coding Brief.

Rules:
- No old MVP/prototype implementation assumptions.
- Prototype screenshots are visual/product reference only.
- No hardcoded business role/status/type/provider strings.
- Use enums, value objects, policies and generated contract types.
- Follow AGENTS.md and CODING_RULES.md.
- Stop for architecture review if a new bounded context, datastore, provider, payment/security/privacy/moderation impact, or breaking contract appears.
```

### MCP Tools

Core tools:

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
pre_coding_brief
check_forbidden_terms
```

### Context Rule

The AI should not guess documentation paths. It should ask MCP to resolve context for the task.

### Source of Truth

```text
README.md / AGENTS.md / CODING_RULES.md = global rules
docs/DOCS_INDEX.md = navigation
docs/blueprint = product and architecture source
docs/uml = flow and topology source
docs/adr = decision source
docs/contracts = API/event/provider contract source
docs/runbooks = operational procedures
```
