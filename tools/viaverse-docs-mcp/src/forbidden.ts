import type { DocItem } from "./docs.js";

export const forbiddenPatterns: Array<{ name: string; pattern: RegExp; severity: "error" | "warn" }> = [
  { name: "forbidden_old_mvp_language", pattern: /old\s+MVP|previous\s+MVP|legacy\s+MVP|eski\s+MVP|mevcut\s+MVP/gi, severity: "error" },
  { name: "prototype_as_implementation_language", pattern: /reuse\s+prototype\s+structure|continue\s+prototype/gi, severity: "error" },
  { name: "hardcoded_customer_check", pattern: /userType\s*(==|===|\.equals\()\s*["']customer["']/gi, severity: "error" },
  { name: "hardcoded_provider_check", pattern: /role\s*(==|===|\.equals\()\s*["']provider["']/gi, severity: "error" },
  { name: "hardcoded_active_status", pattern: /status\s*(==|===|\.equals\()\s*["']ACTIVE["']/gi, severity: "error" },
  { name: "provider_string_check", pattern: /paymentProvider\s*(==|===|\.equals\()\s*["']iyzico["']/gi, severity: "warn" }
];

export type ForbiddenFinding = {
  path: string;
  rule: string;
  severity: "error" | "warn";
  match: string;
  index: number;
  line: number;
};

function lineNumberAt(content: string, index: number): number {
  return content.slice(0, Math.max(0, index)).split(/\r?\n/).length;
}

function isInsideMarkdownCodeFence(content: string, index: number): boolean {
  const before = content.slice(0, Math.max(0, index));
  const fenceCount = (before.match(/```/g) ?? []).length;
  return fenceCount % 2 === 1;
}

function getLine(content: string, index: number): string {
  const start = content.lastIndexOf("\n", Math.max(0, index)) + 1;
  const endRaw = content.indexOf("\n", index);
  const end = endRaw >= 0 ? endRaw : content.length;
  return content.slice(start, end);
}

function isIntentionalDocumentationExample(doc: DocItem, index: number): boolean {
  if (!doc.path.endsWith(".md")) return false;

  if (isInsideMarkdownCodeFence(doc.content, index)) return true;

  const line = getLine(doc.content, index).toLowerCase();

  return (
    line.includes("forbidden") ||
    line.includes("do not use") ||
    line.includes("no old") ||
    line.includes("must not") ||
    line.includes("yasak") ||
    line.includes("wrong:") ||
    line.includes("bad:")
  );
}

export function checkForbiddenTerms(docs: DocItem[]): ForbiddenFinding[] {
  const findings: ForbiddenFinding[] = [];

  for (const doc of docs) {
    for (const rule of forbiddenPatterns) {
      const matches = [...doc.content.matchAll(rule.pattern)];
      for (const match of matches) {
        const index = match.index ?? -1;

        if (index >= 0 && isIntentionalDocumentationExample(doc, index)) {
          continue;
        }

        findings.push({
          path: doc.path,
          rule: rule.name,
          severity: rule.severity,
          match: match[0],
          index,
          line: index >= 0 ? lineNumberAt(doc.content, index) : -1
        });
      }
    }
  }

  return findings;
}
