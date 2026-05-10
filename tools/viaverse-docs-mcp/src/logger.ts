import { appendFile, mkdir } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const packageRoot = path.resolve(fileURLToPath(new URL(".", import.meta.url)), "..");
const defaultLogFile = path.join(packageRoot, "logs", "mcp-exchanges.jsonl");
const maxStringChars = Number(process.env.VIAVERSE_MCP_LOG_MAX_STRING_CHARS ?? 4000);
const maxArrayItems = Number(process.env.VIAVERSE_MCP_LOG_MAX_ARRAY_ITEMS ?? 24);

type McpToolExchange = {
  tool: string;
  input: unknown;
  output?: unknown;
  error?: unknown;
  durationMs: number;
};

export async function logMcpToolExchange(exchange: McpToolExchange) {
  const logFile = process.env.VIAVERSE_MCP_LOG_FILE ?? defaultLogFile;
  const sanitized = sanitize(exchange) as Record<string, unknown>;
  const event = {
    timestamp: new Date().toISOString(),
    kind: "tool_exchange",
    ...sanitized
  };

  await mkdir(path.dirname(logFile), { recursive: true });
  await appendFile(logFile, `${JSON.stringify(event)}\n`, "utf8");
}

function sanitize(value: unknown): unknown {
  if (typeof value === "string") {
    return truncate(value);
  }

  if (Array.isArray(value)) {
    const items = value.slice(0, maxArrayItems).map(sanitize);
    if (value.length > maxArrayItems) {
      items.push(`[truncated ${value.length - maxArrayItems} items]`);
    }
    return items;
  }

  if (value && typeof value === "object") {
    return Object.fromEntries(
      Object.entries(value).map(([key, item]) => [key, sanitize(item)])
    );
  }

  return value;
}

function truncate(value: string): string {
  if (value.length <= maxStringChars) {
    return value;
  }

  return `${value.slice(0, maxStringChars)}...[truncated ${value.length - maxStringChars} chars]`;
}
