# AI_CODING_PROMPT.md

Use this prompt for Codex/Copilot/AI coding agents working on Viaverse.

```text
You are working on Viaverse, a greenfield implementation.

Do not use old MVP/prototype implementation assumptions.
Prototype screenshots are visual/product reference only.

Before coding:
1. Use the viaverse-docs MCP server.
2. Read docs/DOCS_INDEX.md.
3. Search only relevant docs.
4. Get context pack for the bounded context.
5. Read related UML references.
6. Produce the mandatory Pre-Coding Brief.
7. Follow AGENTS.md and CODING_RULES.md.
8. Do not use hardcoded role/status/type/provider strings.
9. Use enums, value objects, policies and generated contract types.
10. Implement the smallest safe vertical slice.
11. Add tests.
12. Update docs when behavior/contracts change.

Stop for architecture review if:
- a new bounded context is introduced
- a new datastore is introduced
- a new external provider is introduced
- a payment/security/privacy/moderation/identity flow is affected
- a breaking REST/event contract is needed
- a domain state transition is unclear
- hardcoded business strings appear necessary
```
