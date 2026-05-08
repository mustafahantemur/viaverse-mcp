# AI Development with Viaverse MCP

## Purpose

This runbook explains how any human developer or AI coding agent should use Viaverse documentation through MCP.

The goal is not simply to connect MCP. The goal is to make the AI retrieve the right amount of context, from the right source, at the right time.

## Required Flow

For every task:

```text
1. get_project_orientation
2. resolve_task_context
3. get_context_bundle
4. read only missing docs/sections if needed
5. pre_coding_brief
6. implement
7. validate with tests
8. update docs/contracts/UML when behavior changes
```

## Why

Viaverse has detailed markdown, UML, ADR and contract documents. Loading all of them creates token bloat and increases hallucination risk.

The MCP server is the knowledge gateway.

## Golden Rule

The AI should not guess which docs to read.

It should call:

```text
resolve_task_context(task)
```

Then:

```text
get_context_bundle(boundedContext, task)
```

## Example

Task:

```text
Implement business publish flow.
```

Expected MCP sequence:

```text
get_project_orientation()
resolve_task_context("Implement business publish flow")
get_context_bundle("business", "Implement business publish flow")
get_related_umls("business")
pre_coding_brief("business", "Implement business publish flow")
```

## If Context Is Insufficient

The AI should call:

```text
get_doc_outline(filePath)
read_doc_sections(filePath, ["Business Publish", "Business Verification", "Business Status"])
search_docs("specific missing term", boundedContext)
```

The AI should not load all docs.

## Pre-Coding Brief Is Mandatory

Before code edits, the AI must produce:

```text
Bounded context:
Relevant docs:
Relevant UML:
Contracts affected:
Domain states / enums / value objects:
Policies / business rules:
Data stores affected:
Events produced/consumed:
Tests:
Docs to update:
Risks / open questions:
```

## Stop Conditions

Stop and request human architecture review if:

```text
new bounded context
new datastore
new external provider
breaking REST/event contract
payment/security/privacy/moderation/identity impact
unclear domain state
hardcoded business string appears necessary
raw provider value leaks into domain
another service's aggregate would be mutated
```
