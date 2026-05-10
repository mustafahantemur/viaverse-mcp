import type { DocItem } from "./docs.js";
import { scoreDoc, snippet } from "./docs.js";
import { getRelatedUmls } from "./context.js";

type RuleSummary = {
  code: string;
  text: string;
};

type SourceReference = {
  path: string;
  reason: string;
};

type PreCodingBrief = {
  affectedModulesFiles: string[];
  domainModelsEnumsValueObjects: string[];
  repositoryContracts: string[];
  apiRestContracts: string[];
  localStorageCacheImpact: string[];
  interceptorsSessionTokenBehavior: string[];
  loggingObservabilityImpact: string[];
  errorsValidation: string[];
  testsToAdd: string[];
  validationCommands: string[];
  risksOpenQuestions: string[];
};

type ScopeGuard = {
  outOfScope: string[];
  warning?: string;
};

export type TaskResolution = {
  task: string;
  primaryBoundedContext: string;
  secondaryBoundedContext?: string;
  taskType: string;
  implementationPhase: string;
  canonicalDocs: string[];
  relatedUmlParts: string[];
  warnings: string[];
  proceedDecision: "Proceed" | "Split task first" | "Missing information, ask for clarification";
};

export type MinimalContextResponse = {
  resolvedContext: TaskResolution;
  minimalRelevantRules: RuleSummary[];
  requiredSourceReferences: SourceReference[];
  preCodingBrief: PreCodingBrief;
  scopeGuard: ScopeGuard;
  proceedDecision: TaskResolution["proceedDecision"];
  compactText: string;
};

type ContextRule = {
  context: string;
  keywords: string[];
  canonicalDocs: string[];
  taskTypes?: Record<string, string[]>;
};

const FOUNDATION_DOCS = [
  "AGENTS.md",
  "docs/blueprint/ARCHITECTURE.md",
  "docs/blueprint/CLIENT_ARCHITECTURE.md",
  "docs/blueprint/BACKEND_ARCHITECTURE.md",
  "docs/blueprint/PRODUCT_MODEL.md",
  "docs/adr/ADR-0002-kotlin-multiplatform-client.md",
  "docs/adr/ADR-0004-no-hardcoded-business-strings.md"
];

const rules: ContextRule[] = [
  {
    context: "foundation",
    keywords: ["greenfield", "foundation", "monorepo", "gradle", "skeleton", "platform-service", "health endpoint", "health check"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/ARCHITECTURE.md",
      "docs/blueprint/BACKEND_ARCHITECTURE.md"
    ],
    taskTypes: {
      monorepo: ["gradle", "monorepo", "skeleton", "module"],
      verticalSlice: ["health", "endpoint", "check", "client"]
    }
  },
  {
    context: "client",
    keywords: ["kotlin", "kmp", "compose", "mobile", "ui", "screen", "theme", "icon", "navigation"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/CLIENT_ARCHITECTURE.md",
      "docs/adr/ADR-0002-kotlin-multiplatform-client.md"
    ],
    taskTypes: {
      foundation: ["foundation", "app", "mobile", "ktor", "session", "storage"],
      ui: ["screen", "ui", "compose", "theme", "navigation"]
    }
  },
  {
    context: "identity",
    keywords: ["login", "auth", "otp", "account", "capability", "session", "jwt", "register", "logout", "me"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/SECURITY_MODEL.md",
      "docs/adr/ADR-0004-no-hardcoded-business-strings.md"
    ],
    taskTypes: {
      authentication: ["login", "register", "otp", "session", "logout", "me"],
      profile: ["profile", "settings", "account"]
    }
  },
  {
    context: "marketplace",
    keywords: ["request", "job", "offer", "quote", "accept", "completion", "lifecycle"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/blueprint/BACKEND_ARCHITECTURE.md"
    ]
  },
  {
    context: "business",
    keywords: ["business", "merchant", "publish", "branch", "catalog", "staff", "verification"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/uml/viaverse_uml_part_22_business_account_merchant_ops/README.md"
    ]
  },
  {
    context: "payment",
    keywords: ["payment", "iyzico", "stripe", "masterpass", "refund", "payout", "commission"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/PAYMENT_MODEL.md",
      "docs/blueprint/SECURITY_MODEL.md"
    ]
  },
  {
    context: "chat",
    keywords: ["chat", "message", "conversation", "moderation"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/uml/viaverse_uml_part_25_chat_messaging_safety/README.md",
      "docs/blueprint/SECURITY_MODEL.md"
    ]
  },
  {
    context: "search",
    keywords: ["search", "opensearch", "matching", "seo", "discovery", "index"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/DATA_ARCHITECTURE.md",
      "docs/blueprint/SEO_AND_GROWTH.md"
    ]
  }
];

