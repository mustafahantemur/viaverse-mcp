# AI_CODING_PROMPT.md

Use this prompt for Codex/Copilot/AI coding agents working on Viaverse.

```text
You are working on Viaverse, a greenfield implementation.

Do not use old MVP/prototype implementation assumptions.
Prototype screenshots are visual/product reference only.

Before coding:
1. Use the viaverse-docs MCP server.
2. Read docs/DOCS_INDEX.md.
3. Call resolve_task_context.
4. Call get_context_bundle.
5. Call pre_coding_brief.
6. Produce a short implementation plan.
7. Search/read only relevant docs.
8. Follow AGENTS.md and CODING_RULES.md.
9. Do not use hardcoded role/status/type/provider strings.
10. Use enums, value objects, policies and generated contract types.
11. Implement the smallest safe vertical slice.
12. Add tests.
13. Update docs when behavior/contracts change.

The Pre-Coding Brief must not contain blank placeholders. Unknown fields must say "Unknown / must inspect repo" or "Not applicable".

Global backend defaults:
- Java 25, Spring Boot, Gradle Kotlin DSL.
- Flyway migrations and Hibernate ddl-auto=validate.
- Structured ECS JSON logs to stdout/stderr.
- audit_log is not application logging.
- OpenSearch is the default log/search store.
- No MinIO; use SeaweedFS only for local S3-compatible storage behind generic object-storage abstractions.
- No manual JWT; use Spring Security Resource Server and Nimbus-backed JwtEncoder/JwtDecoder.

Stop for architecture review if:
- a new bounded context is introduced
- a new datastore is introduced
- a new external provider is introduced
- a payment/security/privacy/moderation/identity flow is affected
- a breaking REST/event contract is needed
- a domain state transition is unclear
- hardcoded business strings appear necessary
- AGPL/GPL/LGPL/SSPL/copyleft infrastructure appears necessary
- a huge task asks for a full template/all flows instead of a vertical slice
```
