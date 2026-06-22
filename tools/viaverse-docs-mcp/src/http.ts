import { StreamableHTTPServerTransport } from "@modelcontextprotocol/sdk/server/streamableHttp.js";
import { createMcpExpressApp } from "@modelcontextprotocol/sdk/server/express.js";
import { createViaverseDocsServer } from "./index.js";

const host = process.env.VIAVERSE_MCP_HOST ?? "127.0.0.1";
const port = Number(process.env.VIAVERSE_MCP_PORT ?? 6275);

const app = createMcpExpressApp({ host });

type HttpRequest = {
  body?: unknown;
};

type HttpResponse = {
  headersSent: boolean;
  json: (body: unknown) => unknown;
  on: (event: string, callback: () => void) => unknown;
  status: (code: number) => HttpResponse;
};

app.get("/health", (_req: HttpRequest, res: HttpResponse) => {
  res.json({
    ok: true,
    name: "viaverse-docs-mcp",
    transport: "streamable-http",
    endpoint: "/mcp"
  });
});

app.post("/mcp", async (req: HttpRequest, res: HttpResponse) => {
  const server = createViaverseDocsServer();
  const transport = new StreamableHTTPServerTransport({
    sessionIdGenerator: undefined
  });

  try {
    await server.connect(transport);
    await transport.handleRequest(req as any, res as any, req.body);
  } catch (error) {
    console.error("Error handling MCP HTTP request:", error);
    if (!res.headersSent) {
      res.status(500).json({
        jsonrpc: "2.0",
        error: {
          code: -32603,
          message: "Internal server error"
        },
        id: null
      });
    }
  } finally {
    res.on("close", () => {
      void transport.close();
      void server.close();
    });
  }
});

app.get("/mcp", (_req: HttpRequest, res: HttpResponse) => {
  res.status(405).json({
    jsonrpc: "2.0",
    error: {
      code: -32000,
      message: "Method not allowed."
    },
    id: null
  });
});

app.delete("/mcp", (_req: HttpRequest, res: HttpResponse) => {
  res.status(405).json({
    jsonrpc: "2.0",
    error: {
      code: -32000,
      message: "Method not allowed."
    },
    id: null
  });
});

const httpServer = app.listen(port, host, () => {
  console.error(`Viaverse Docs MCP listening on http://${host}:${port}/mcp`);
});

httpServer.on("error", (error: Error) => {
  console.error("Failed to start Viaverse Docs MCP HTTP server:", error);
  process.exit(1);
});

process.on("SIGINT", () => {
  httpServer.close(() => process.exit(0));
});
