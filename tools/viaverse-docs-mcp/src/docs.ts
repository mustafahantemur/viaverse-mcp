import fg from "fast-glob";
import fs from "node:fs/promises";
import path from "node:path";
import {
  CANONICAL_CONTEXT_DOC_PRIORITY,
  DEPRECATED_UML_HINTS,
  DOC_GLOBS,
  IGNORE_GLOBS,
  MAX_DOC_CHARS,
  REPO_ROOT
} from "./config.js";

export type DocType =
  | "root"
  | "blueprint"
  | "adr"
  | "uml"
  | "api"
  | "contract"
  | "runbook"
  | "template"
  | "unknown";

export type DocItem = {
  path: string;
  title: string;
  type: DocType;
  boundedContexts: string[];
  content: string;
  lower: string;
};

const contextKeywords: Record<string, string[]> = {
  identity: ["identity", "identity-service", "account", "auth", "otp", "login", "capability", "session", "jwt", "refresh token"],
  profile: ["profile", "settings", "locale", "privacy"],
  taxonomy: ["taxonomy", "category", "tag", "dynamic environment"],
  marketplace: ["marketplace", "request", "offer", "quote", "job"],
  business: ["business", "merchant", "branch", "catalog", "staff", "publish"],
  payment: ["payment", "iyzico", "stripe", "masterpass", "refund", "payout"],
  monetization: ["monetization", "subscription", "commission", "ads", "sponsored"],
  chat: ["chat", "message", "conversation", "receipt"],
  media: ["media", "cdn", "image", "video", "s3", "object storage", "object-storage", "seaweedfs"],
  "media-storage": ["media-storage", "object storage", "object-storage", "s3", "seaweedfs", "minio", "bucket", "presigned"],
  trust: ["trust", "review", "rating", "badge"],
  search: ["search", "opensearch", "seo", "ranking"],
  observability: ["observability", "logging", "logs", "opentelemetry", "otel", "trace", "tracing", "micrometer", "actuator", "prometheus", "audit_log", "stdout", "ecs"],
  security: ["security", "kvkk", "privacy", "rate limit", "audit", "jwt", "oauth2", "resource server", "nimbus", "abuse"],
  compliance: ["compliance", "kvkk", "privacy", "audit", "license", "agpl", "gpl", "lgpl", "sspl", "sensitive"],
  infrastructure: ["infrastructure", "aws", "eks", "kubernetes", "deployment", "docker compose", "local infra"],
  "local-infra": ["local-infra", "local infra", "docker compose", "opensearch", "seaweedfs", "postgresql", "flyway", "bootrun", "debug"],
  ai: ["ai", "assistant", "mcp", "rag", "support"],
  api: ["api", "openapi", "bff", "rest", "contract"],
  client: ["client", "mobile", "kotlin", "kmp", "compose", "react", "template", "ui", "theme"],
  template: ["template", "react", "kotlin", "compose", "migration", "splash", "auth", "otp"]
};

export function getDocType(filePath: string): DocType {
  if (filePath.startsWith("docs/blueprint/")) return "blueprint";
  if (filePath.startsWith("docs/adr/")) return "adr";
  if (filePath.startsWith("docs/uml/")) return "uml";
  if (filePath.startsWith("docs/api/")) return "api";
  if (filePath.startsWith("docs/contracts/")) return "contract";
  if (filePath.startsWith("docs/runbooks/")) return "runbook";
  if (filePath.startsWith("docs/templates/")) return "template";
  if (filePath.startsWith("templates/")) return "template";
  if (!filePath.includes("/")) return "root";
  return "unknown";
}

export function inferBoundedContexts(filePath: string, content: string): string[] {
  const haystack = `${filePath}\n${content}`.toLowerCase();
  const found: string[] = [];

  for (const [context, keywords] of Object.entries(contextKeywords)) {
    if (keywords.some(keyword => haystack.includes(keyword))) {
      found.push(context);
    }
  }

  return found;
}

