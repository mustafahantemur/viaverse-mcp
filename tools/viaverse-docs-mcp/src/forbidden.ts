import type { DocItem } from "./docs.js";

export const forbiddenPatterns: Array<{ name: string; pattern: RegExp; severity: "error" | "warn" }> = [
  { name: "forbidden_old_mvp_language", pattern: /old\s+MVP|previous\s+MVP|legacy\s+MVP|eski\s+MVP|mevcut\s+MVP/gi, severity: "error" },
  { name: "prototype_as_implementation_language", pattern: /reuse\s+prototype\s+structure|continue\s+prototype/gi, severity: "error" },
  { name: "hardcoded_customer_check", pattern: /userType\s*(==|===|\.equals\()\s*["']customer["']/gi, severity: "error" },
  { name: "hardcoded_provider_check", pattern: /role\s*(==|===|\.equals\()\s*["']provider["']/gi, severity: "error" },
  { name: "hardcoded_active_status", pattern: /status\s*(==|===|\.equals\()\s*["']ACTIVE["']/gi, severity: "error" },
  { name: "provider_string_check", pattern: /paymentProvider\s*(==|===|\.equals\()\s*["']iyzico["']/gi, severity: "warn" },
  { name: "minio_dependency", pattern: /\bminio\b/gi, severity: "error" },
  { name: "non_permissive_infra_license", pattern: /\b(AGPL|GPL|LGPL|SSPL|network copyleft|strong copyleft|source-available)\b/gi, severity: "warn" },
  { name: "file_based_app_log", pattern: /\b(app\.log|debug\.log|errors\.txt|local\.txt)\b/gi, severity: "error" },
  { name: "manual_jwt_implementation", pattern: /\b(Base64\.getUrlEncoder|Base64\.getUrlDecoder|HmacSHA256|JWT header|JWT payload|manual(?:ly)? (?:sign|parse|verify|build).{0,40}JWT)\b/gi, severity: "warn" },
  { name: "literal_service_exception", pattern: /\b(UnauthorizedException|ValidationException)\s*\(\s*["'][^"']+["']\s*\)/g, severity: "warn" }
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

function getNearbyText(content: string, index: number): string {
  const before = content.slice(0, Math.max(0, index)).split(/\r?\n/);
  const after = content.slice(index).split(/\r?\n/);
  return [...before.slice(-35), ...after.slice(0, 3)].join("\n").toLowerCase();
}

function isIntentionalDocumentationExample(doc: DocItem, index: number): boolean {
  if (!doc.path.endsWith(".md")) return false;

  if (isInsideMarkdownCodeFence(doc.content, index)) return true;
  if (doc.path.includes("ADR-0006-seaweedfs-local-object-storage.md")) return true;

  const line = getLine(doc.content, index).toLowerCase();
  const nearby = getNearbyText(doc.content, index);

  return (
    line.includes("forbidden") ||
    line.includes("do not") ||
    line.includes("do not use") ||
    line.includes("no ") ||
    line.includes("must not") ||
    line.includes("must be") ||
    line.includes("must not be used") ||
    line.includes("removed") ||
    line.includes("not acceptable") ||
    line.includes("not default") ||
    line.includes("is not") ||
    line.includes("avoid") ||
    line.includes("prefer") ||
    line.includes("allowed") ||
    line.includes("approval") ||
    line.includes("unless adr") ||
    line.includes("requires explicit adr") ||
    line.includes("recommend") ||
    line.includes("guardrail") ||
    line.includes("ban") ||
    line.includes("risk") ||
    line.includes("yasak") ||
    line.includes("wrong:") ||
    line.includes("bad:") ||
    nearby.includes("forbidden patterns") ||
    nearby.includes("never introduce") ||
    nearby.includes("non-negotiable") ||
    nearby.includes("avoid unless") ||
    nearby.includes("avoid prompts") ||
    nearby.includes("license policy") ||
    nearby.includes("guardrails") ||
    nearby.includes("do not introduce") ||
    nearby.includes("must not be used")
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
