import type { DocItem, DocType } from "./docs.js";
import { scoreDoc, snippet } from "./docs.js";

export type ContextPack = {
  boundedContext: string;
  task: string;
  docs: Array<{
    path: string;
    title: string;
    type: DocType;
    score: number;
    snippet: string;
  }>;
  suggestedUmlParts: string[];
  preCodingBriefTemplate: string;
};

const umlMap: Record<string, string[]> = {
  identity: ["Part 01", "Part 02", "Part 05", "Part 06", "Part 14", "Part 18", "Part 28"],
  profile: ["Part 02", "Part 18", "Part 21", "Part 28"],
  taxonomy: ["Part 21", "Part 24", "Part 28"],
  marketplace: ["Part 03", "Part 04", "Part 05", "Part 06", "Part 28"],
  business: ["Part 06", "Part 22", "Part 23", "Part 28"],
  payment: ["Part 08", "Part 15", "Part 18", "Part 23", "Part 28"],
  monetization: ["Part 22", "Part 23", "Part 28"],
  chat: ["Part 10", "Part 18", "Part 25", "Part 28"],
  media: ["Part 07", "Part 10", "Part 28"],
  trust: ["Part 09", "Part 10", "Part 28"],
  search: ["Part 05", "Part 16", "Part 24", "Part 28"],
  infrastructure: ["Part 13", "Part 17", "Part 19", "Part 27", "Part 28"],
  ai: ["Part 20", "Part 25", "Part 28", "Part 29"],
  security: ["Part 14", "Part 18", "Part 25", "Part 28"],
  api: ["Part 27", "Part 28", "Part 29"],
  client: ["Part 02", "Part 03", "Part 05", "Part 06", "Part 18", "Part 24", "Part 28"],
  template: ["Part 02", "Part 03", "Part 05", "Part 06", "Part 24", "Part 28"]
};

export function getRelatedUmls(boundedContext: string): string[] {
  return umlMap[boundedContext.toLowerCase()] ?? ["Part 28", "Part 29"];
}

export function createPreCodingBriefTemplate(boundedContext: string, task: string): string {
  return `## Pre-Coding Brief

Bounded context:
- ${boundedContext}

Task:
- ${task}

Relevant docs:
- [fill from MCP context pack]

Relevant UML:
- ${getRelatedUmls(boundedContext).join(", ")}

Contracts affected:
- REST:
- Events:
- DB migrations:
- External providers:

Domain states / enums / value objects:
-

Policies / business rules:
-

Data stores affected:
- PostgreSQL:
- Cassandra:
- Redis:
- OpenSearch:
- S3/Media:

Events:
- Produced:
- Consumed:

Tests:
- Unit:
- Integration:
- Contract:
- Architecture:
- E2E/manual:

Docs to update:
-

Risks / open questions:
-`;
}

export function buildContextPack(docs: DocItem[], boundedContext: string, task: string, limit = 8): ContextPack {
  const ranked = docs
    .map(doc => ({
      path: doc.path,
      title: doc.title,
      type: doc.type,
      score: scoreDoc(doc, task, boundedContext),
      snippet: snippet(doc.content, task)
    }))
    .filter(x => x.score > 0)
    .sort((a, b) => b.score - a.score)
    .slice(0, limit);

  return {
    boundedContext,
    task,
    docs: ranked,
    suggestedUmlParts: getRelatedUmls(boundedContext),
    preCodingBriefTemplate: createPreCodingBriefTemplate(boundedContext, task)
  };
}
