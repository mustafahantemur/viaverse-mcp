import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { StdioClientTransport } from "@modelcontextprotocol/sdk/client/stdio.js";
import path from "node:path";

const repoRoot =
  process.env.VIAVERSE_REPO_ROOT ??
  path.resolve(process.cwd(), "..", "..");

const transport = new StdioClientTransport({
  command: "node",
  args: ["dist/index.js"],
  env: {
    ...process.env,
    VIAVERSE_REPO_ROOT: repoRoot
  }
});

const client = new Client({
  name: "viaverse-docs-mcp-smoke-test",
  version: "2.0.0"
});

await client.connect(transport);

console.log("Connected to MCP.");

const tools = await client.listTools();
const toolNames = tools.tools.map(tool => tool.name);
console.log("Tools:");
console.log(toolNames);

const required = [
  "get_project_orientation",
  "resolve_task_context",
  "get_context_bundle",
  "get_doc_outline",
  "read_doc_sections",
  "search_docs",
  "read_doc",
  "list_docs",
  "get_related_umls",
  "get_context_pack",
  "pre_coding_brief",
  "check_forbidden_terms"
];

for (const name of required) {
  if (!toolNames.includes(name)) {
    throw new Error(`Missing MCP tool: ${name}`);
  }
}

const resolution = await client.callTool({
  name: "resolve_task_context",
  arguments: {
    task: "implement business publish flow"
  }
});

console.log("\nresolve_task_context result:");
console.log(JSON.stringify(resolution, null, 2));

const bundle = await client.callTool({
  name: "get_context_bundle",
  arguments: {
    boundedContext: "business",
    task: "implement business publish flow",
    limit: 10
  }
});

console.log("\nget_context_bundle result:");
console.log(JSON.stringify(bundle, null, 2));

const forbidden = await client.callTool({
  name: "check_forbidden_terms",
  arguments: {}
});

console.log("\ncheck_forbidden_terms result:");
console.log(JSON.stringify(forbidden, null, 2));

await client.close();