export async function loadDocs(): Promise<DocItem[]> {
  const entries = await fg(DOC_GLOBS, {
    cwd: REPO_ROOT,
    absolute: false,
    onlyFiles: true,
    ignore: IGNORE_GLOBS
  });

  const docs: DocItem[] = [];

  for (const relativePath of entries.sort()) {
    const normalized = relativePath.replaceAll("\\", "/");
    const absolutePath = path.join(REPO_ROOT, normalized);
    let content = await fs.readFile(absolutePath, "utf8");

    if (content.length > MAX_DOC_CHARS) {
      content = content.slice(0, MAX_DOC_CHARS) + "\n\n[TRUNCATED_BY_MCP]";
    }

    const firstHeading =
      content.match(/^#\s+(.+)$/m)?.[1]?.trim() ??
      path.basename(normalized);

    docs.push({
      path: normalized,
      title: firstHeading,
      type: getDocType(normalized),
      boundedContexts: inferBoundedContexts(normalized, content),
      content,
      lower: content.toLowerCase()
    });
  }

  return docs;
}

export function safeRepoPath(filePath: string): string {
  const normalized = path.normalize(filePath).replaceAll("\\", "/");

  if (normalized.startsWith("../") || normalized === ".." || path.isAbsolute(normalized)) {
    throw new Error("Invalid path. Only repo-relative paths are allowed.");
  }

  if (
    normalized.includes("/.git/") ||
    normalized.includes("node_modules/") ||
    normalized.includes("/dist/") ||
    normalized.endsWith(".env") ||
    normalized.includes(".env.") ||
    normalized.endsWith(".pem") ||
    normalized.endsWith(".key") ||
    normalized.includes("/secrets/") ||
    normalized.includes("/credentials/")
  ) {
    throw new Error("Path is blocked by MCP safety policy.");
  }

  return normalized;
}

export async function readDoc(filePath: string): Promise<string> {
  const normalized = safeRepoPath(filePath);
  const absolutePath = path.join(REPO_ROOT, normalized);
  return fs.readFile(absolutePath, "utf8");
}

function canonicalPriorityScore(docPath: string, boundedContext?: string): number {
  if (!boundedContext) return 0;
  const context = boundedContext.toLowerCase();

  const canonical = CANONICAL_CONTEXT_DOC_PRIORITY[context] ?? [];
  const canonicalIndex = canonical.indexOf(docPath);
  if (canonicalIndex >= 0) return 100 - canonicalIndex * 5;

  const deprecated = DEPRECATED_UML_HINTS[context] ?? [];
  if (deprecated.includes(docPath)) return -40;

  return 0;
}

export function scoreDoc(doc: DocItem, query: string, boundedContext?: string, type?: DocType): number {
  const terms = query.toLowerCase().split(/\s+/).map(x => x.trim()).filter(Boolean);
  let score = canonicalPriorityScore(doc.path, boundedContext);

  if (boundedContext && doc.boundedContexts.includes(boundedContext.toLowerCase())) score += 12;
  if (type && doc.type === type) score += 7;

  for (const term of terms) {
    if (doc.path.toLowerCase().includes(term)) score += 6;
    if (doc.title.toLowerCase().includes(term)) score += 5;
    if (doc.lower.includes(term)) score += 1;
  }

  return score;
}

export function snippet(content: string, query: string): string {
  const terms = query.toLowerCase().split(/\s+/).filter(Boolean);
  const lower = content.toLowerCase();

  let index = -1;
  for (const term of terms) {
    index = lower.indexOf(term);
    if (index >= 0) break;
  }

  if (index < 0) return content.slice(0, 900).replace(/\s+/g, " ").trim();

  const start = Math.max(0, index - 300);
  const end = Math.min(content.length, index + 900);

  return content.slice(start, end).replace(/\s+/g, " ").trim();
}
