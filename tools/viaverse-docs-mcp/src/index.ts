import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import type { CallToolResult } from "@modelcontextprotocol/sdk/types.js";
import { pathToFileURL } from "node:url";
import { z } from "zod";
import { loadDocs, readDoc, scoreDoc, snippet } from "./docs.js";
import { checkForbiddenTerms } from "./forbidden.js";
import { buildContextPack, getRelatedUmls, createPreCodingBriefTemplate } from "./context.js";
import { resolveTaskContext, buildContextBundle, buildCompactSearchDocs } from "./task-router.js";
import { getDocOutline, findSections } from "./sections.js";
import { logMcpToolExchange } from "./logger.js";

type ToolHandler = (args: any) => Promise<CallToolResult>;

function registerLoggedTool(
  server: McpServer,
  name: string,
  inputSchema: Record<string, z.ZodTypeAny>,
  handler: ToolHandler
) {
  (server.tool as unknown as (
    toolName: string,
    schema: Record<string, z.ZodTypeAny>,
    callback: (args: any) => Promise<CallToolResult>
  ) => void)(name, inputSchema, async (args) => {
    const startedAt = Date.now();
    try {
      const result = await handler(args);
      await logMcpToolExchange({
        tool: name,
        input: args,
        output: result,
        durationMs: Date.now() - startedAt
      });
      return result;
    } catch (error) {
      await logMcpToolExchange({
        tool: name,
        input: args,
        error: error instanceof Error
          ? { name: error.name, message: error.message, stack: error.stack }
          : { message: String(error) },
        durationMs: Date.now() - startedAt
      });
      throw error;
    }
  });
}

