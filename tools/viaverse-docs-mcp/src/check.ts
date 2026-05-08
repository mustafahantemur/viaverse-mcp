import { loadDocs } from "./docs.js";
import { checkForbiddenTerms } from "./forbidden.js";

const docs = await loadDocs();
const findings = checkForbiddenTerms(docs);
const errors = findings.filter(x => x.severity === "error");
const warnings = findings.filter(x => x.severity === "warn");

console.log(JSON.stringify({ errors: errors.length, warnings: warnings.length, findings }, null, 2));

if (errors.length > 0) {
  process.exit(1);
}
