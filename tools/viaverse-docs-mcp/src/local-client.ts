import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { StreamableHTTPClientTransport } from "@modelcontextprotocol/sdk/client/streamableHttp.js";

const endpoint = process.env.VIAVERSE_MCP_ENDPOINT ?? "http://127.0.0.1:6275/mcp";
const toolName = process.argv[2] ?? "resolve_task_context";
const toolArgs = process.argv[3] ? JSON.parse(process.argv[3]) : {};

const client = new Client({
  name: "viaverse-docs-mcp-local-client",
  version: "2.0.0"
});

const transport = new StreamableHTTPClientTransport(new URL(endpoint));
await client.connect(transport);

const result = await client.callTool({
  name: toolName,
  arguments: toolArgs
});

console.log(JSON.stringify(result, null, 2));

await client.close();
