# Viaverse MCP Pre-Coding Brief Template

`pre_coding_brief` must not be empty. Every field must be filled with useful context or explicitly marked `Unknown / must inspect repo` or `Not applicable`.

Required fields:

- Bounded context.
- Feature/slice.
- Affected services.
- Affected REST contracts.
- Affected events.
- DB migrations.
- Domain states/enums/value objects.
- Policies/business rules.
- Data stores.
- Security considerations.
- Observability/logging impact.
- Tests.
- Docs/ADR updates.
- Risks/open questions.

Forbidden placeholders:

- `Relevant docs: [fill from MCP context pack]`
- `Domain states: -`
- `Policies: -`
- Empty list items with no meaning.

Coding tasks must follow this MCP flow before code generation:

1. `resolve_task_context`
2. `get_context_bundle`
3. `pre_coding_brief`
4. Short implementation plan
5. Code changes

Huge prompts such as `finish full template`, `complete all flows`, `port all React screens`, or `build marketplace/payment/chat/business all at once` must be split into vertical slices before coding.

Recommended vertical slices:

- `identity-auth-backend`
- `auth-abuse-protection`
- `mobile-auth-client`
- `mobile-auth-ui`
- `profile-foundation`
- `observability-foundation`
- `search-foundation`