export function createViaverseDocsServer(): McpServer {
const server = new McpServer({
  name: "viaverse-docs-mcp",
  version: "2.0.0"
});

registerLoggedTool(
  server,
  "get_project_orientation",
  {},
  async () => {
    return {
      content: [{
        type: "text",
        text: JSON.stringify({
          project: "Viaverse",
          implementation: "greenfield",
          rule: "Use MCP before coding. Return the smallest sufficient context, not the largest available context.",
          compactMode: {
            maxBoundedContextsPerTask: 2,
            maxCanonicalDocsPerTask: 3,
            maxSnippetsPerDoc: 1
          },
          implementationOrder: [
            "Repository / Gradle monorepo skeleton",
            "Backend Spring Boot platform-service foundation",
            "Mobile KMP/CMP foundation",
            "Backend health endpoint",
            "Mobile-to-backend health check",
            "Logging, env config, network client, interceptors",
            "AppResult/AppError and centralized error handling",
            "Token storage abstraction and session manager",
            "Auth and current-account vertical slices"
          ],
          mandatoryFlow: [
            "resolve_task_context",
            "get_context_bundle",
            "pre_coding_brief",
            "implement"
          ],
          compactGlobalDocs: [
            "AGENTS.md",
            "docs/blueprint/CLIENT_ARCHITECTURE.md",
            "docs/blueprint/BACKEND_ARCHITECTURE.md"
          ],
          scopeGuards: [
            "Do not include React template context for foundation, backend, auth, repository, logging, DB, domain, or infrastructure tasks unless explicitly requested.",
            "Do not continue Kotlin template completion unless explicitly requested.",
            "Recommend splitting tasks that need more than 2 bounded contexts."
          ],
          stopConditions: [
            "new bounded context",
            "new datastore",
            "new external provider",
            "breaking REST/event contract",
            "payment/security/privacy/moderation/identity impact",
            "unclear domain state",
            "hardcoded business string appears necessary"
          ]
        }, null, 2)
      }]
    };
  }
);

registerLoggedTool(
  server,
  "resolve_task_context",
  {
    task: z.string().min(3)
  },
  async ({ task }) => {
    return {
      content: [{
        type: "text",
        text: JSON.stringify(resolveTaskContext(task), null, 2)
      }]
    };
  }
);

registerLoggedTool(
  server,
  "get_context_bundle",
  {
    boundedContext: z.string().min(2),
    task: z.string().min(3),
    limit: z.number().int().min(5).max(20).default(10)
  },
  async ({ boundedContext, task, limit }) => {
    const docs = await loadDocs();
    return {
      content: [{
        type: "text",
        text: JSON.stringify({
          ...buildContextBundle(docs, boundedContext, task, Math.min(limit, 3)),
          searchedDocs: buildCompactSearchDocs(docs, boundedContext, task, 2)
        }, null, 2)
      }]
    };
  }
);

registerLoggedTool(
  server,
  "get_doc_outline",
  {
    filePath: z.string().min(1)
  },
  async ({ filePath }) => {
    const docs = await loadDocs();
    const doc = docs.find(d => d.path === filePath);
    if (!doc) throw new Error(`Document not found: ${filePath}`);
    return {
      content: [{
        type: "text",
        text: JSON.stringify({
          path: doc.path,
          title: doc.title,
          outline: getDocOutline(doc)
        }, null, 2)
      }]
    };
  }
);

registerLoggedTool(
  server,
  "read_doc_sections",
  {
    filePath: z.string().min(1),
    queries: z.array(z.string().min(1)).min(1),
    maxCharsPerSection: z.number().int().min(1000).max(20000).default(6000)
  },
  async ({ filePath, queries, maxCharsPerSection }) => {
    const docs = await loadDocs();
    const doc = docs.find(d => d.path === filePath);
    if (!doc) throw new Error(`Document not found: ${filePath}`);
    return {
      content: [{
        type: "text",
        text: JSON.stringify({
          path: doc.path,
          title: doc.title,
          queries,
          sections: findSections(doc, queries, maxCharsPerSection)
        }, null, 2)
      }]
    };
  }
);

registerLoggedTool(
  server,
  "search_docs",
  {
    query: z.string().min(2),
    boundedContext: z.string().optional(),
    docType: z.enum(["root", "blueprint", "adr", "uml", "api", "contract", "runbook", "template", "unknown"]).optional(),
    limit: z.number().int().min(1).max(20).default(8)
  },
  async ({ query, boundedContext, docType, limit }) => {
    const docs = await loadDocs();
    const results = docs
      .map(doc => ({
        path: doc.path,
        title: doc.title,
        type: doc.type,
        boundedContexts: doc.boundedContexts,
        score: scoreDoc(doc, query, boundedContext, docType),
        snippet: snippet(doc.content, query)
      }))
      .filter(x => x.score > 0)
      .sort((a, b) => b.score - a.score)
      .slice(0, limit);

    return { content: [{ type: "text", text: JSON.stringify({ query, boundedContext, docType, results }, null, 2) }] };
  }
);

registerLoggedTool(
  server,
  "read_doc",
  {
    filePath: z.string().min(1)
  },
  async ({ filePath }) => {
    const content = await readDoc(filePath);
    return { content: [{ type: "text", text: content }] };
  }
);

registerLoggedTool(
  server,
  "list_docs",
  {
    boundedContext: z.string().optional(),
    docType: z.enum(["root", "blueprint", "adr", "uml", "api", "contract", "runbook", "template", "unknown"]).optional()
  },
  async ({ boundedContext, docType }) => {
    const docs = await loadDocs();
    const filtered = docs.filter(doc => {
      const contextOk = boundedContext ? doc.boundedContexts.includes(boundedContext.toLowerCase()) : true;
      const typeOk = docType ? doc.type === docType : true;
      return contextOk && typeOk;
    });

    return {
      content: [{
        type: "text",
        text: JSON.stringify(filtered.map(doc => ({
          path: doc.path,
          title: doc.title,
          type: doc.type,
          boundedContexts: doc.boundedContexts
        })), null, 2)
      }]
    };
  }
);

registerLoggedTool(
  server,
  "get_related_umls",
  {
    boundedContext: z.string().min(2)
  },
  async ({ boundedContext }) => {
    return {
      content: [{
        type: "text",
        text: JSON.stringify({ boundedContext, related: getRelatedUmls(boundedContext) }, null, 2)
      }]
    };
  }
);

registerLoggedTool(
  server,
  "get_context_pack",
  {
    boundedContext: z.string().min(2),
    task: z.string().min(3),
    limit: z.number().int().min(3).max(15).default(8)
  },
  async ({ boundedContext, task, limit }) => {
    const docs = await loadDocs();
    const pack = buildContextPack(docs, boundedContext, task, limit);
    return { content: [{ type: "text", text: JSON.stringify(pack, null, 2) }] };
  }
);

registerLoggedTool(
  server,
  "pre_coding_brief",
  {
    boundedContext: z.string().min(2),
    task: z.string().min(3)
  },
  async ({ boundedContext, task }) => {
    return {
      content: [{
        type: "text",
        text: createPreCodingBriefTemplate(boundedContext, task)
      }]
    };
  }
);

registerLoggedTool(
  server,
  "check_forbidden_terms",
  {
    boundedContext: z.string().optional()
  },
  async ({ boundedContext }) => {
    const docs = await loadDocs();
    const filtered = boundedContext
      ? docs.filter(doc => doc.boundedContexts.includes(boundedContext.toLowerCase()))
      : docs;

    const findings = checkForbiddenTerms(filtered);
    const errors = findings.filter(x => x.severity === "error").length;
    const warnings = findings.filter(x => x.severity === "warn").length;

    return {
      content: [{
        type: "text",
        text: JSON.stringify({ errors, warnings, count: findings.length, findings }, null, 2)
      }]
    };
  }
);

return server;
}

export async function connectStdioServer() {
  const server = createViaverseDocsServer();
  const transport = new StdioServerTransport();
  await server.connect(transport);
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  await connectStdioServer();
}
