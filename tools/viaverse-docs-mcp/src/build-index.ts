import fs from "node:fs/promises";
import path from "node:path";
import { REPO_ROOT } from "./config.js";
import { loadDocs } from "./docs.js";

const docs = await loadDocs();

const index = docs.map(doc => ({
  path: doc.path,
  title: doc.title,
  type: doc.type,
  boundedContexts: doc.boundedContexts,
  size: doc.content.length
}));

const outputPath = path.join(REPO_ROOT, "docs", "docs-index.generated.json");
await fs.writeFile(outputPath, JSON.stringify(index, null, 2), "utf8");

console.log(`Generated ${outputPath} with ${index.length} docs.`);