const FOUNDATION_SCOPE_EXCLUSIONS = [
  "marketplace",
  "payment",
  "chat",
  "review",
  "support",
  "business workspace",
  "explore feed",
  "social feed",
  "full mocked workflows",
  "React-to-Kotlin migration details"
];

const EARLY_TEST_RULE = "Defer detailed unit tests; use Gradle check, manual smoke validation, and add focused tests only for critical auth/session/security behavior.";

const GREENFIELD_RULES: RuleSummary[] = [
  { code: "greenfield", text: "Treat Viaverse as a greenfield implementation; prototypes are visual/product references only." },
  { code: "gradle-first", text: "Use Gradle Kotlin DSL for backend, mobile, and root orchestration." },
  { code: "typed-domain", text: "No hardcoded business role/status/type/capability/payment/request strings; use enums/value objects." },
  { code: "bounded-context", text: "Default maximum is 2 bounded contexts per task; split broader work." },
  { code: "small-context", text: "Default maximum is 3 canonical docs and 1 short snippet per doc." },
  { code: "react-guard", text: "React template is allowed only for UI/screen tasks; never as architecture or domain source of truth." },
  { code: "thin-scope", text: "For foundation/auth/profile tasks, exclude marketplace, payment, chat, review, support, business workspace, and full social flows." },
  { code: "localize", text: "User-facing strings must be centralized/localized; business logic must not depend on display text." },
  { code: "product-balance", text: "Preserve Dinamik Cevre and Hizmet Al as separate product surfaces; do not collapse Viaverse into only a formal job marketplace." },
  { code: "test-budget", text: EARLY_TEST_RULE }
];

function normalize(input: string): string {
  return input.toLowerCase();
}

function tokenize(text: string): string[] {
  return normalize(text).split(/[^a-z0-9ğüşöçıİĞÜŞÖÇ_-]+/i).filter(Boolean);
}

function includesKeyword(text: string, keyword: string): boolean {
  const lowerText = normalize(text);
  const lowerKeyword = normalize(keyword);
  if (lowerKeyword.includes(" ")) {
    return lowerText.includes(lowerKeyword);
  }
  return tokenize(lowerText).includes(lowerKeyword);
}

function includesAny(text: string, needles: string[]): boolean {
  return needles.some(needle => includesKeyword(text, needle));
}

