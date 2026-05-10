import type { DocItem, DocType } from "./docs.js";
import { scoreDoc, snippet } from "./docs.js";
import { buildContextBundle, resolveTaskContext } from "./task-router.js";

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
  preCodingBrief: ReturnType<typeof buildContextBundle>["preCodingBrief"];
};

const umlMap: Record<string, string[]> = {
  foundation: ["Part 28"],
  identity: ["Part 02", "Part 18", "Part 28"],
  profile: ["Part 02", "Part 18", "Part 28"],
  marketplace: ["Part 03", "Part 04", "Part 28"],
  business: ["Part 06", "Part 22", "Part 28"],
  payment: ["Part 08", "Part 15", "Part 28"],
  chat: ["Part 25", "Part 28"],
  search: ["Part 05", "Part 24", "Part 28"],
  client: ["Part 02", "Part 18", "Part 28"]
};

export function getRelatedUmls(boundedContext: string): string[] {
  return umlMap[boundedContext.toLowerCase()] ?? ["Part 28"];
}

export function createPreCodingBriefTemplate(boundedContext: string, task: string): string {
  const compact = buildContextBundle([], boundedContext, task);
  return JSON.stringify(compact.preCodingBrief, null, 2);
}

export function buildContextPack(docs: DocItem[], boundedContext: string, task: string, limit = 3): ContextPack {
  const resolution = resolveTaskContext(task);
  const ranked = docs
    .map(doc => ({
      path: doc.path,
      title: doc.title,
      type: doc.type,
      score: scoreDoc(doc, task, boundedContext),
      snippet: snippet(doc.content, task)
    }))
    .filter(item => item.score > 0)
    .sort((a, b) => b.score - a.score)
    .slice(0, limit)
    .map(item => ({
      ...item,
      snippet: item.snippet.length > 280 ? `${item.snippet.slice(0, 277)}...` : item.snippet
    }));

  return {
    boundedContext,
    task,
    docs: ranked,
    suggestedUmlParts: resolution.relatedUmlParts,
    preCodingBrief: buildContextBundle(docs, boundedContext, task).preCodingBrief
  };
}
