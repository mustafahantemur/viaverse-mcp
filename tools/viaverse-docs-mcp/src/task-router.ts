import type { DocItem } from "./docs.js";
import { scoreDoc, snippet } from "./docs.js";
import { getRelatedUmls } from "./context.js";

export type TaskResolution = {
  task: string;
  inferredBoundedContexts: Array<{
    boundedContext: string;
    confidence: number;
    reasons: string[];
  }>;
  primaryBoundedContext: string;
  taskType: string;
  canonicalDocs: string[];
  relatedUmlParts: string[];
  searchQueries: string[];
  warnings: string[];
  requiredNextTools: string[];
};

type ContextRule = {
  context: string;
  keywords: string[];
  canonicalDocs: string[];
  taskTypes?: Record<string, string[]>;
};

const rules: ContextRule[] = [
  {
    context: "client",
    keywords: [
      "kotlin",
      "kmp",
      "compose",
      "mobile",
      "ui",
      "template",
      "react",
      "splash",
      "auth",
      "otp",
      "theme",
      "icon"
    ],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/CLIENT_ARCHITECTURE.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/adr/ADR-0004-no-hardcoded-business-strings.md",
      "docs/templates/react-to-kotlin-compose-migration.md",
      "templates/kotlin/viaverse-template/KOTLIN_TEMPLATE_GUARDRAILS.md"
    ],
    taskTypes: {
      kotlinTemplate: ["kotlin", "compose", "kmp", "template"],
      reactMigration: ["react", "migration", "convert", "port"],
      authUi: ["auth", "login", "otp", "password", "signup"],
      designSystem: ["theme", "style", "icon", "splash"]
    }
  },
  {
    context: "template",
    keywords: ["template", "react template", "kotlin template", "compose template", "migration", "showcase"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/CLIENT_ARCHITECTURE.md",
      "docs/templates/react-to-kotlin-compose-migration.md",
      "templates/kotlin/viaverse-template/KOTLIN_TEMPLATE_GUARDRAILS.md"
    ],
    taskTypes: {
      migration: ["react", "kotlin", "compose", "migration", "port"],
      guardrails: ["guardrail", "rules", "md", "review"]
    }
  },
  {
    context: "identity",
    keywords: ["login", "auth", "otp", "account", "capability", "session", "jwt", "google", "apple"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/blueprint/DATA_ARCHITECTURE.md",
      "docs/blueprint/SECURITY_MODEL.md",
      "docs/blueprint/BACKEND_ARCHITECTURE.md",
      "docs/adr/ADR-0004-no-hardcoded-business-strings.md",
      "docs/uml/viaverse_uml_part_02_capability/README.md",
      "docs/uml/viaverse_uml_part_05_worker_onboarding/README.md",
      "docs/uml/viaverse_uml_part_06_business_mode/README.md"
    ],
    taskTypes: {
      authentication: ["login", "otp", "session", "jwt", "auth"],
      capability: ["capability", "request work", "do work", "individual work", "work mode", "operate business", "business account"]
    }
  },
  {
    context: "marketplace",
    keywords: ["request", "job", "offer", "quote", "accept", "completion", "lifecycle", "feed"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/blueprint/BACKEND_ARCHITECTURE.md",
      "docs/blueprint/EVENT_ARCHITECTURE.md"
    ],
    taskTypes: {
      request: ["request", "post", "budget", "category"],
      offer: ["offer", "quote", "accept"],
      lifecycle: ["job", "complete", "cancel", "dispute"]
    }
  },
  {
    context: "business",
    keywords: ["business", "merchant", "publish", "branch", "catalog", "staff", "verification", "lead"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/blueprint/MONETIZATION_MODEL.md",
      "docs/blueprint/PAYMENT_MODEL.md",
      "docs/blueprint/BACKEND_ARCHITECTURE.md",
      "docs/adr/ADR-0004-no-hardcoded-business-strings.md",
      "docs/uml/viaverse_uml_part_06_business_mode/README.md",
      "docs/uml/viaverse_uml_part_22_business_account_merchant_ops/README.md",
      "docs/uml/viaverse_uml_part_23_monetization_subscription_ads_commission/README.md",
      "docs/uml/viaverse_uml_part_28_final_master_topology/README.md"
    ],
    taskTypes: {
      publish: ["publish", "verification", "catalog", "branch", "subscription"],
      catalog: ["catalog", "service", "package", "pricing"],
      staff: ["staff", "role", "invite", "permission"],
      leads: ["lead", "quote", "business quote"]
    }
  },
  {
    context: "payment",
    keywords: ["payment", "iyzico", "stripe", "masterpass", "card", "refund", "payout", "commission"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/PAYMENT_MODEL.md",
      "docs/blueprint/MONETIZATION_MODEL.md",
      "docs/blueprint/SECURITY_MODEL.md",
      "docs/blueprint/PRIVACY_AND_KVKK.md",
      "docs/contracts/providers/payment-providers.md"
    ],
    taskTypes: {
      provider: ["iyzico", "stripe", "masterpass", "provider"],
      refund: ["refund", "cancel"],
      payout: ["payout", "commission", "settlement"]
    }
  },
  {
    context: "chat",
    keywords: ["chat", "message", "conversation", "receipt", "typing", "cassandra", "moderation"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/DATA_ARCHITECTURE.md",
      "docs/blueprint/SECURITY_MODEL.md",
      "docs/blueprint/PRIVACY_AND_KVKK.md",
      "docs/uml/viaverse_uml_part_25_chat_messaging_safety/README.md"
    ],
    taskTypes: {
      messaging: ["message", "conversation", "receipt"],
      safety: ["moderation", "report", "off-platform", "personal info"]
    }
  },
  {
    context: "taxonomy",
    keywords: ["taxonomy", "category", "tag", "dynamic environment", "form schema", "category suggestion"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/blueprint/CLIENT_ARCHITECTURE.md",
      "docs/uml/viaverse_uml_part_24_category_taxonomy_dynamic_environment/README.md"
    ]
  },
  {
    context: "search",
    keywords: ["search", "opensearch", "ranking", "matching", "seo", "discovery", "index"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/DATA_ARCHITECTURE.md",
      "docs/blueprint/SEO_AND_GROWTH.md",
      "docs/uml/viaverse_uml_part_05_search_discovery_matching/README.md"
    ]
  },
  {
    context: "infrastructure",
    keywords: ["aws", "eks", "kubernetes", "deploy", "docker", "observability", "otel", "prometheus", "jaeger"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/blueprint/ARCHITECTURE.md",
      "docs/blueprint/TECH_STACK_DECISIONS.md",
      "docs/runbooks/one-command-bootstrap.md"
    ]
  }
];

