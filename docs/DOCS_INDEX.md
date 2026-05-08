# DOCS_INDEX.md

This is the canonical navigation file for Viaverse AI agents and developers.

## Non-Negotiable Rules

- Viaverse is a greenfield implementation.
- Prototype screenshots are visual/product references only.
- Do not use old/previous/legacy MVP assumptions.
- Do not use hardcoded business role/status/type strings.
- Use enums, value objects, policies, typed state machines and generated contract types.
- AI must use MCP/context pack instead of loading all markdown files.

## Reading Flow

```text
DOCS_INDEX.md
→ viaverse-docs-mcp:get_context_pack
→ relevant blueprint document
→ related UML README / UML_INDEX
→ ADR / contract
→ code
```

## Documents

| Document | Purpose |
|---|---|
| `AGENTS.md` | AI agent behavior and Pre-Coding Brief |
| `README.md` | Project overview and setup |
| `CODING_RULES.md` | Mandatory code rules |
| `docs/blueprint/PRODUCT_MODEL.md` | Product and domain model |
| `docs/blueprint/TECH_STACK_DECISIONS.md` | Technology choices |
| `docs/blueprint/ARCHITECTURE.md` | System architecture |
| `docs/blueprint/BACKEND_ARCHITECTURE.md` | Spring Boot backend structure |
| `docs/blueprint/CLIENT_ARCHITECTURE.md` | KMP client structure |
| `docs/blueprint/DATA_ARCHITECTURE.md` | Data stores and ownership |
| `docs/blueprint/EVENT_ARCHITECTURE.md` | Kafka/outbox/event model |
| `docs/blueprint/SECURITY_MODEL.md` | Auth/security/rate limits |
| `docs/blueprint/PAYMENT_MODEL.md` | Payment/provider abstraction |
| `docs/blueprint/PRIVACY_AND_KVKK.md` | KVKK/privacy/retention |
| `docs/blueprint/MONETIZATION_MODEL.md` | Subscription/commission/ads |
| `docs/blueprint/GAMIFICATION_AND_TRUST.md` | Trust/review/badges |
| `docs/blueprint/SEO_AND_GROWTH.md` | SEO/growth/analytics |
| `docs/blueprint/ROADMAP.md` | Delivery phases |
| `docs/blueprint/WEB_FRONTEND_STRUCTURE.md` | Web/landing/admin structure |

## Bounded Context to UML Map

| Context | Related UML |
|---|---|
| identity | 01, 14, 18, 28 |
| profile | 02, 18, 21, 28 |
| taxonomy | 21, 24, 28 |
| marketplace | 03, 04, 05, 06, 28 |
| business | 22, 23, 28 |
| payment | 08, 15, 18, 23, 28 |
| chat | 10, 18, 25, 28 |
| media | 07, 10, 28 |
| trust | 09, 10, 28 |
| search | 05, 16, 24, 28 |
| infrastructure | 13, 17, 19, 27, 28 |
| AI/support | 20, 25, 28, 29 |
