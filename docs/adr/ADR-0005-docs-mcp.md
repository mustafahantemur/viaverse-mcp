# ADR-0005 — Viaverse Docs MCP

## Status

Accepted

## Context

Viaverse has many detailed markdown and UML files. Loading all files into AI context wastes tokens and creates lower-quality implementation.

## Decision

Use a local read-only MCP server named `viaverse-docs-mcp`.

It exposes docs search, related UML lookup, context pack generation, pre-coding brief template and forbidden-term checks.

## Consequences

- AI agents should use MCP before coding.
- MCP is read-only by default.
- No shell execution.
- No secret reads.
- No path traversal.
- AI tasks start with a Pre-Coding Brief.