function inferTaskType(rule: ContextRule | undefined, task: string): string {
  if (!rule?.taskTypes) return "general";
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

function inferImplementationPhase(task: string): string {
  const lower = normalize(task);
  if (includesAny(lower, ["monorepo", "gradle", "skeleton", "foundation"])) return "Repository / monorepo foundation";
  if (includesAny(lower, ["platform-service", "spring boot", "health endpoint", "backend foundation"])) return "Backend platform-service foundation";
  if (includesAny(lower, ["kmp", "compose", "mobile foundation", "health check", "ktor"])) return "Mobile foundation";
  if (includesAny(lower, ["auth", "login", "register", "logout", "session", "me"])) return "Auth and session vertical slice";
  if (includesAny(lower, ["profile", "settings"])) return "Profile and settings vertical slice";
  return "Foundation-first implementation";
}

function inferContexts(task: string) {
  const lower = normalize(task);
  return rules
    .map(rule => {
      const matched = rule.keywords.filter(keyword => includesKeyword(lower, keyword));
      return {
        boundedContext: rule.context,
        confidence: matched.length === 0 ? 0 : Math.min(0.95, 0.35 + matched.length * 0.12),
        reasons: matched
      };
    })
    .filter(item => item.confidence > 0)
    .sort((a, b) => b.confidence - a.confidence);
}

function inferProceedDecision(task: string, contexts: Array<{ boundedContext: string; confidence: number }>): TaskResolution["proceedDecision"] {
  const lower = normalize(task);
  if (includesAny(lower, ["everything", "all flows", "full app", "complete platform"])) {
    return "Split task first";
  }
  if (contexts.length > 2) {
    return "Split task first";
  }
  if (includesAny(lower, ["unknown provider", "undecided provider", "pick any provider"])) {
    return "Missing information, ask for clarification";
  }
  return "Proceed";
}

function selectCanonicalDocs(primary: string, secondary: string | undefined, task: string): string[] {
  const lower = normalize(task);
  const reactAllowed = includesAny(lower, ["ui", "screen", "visual", "ux", "compose", "theme"]);
  const paths = new Set<string>();
  for (const context of [primary, secondary].filter(Boolean) as string[]) {
    const rule = rules.find(item => item.context === context);
    for (const path of rule?.canonicalDocs ?? []) {
      if (!reactAllowed && path.includes("react-to-kotlin")) continue;
      paths.add(path);
      if (paths.size >= 3) break;
    }
    if (paths.size >= 3) break;
  }
  if (paths.size === 0) {
    for (const path of FOUNDATION_DOCS.slice(0, 3)) paths.add(path);
  }
  return Array.from(paths).slice(0, 3);
}

function buildRulesForTask(task: string): RuleSummary[] {
  const lower = normalize(task);
  const selected = [...GREENFIELD_RULES];
  if (includesAny(lower, ["auth", "login", "register", "logout", "session", "me", "profile", "settings"])) {
    selected.push({ code: "auth-scope", text: "Do not pull marketplace, payment, chat, support, business workspace, or social feed context into auth/profile tasks." });
  }
  if (includesAny(lower, ["ui", "screen", "compose", "visual", "ux"])) {
    selected.push({ code: "ui-reference", text: "For UI work, React/prototype references may guide screen intent and visual hierarchy only." });
  }
  return selected.slice(0, 5);
}

function buildReferences(canonicalDocs: string[]): SourceReference[] {
  return canonicalDocs.map(path => ({
    path,
    reason:
      path === "AGENTS.md" ? "Highest-priority implementation rules and architectural guardrails." :
      path.includes("CLIENT_ARCHITECTURE") ? "Client module and foundation expectations." :
      path.includes("BACKEND_ARCHITECTURE") ? "Backend service structure and boundaries." :
      path.includes("SECURITY_MODEL") ? "Auth, token, and security-sensitive behavior." :
      path.includes("PRODUCT_MODEL") ? "Minimum product intent and flow ownership." :
      path.includes("ADR-0002") ? "Chosen KMP/Compose client decision." :
      path.includes("ADR-0004") ? "Typed domain and no hardcoded business strings." :
      path.includes("react-to-kotlin") ? "Visual/product reference only for UI tasks." :
      "Directly relevant canonical source for this task."
  }));
}

function buildPreCodingBrief(primary: string, secondary: string | undefined, task: string, phase: string): PreCodingBrief {
  const lower = normalize(task);
  const isFoundation = includesAny(lower, ["monorepo", "gradle", "skeleton", "foundation", "health endpoint", "health check"]);
  const isAuth = includesAny(lower, ["auth", "login", "register", "logout", "session", "me"]);
  const isProfile = includesAny(lower, ["profile", "settings"]);

  if (isFoundation) {
    return {
      affectedModulesFiles: [
        "settings.gradle.kts",
        "build.gradle.kts",
        "gradle/libs.versions.toml",
        "services/platform-service/build.gradle.kts",
        "services/platform-service/src/main/java/.../HealthController.java",
        "apps/mobile-kmp/build.gradle.kts",
        "apps/mobile-kmp shared/network foundation files"
      ],
      domainModelsEnumsValueObjects: [
        "Assumption: no business domain models yet; add only a tiny typed health response DTO."
      ],
      repositoryContracts: [
        "Mobile health repository contract for calling backend health endpoint."
      ],
      apiRestContracts: [
        "GET /api/v1/health -> 200 with minimal typed status payload."
      ],
      localStorageCacheImpact: [
        "None for first foundation slice."
      ],
      interceptorsSessionTokenBehavior: [
        "No auth token behavior yet; prepare Ktor client hook points only."
      ],
      loggingObservabilityImpact: [
        "Backend request logging and app startup logging should be minimal and non-sensitive."
      ],
      errorsValidation: [
        "Typed transport error for mobile health check; no business validation yet."
      ],
      testsToAdd: [
        "Defer detailed unit tests.",
        "Run Gradle check.",
        "Manual smoke validation for backend health endpoint and mobile health check."
      ],
      validationCommands: [
        "./gradlew check",
        "./gradlew :services:platform-service:check",
        "./gradlew :apps:mobile-kmp:check"
      ],
      risksOpenQuestions: [
        `Implementation phase is '${phase}'.`,
        "Confirm exact module paths if repo layout differs from services/apps assumption."
      ]
    };
  }

  if (isAuth || isProfile) {
    return {
      affectedModulesFiles: [
        "Platform service auth/profile controller, application, domain, infrastructure packages",
        "Mobile auth/profile feature module, repository, Ktor client, session/token storage abstractions"
      ],
      domainModelsEnumsValueObjects: [
        "Typed account/session/profile DTOs and enums only; no display-text-driven logic."
      ],
      repositoryContracts: [
        isAuth ? "AuthRepository, SessionRepository, CurrentAccountRepository." : "ProfileRepository, SettingsRepository, CurrentAccountRepository."
      ],
      apiRestContracts: [
        isAuth ? "Register/login/logout/session restore/current account endpoints." : "Current account profile + settings read/update endpoints."
      ],
      localStorageCacheImpact: [
        isAuth ? "Secure token storage abstraction and session restore cache." : "Small current-account cache if needed; no social/feed cache."
      ],
      interceptorsSessionTokenBehavior: [
        "Bearer token injection, 401 handling, logout/clear on invalid session."
      ],
      loggingObservabilityImpact: [
        "Structured auth/profile logs without sensitive data or tokens."
      ],
      errorsValidation: [
        "Central AppResult/AppError mapping, input validation, typed field errors."
      ],
      testsToAdd: [
        "Defer detailed unit tests.",
        "Run Gradle check.",
        "Add focused test only if this task changes critical auth/session/security behavior."
      ],
      validationCommands: [
        "./gradlew check",
        "./gradlew :services:platform-service:check",
        "./gradlew :apps:mobile-kmp:check"
      ],
      risksOpenQuestions: [
        "Confirm token shape and refresh policy before implementing full auth persistence.",
        "Out of scope domains must stay excluded from this slice."
      ]
    };
  }

  return {
    affectedModulesFiles: [
      `Assumption: start inside ${primary}${secondary ? ` and ${secondary}` : ""} modules only.`
    ],
    domainModelsEnumsValueObjects: [
      "Add only the minimal typed models directly required by the slice."
    ],
    repositoryContracts: [
      "Introduce one repository boundary per external dependency or feature surface."
    ],
    apiRestContracts: [
      "Define only the REST contracts strictly needed for this slice."
    ],
    localStorageCacheImpact: [
      "Add cache/storage only if the slice cannot function without it."
    ],
    interceptorsSessionTokenBehavior: [
      "Assumption: no new token behavior unless the task explicitly touches auth/session."
    ],
    loggingObservabilityImpact: [
      "Add minimal structured logs around boundary crossings and failures."
    ],
    errorsValidation: [
      "Map transport/domain errors into typed application errors."
    ],
    testsToAdd: [
      "Defer detailed unit tests.",
      "Run Gradle check.",
      "Use manual smoke validation unless the task is security-critical."
    ],
    validationCommands: [
      "./gradlew check"
    ],
    risksOpenQuestions: [
      "Task may still be too broad if multiple business areas are required together."
    ]
  };
}

function buildScopeGuard(task: string, resolution: TaskResolution): ScopeGuard {
  const lower = normalize(task);
  if (includesAny(lower, ["auth", "login", "register", "logout", "session", "me", "profile", "settings", "foundation", "monorepo", "gradle", "health"])) {
    return {
      outOfScope: FOUNDATION_SCOPE_EXCLUSIONS
    };
  }
  if (resolution.proceedDecision === "Split task first") {
    return {
      outOfScope: ["More than 2 bounded contexts in one pass."],
      warning: "Task is broader than the compact MCP budget. Split into a smaller vertical slice first."
    };
  }
  return {
    outOfScope: ["Unrequested adjacent flows and extra bounded contexts."]
  };
}

function buildCompactText(
  resolvedContext: TaskResolution,
  rules: RuleSummary[],
  references: SourceReference[],
  preCodingBrief: PreCodingBrief,
  scopeGuard: ScopeGuard,
  proceedDecision: TaskResolution["proceedDecision"]
): string {
  const lines: string[] = [
    "## MCP Compact Context",
    "",
    "Primary context:",
    `- ${resolvedContext.primaryBoundedContext}`,
    "",
    "Secondary context:",
    `- ${resolvedContext.secondaryBoundedContext ?? "none"}`,
    "",
    "Task type:",
    `- ${resolvedContext.taskType}`,
    "",
    "Phase:",
    `- ${resolvedContext.implementationPhase}`,
    "",
    "Relevant docs:"
  ];

  references.forEach(reference => {
    lines.push(`- ${reference.path} — ${reference.reason}`);
  });

  lines.push("", "Minimal rules:");
  rules.forEach((rule, index) => {
    lines.push(`${index + 1}. ${rule.text}`);
  });

  lines.push(
    "",
    "Pre-coding brief:",
    `- affected files: ${preCodingBrief.affectedModulesFiles.join("; ")}`,
    `- domain types: ${preCodingBrief.domainModelsEnumsValueObjects.join("; ")}`,
    `- repositories: ${preCodingBrief.repositoryContracts.join("; ")}`,
    `- API contracts: ${preCodingBrief.apiRestContracts.join("; ")}`,
    `- storage/session: ${[...preCodingBrief.localStorageCacheImpact, ...preCodingBrief.interceptorsSessionTokenBehavior].join("; ")}`,
    `- errors: ${[...preCodingBrief.errorsValidation, ...preCodingBrief.loggingObservabilityImpact].join("; ")}`,
    `- tests: ${preCodingBrief.testsToAdd.join("; ")}`,
    `- validation: ${preCodingBrief.validationCommands.join("; ")}`,
    "",
    "Out of scope:",
    `- ${scopeGuard.outOfScope.join("; ")}`,
    "",
    "Decision:",
    `- ${proceedDecision}`
  );

  if (scopeGuard.warning) {
    lines.splice(lines.length - 3, 0, `- scope warning: ${scopeGuard.warning}`);
  }

  return lines.join("\n");
}

export function resolveTaskContext(task: string): TaskResolution {
  const candidates = inferContexts(task);
  const primary = candidates[0]?.boundedContext ?? "foundation";
  const secondary = candidates[1]?.boundedContext;
  const primaryRule = rules.find(rule => rule.context === primary);
  const implementationPhase = inferImplementationPhase(task);
  const proceedDecision = inferProceedDecision(task, candidates);
  const canonicalDocs = selectCanonicalDocs(primary, secondary, task);
  const warnings: string[] = [];

  if (candidates.length > 2) {
    warnings.push(`Compact budget exceeded: inferred contexts are ${candidates.map(x => x.boundedContext).join(", ")}.`);
  }
  if (secondary && candidates.length > 2) {
    warnings.push("Recommend splitting the task instead of loading more than 2 bounded contexts.");
  }

  return {
    task,
    primaryBoundedContext: primary,
    secondaryBoundedContext: secondary,
    taskType: inferTaskType(primaryRule, task),
    implementationPhase,
    canonicalDocs,
    relatedUmlParts: getRelatedUmls(primary).slice(0, 3),
    warnings,
    proceedDecision
  };
}

export function buildContextBundle(docs: DocItem[], boundedContext: string, task: string, limit = 3): MinimalContextResponse {
  const resolution = resolveTaskContext(task);
  const canonicalDocs = resolution.canonicalDocs.slice(0, Math.min(3, limit));
  const availablePaths = new Set(docs.map(doc => doc.path));
  const filteredCanonicalDocs = canonicalDocs.filter(path => availablePaths.has(path));

  const minimalRelevantRules = buildRulesForTask(task).slice(0, 5);
  const requiredSourceReferences = buildReferences(filteredCanonicalDocs);
  const preCodingBrief = buildPreCodingBrief(
    boundedContext.toLowerCase(),
    resolution.secondaryBoundedContext,
    task,
    resolution.implementationPhase
  );
  const scopeGuard = buildScopeGuard(task, resolution);
  const compactText = buildCompactText(
    {
      ...resolution,
      primaryBoundedContext: boundedContext.toLowerCase()
    },
    minimalRelevantRules,
    requiredSourceReferences,
    preCodingBrief,
    scopeGuard,
    resolution.proceedDecision
  );

  return {
    resolvedContext: {
      ...resolution,
      primaryBoundedContext: boundedContext.toLowerCase()
    },
    minimalRelevantRules,
    requiredSourceReferences,
    preCodingBrief,
    scopeGuard,
    proceedDecision: resolution.proceedDecision,
    compactText
  };
}

export function buildCompactSearchDocs(docs: DocItem[], boundedContext: string, task: string, limit = 3) {
  const canonicalPaths = new Set(resolveTaskContext(task).canonicalDocs);
  return docs
    .filter(doc => !canonicalPaths.has(doc.path))
    .map(doc => ({
      path: doc.path,
      title: doc.title,
      type: doc.type,
      score: scoreDoc(doc, task, boundedContext),
      snippet: snippet(doc.content, task)
    }))
    .filter(item => item.score > 0)
    .sort((a, b) => b.score - a.score)
    .slice(0, limit)
    .map(item => ({
      path: item.path,
      title: item.title,
      type: item.type,
      snippet: item.snippet.length > 280 ? `${item.snippet.slice(0, 277)}...` : item.snippet
    }));
}