function normalize(input: string): string {
  return input.toLowerCase();
}

function inferTaskType(rule: ContextRule, task: string): string {
  if (!rule.taskTypes) return "general";

  const lower = normalize(task);
  let best = "general";
  let bestScore = 0;

  for (const [type, keywords] of Object.entries(rule.taskTypes)) {
    const score = keywords.filter(keyword => lower.includes(keyword)).length;
    if (score > bestScore) {
      best = type;
      bestScore = score;
    }
  }

  return best;
}

export function resolveTaskContext(task: string): TaskResolution {
  const lower = normalize(task);

  const candidates = rules
    .map(rule => {
      const matched = rule.keywords.filter(keyword => lower.includes(keyword));
      const confidence = matched.length / Math.max(1, rule.keywords.length);
      return {
        boundedContext: rule.context,
        confidence: matched.length === 0 ? 0 : Math.min(0.95, 0.35 + matched.length * 0.12),
        reasons: matched
      };
    })
    .filter(x => x.confidence > 0)
    .sort((a, b) => b.confidence - a.confidence);

  const primary = candidates[0]?.boundedContext ?? "architecture";
  const primaryRule = rules.find(rule => rule.context === primary);

  const canonicalDocs = primaryRule?.canonicalDocs ?? [
    "AGENTS.md",
    "CODING_RULES.md",
    "docs/DOCS_INDEX.md",
    "docs/blueprint/ARCHITECTURE.md"
  ];

  const warnings: string[] = [];
  if (candidates.length === 0) {
    warnings.push("No clear bounded context inferred. Start with architecture docs and ask for human clarification if implementation scope is unclear.");
  }
  if (candidates.length > 1 && candidates[1].confidence > 0.5) {
    warnings.push(`Task may involve multiple contexts: ${candidates.map(x => x.boundedContext).join(", ")}.`);
  }

  return {
    task,
    inferredBoundedContexts: candidates.length > 0 ? candidates : [{ boundedContext: primary, confidence: 0.2, reasons: ["fallback"] }],
    primaryBoundedContext: primary,
    taskType: primaryRule ? inferTaskType(primaryRule, task) : "general",
    canonicalDocs,
    relatedUmlParts: getRelatedUmls(primary),
    searchQueries: [
      task,
      `${primary} ${task}`,
      `${primary} policy state enum`,
      `${primary} REST event contract`
    ],
    warnings,
    requiredNextTools: [
      "get_context_bundle",
      "pre_coding_brief"
    ]
  };
}

export function buildContextBundle(docs: DocItem[], boundedContext: string, task: string, limit = 10) {
  const resolution = resolveTaskContext(task);
  const context = boundedContext.toLowerCase();

  const canonicalPaths = new Set(
    (rules.find(rule => rule.context === context)?.canonicalDocs ?? resolution.canonicalDocs)
  );

  const canonicalDocs = docs
    .filter(doc => canonicalPaths.has(doc.path))
    .map(doc => ({
      path: doc.path,
      title: doc.title,
      type: doc.type,
      reason: "canonical",
      snippet: snippet(doc.content, task)
    }));

  const searchedDocs = docs
    .filter(doc => !canonicalPaths.has(doc.path))
    .map(doc => ({
      path: doc.path,
      title: doc.title,
      type: doc.type,
      score: scoreDoc(doc, task, context),
      snippet: snippet(doc.content, task)
    }))
    .filter(x => x.score > 0)
    .sort((a, b) => b.score - a.score)
    .slice(0, Math.max(0, limit - canonicalDocs.length));

  return {
    resolution,
    boundedContext: context,
    task,
    canonicalDocs,
    searchedDocs,
    relatedUmlParts: getRelatedUmls(context)
  };
}
