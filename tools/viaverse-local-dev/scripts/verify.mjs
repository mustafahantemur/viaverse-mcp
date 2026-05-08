import fs from "node:fs";
import path from "node:path";
import { spawnSync } from "node:child_process";

const repoRoot = process.cwd();

const required = [
  "README.md",
  "AGENTS.md",
  "CODING_RULES.md",
  "AI_CODING_PROMPT.md",
  "MCP_SETUP_AND_USAGE.md",
  "docs/DOCS_INDEX.md",
  "docs/blueprint/PRODUCT_MODEL.md",
  "docs/blueprint/ARCHITECTURE.md",
  "docs/blueprint/BACKEND_ARCHITECTURE.md",
  "docs/blueprint/CLIENT_ARCHITECTURE.md",
  "docs/blueprint/TECH_STACK_DECISIONS.md",
  "docs/uml/UML_INDEX.md",
  "docs/adr/README.md",
  "docs/contracts/README.md",
  "docs/api/README.md",
  "docs/runbooks/README.md",
  "tools/viaverse-docs-mcp/package.json",
  "tools/viaverse-docs-mcp/src/index.ts",
  "tools/viaverse-docs-mcp/src/config.ts",
  "tools/viaverse-docs-mcp/src/docs.ts",
  "tools/viaverse-docs-mcp/src/context.ts",
  "tools/viaverse-docs-mcp/src/forbidden.ts",
  "tools/viaverse-docs-mcp/src/build-index.ts",
  "tools/viaverse-docs-mcp/src/check.ts",
  "tools/viaverse-docs-mcp/src/smoke-test.ts"
];

let failed = false;

for (const rel of required) {
  const ok = fs.existsSync(path.join(repoRoot, rel));
  console.log(`${ok ? "OK  " : "MISS"} ${rel}`);
  if (!ok) failed = true;
}

const rootBlueprintDuplicates = [
  "PRODUCT_MODEL.md",
  "TECH_STACK_DECISIONS.md",
  "ARCHITECTURE.md",
  "BACKEND_ARCHITECTURE.md",
  "CLIENT_ARCHITECTURE.md",
  "DATA_ARCHITECTURE.md",
  "EVENT_ARCHITECTURE.md",
  "SECURITY_MODEL.md",
  "PAYMENT_MODEL.md",
  "PRIVACY_AND_KVKK.md",
  "MONETIZATION_MODEL.md",
  "GAMIFICATION_AND_TRUST.md",
  "SEO_AND_GROWTH.md",
  "ROADMAP.md"
].filter(rel => fs.existsSync(path.join(repoRoot, rel)));

if (rootBlueprintDuplicates.length > 0) {
  console.log("\nRoot-level blueprint duplicates found. Move/delete these; source of truth is docs/blueprint/:");
  for (const rel of rootBlueprintDuplicates) console.log(`DUP  ${rel}`);
  failed = true;
}

function run(command, args, cwd) {
  const result = spawnSync(command, args, {
    cwd,
    env: { ...process.env, VIAVERSE_REPO_ROOT: repoRoot },
    stdio: "inherit",
    shell: process.platform === "win32"
  });
  if (result.status !== 0) failed = true;
}

const mcpDir = path.join(repoRoot, "tools", "viaverse-docs-mcp");
if (fs.existsSync(path.join(mcpDir, "package.json"))) {
  run("npm", ["run", "build"], mcpDir);
  run("npm", ["run", "docs:index"], mcpDir);
  run("npm", ["run", "docs:check"], mcpDir);
}

if (failed) {
  console.error("\nViaverse verification failed.");
  process.exit(1);
}

console.log("\nViaverse verification passed.");
