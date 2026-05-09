import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";
import { loadDocs, readDoc, scoreDoc, snippet } from "./docs.js";
import { checkForbiddenTerms } from "./forbidden.js";
import { buildContextPack, getRelatedUmls, createPreCodingBriefTemplate } from "./context.js";
import { resolveTaskContext, buildContextBundle } from "./task-router.js";
import { getDocOutline, findSections } from "./sections.js";

const server = new McpServer({
  name: "viaverse-docs-mcp",
  version: "2.0.0"
});

server.tool(
  "get_project_orientation",
  {},
  async () => {
    return {
      content: [{
        type: "text",
        text: JSON.stringify({
          project: "Viaverse",
          implementation: "greenfield",
          rule: "Use MCP before coding. Do not load all docs manually.",
          mandatoryFlow: [
            "resolve_task_context",
            "get_context_bundle",
            "pre_coding_brief",
            "implement"
          ],
          globalDocs: [
            "README.md",
            "AGENTS.md",
            "CODING_RULES.md",
            "docs/DOCS_INDEX.md",
            "docs/uml/UML_INDEX.md"
          ],
          accountCapabilityRules: [
            "One real Account can request work, do individual work, and operate business resources.",
            "Personal/requester and individual Work Mode should feel like a lightweight Airbnb-style become-a-host transition after worker onboarding.",
            "Business is a separate workspace/resource flow on the same Account: BusinessAccount/profile, merchant onboarding, verification, staff roles, catalog, subscription, and publish-ready policy.",
            "Do not model customer, individual worker, and business as three separate login identities.",
            "Do not treat Business Mode as the same lightweight switch as individual Work Mode."
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

server.tool(
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

server.tool(
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
        text: JSON.stringify(buildContextBundle(docs, boundedContext, task, limit), null, 2)
      }]
    };
  }
);

server.tool(
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

server.tool(
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

server.tool(
  "search_docs",
  {
    query: z.string().min(2),
    boundedContext: z.string().optional(),
    docType: z.enum(["root", "blueprint", "adr", "uml", "api", "contract", "runbook", "unknown"]).optional(),
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

server.tool(
  "read_doc",
  {
    filePath: z.string().min(1)
  },
  async ({ filePath }) => {
    const content = await readDoc(filePath);
    return { content: [{ type: "text", text: content }] };
  }
);

server.tool(
  "list_docs",
  {
    boundedContext: z.string().optional(),
    docType: z.enum(["root", "blueprint", "adr", "uml", "api", "contract", "runbook", "unknown"]).optional()
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

server.tool(
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

server.tool(
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

server.tool(
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

server.tool(
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

const transport = new StdioServerTransport();
await server.connect(transport);
