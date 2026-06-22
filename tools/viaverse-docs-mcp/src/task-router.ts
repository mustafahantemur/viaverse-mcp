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
  boundedContext: string[];
  featureSlice: string[];
  affectedServices: string[];
  affectedModulesFiles: string[];
  affectedRestContracts: string[];
  affectedEvents: string[];
  dbMigrations: string[];
  domainStatesEnumsValueObjects: string[];
  policiesBusinessRules: string[];
  dataStores: string[];
  securityConsiderations: string[];
  observabilityLoggingImpact: string[];
  errorsValidation: string[];
  testsToAdd: string[];
  docsAdrUpdates: string[];
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
  inferredBoundedContexts: string[];
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
  "CODING_RULES.md",
  "docs/blueprint/ARCHITECTURE.md",
  "docs/blueprint/BACKEND_ARCHITECTURE.md",
  "docs/blueprint/PRODUCT_MODEL.md",
  "docs/standards/backend-service-template.md",
  "docs/adr/ADR-0004-no-hardcoded-business-strings.md"
];

const rules: ContextRule[] = [
  {
    context: "foundation",
    keywords: ["greenfield", "foundation", "monorepo", "gradle", "skeleton", "platform-service", "health endpoint", "health check", "java 25", "spring boot"],
    canonicalDocs: [
      "AGENTS.md",
      "CODING_RULES.md",
      "docs/standards/backend-service-template.md",
      "docs/blueprint/BACKEND_ARCHITECTURE.md"
    ],
    taskTypes: {
      monorepo: ["gradle", "monorepo", "skeleton", "module"],
      verticalSlice: ["health", "endpoint", "check", "client"]
    }
  },
  {
    context: "client",
    keywords: ["kotlin", "kmp", "compose", "mobile", "ui", "screen", "theme", "icon", "navigation", "ktor", "sqldelight"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/CLIENT_ARCHITECTURE.md",
      "docs/templates/react-to-kotlin-compose-migration.md",
      "docs/adr/ADR-0002-kotlin-multiplatform-client.md"
    ],
    taskTypes: {
      foundation: ["foundation", "app", "mobile", "ktor", "session", "storage"],
      ui: ["screen", "ui", "compose", "theme", "navigation"]
    }
  },
  {
    context: "identity",
    keywords: ["identity", "identity-service", "login", "auth", "otp", "account", "capability", "session", "jwt", "bearer", "refresh token", "register", "logout", "me"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/identity-auth-rules.md",
      "docs/blueprint/SECURITY_MODEL.md",
      "docs/adr/ADR-0004-no-hardcoded-business-strings.md"
    ],
    taskTypes: {
      authentication: ["login", "register", "otp", "session", "logout", "me", "jwt", "refresh token"],
      profile: ["profile", "settings", "account"],
      abuseProtection: ["rate limit", "abuse", "lockout", "cooldown"]
    }
  },
  {
    context: "marketplace",
    keywords: ["marketplace", "request", "job", "offer", "quote", "accept", "completion", "lifecycle"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/backend-service-template.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/blueprint/BACKEND_ARCHITECTURE.md"
    ]
  },
  {
    context: "business",
    keywords: ["business", "merchant", "publish", "branch", "catalog", "staff", "verification"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/backend-service-template.md",
      "docs/blueprint/PRODUCT_MODEL.md",
      "docs/uml/viaverse_uml_part_22_business_account_merchant_ops/README.md"
    ]
  },
  {
    context: "payment",
    keywords: ["payment", "iyzico", "stripe", "masterpass", "refund", "payout", "commission"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/backend-service-template.md",
      "docs/blueprint/PAYMENT_MODEL.md",
      "docs/blueprint/SECURITY_MODEL.md"
    ]
  },
  {
    context: "chat",
    keywords: ["chat", "message", "conversation", "moderation"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/backend-service-template.md",
      "docs/uml/viaverse_uml_part_25_chat_messaging_safety/README.md",
      "docs/blueprint/SECURITY_MODEL.md"
    ]
  },
  {
    context: "search",
    keywords: ["search", "matching", "seo", "discovery", "index"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/observability-logging-rules.md",
      "docs/blueprint/DATA_ARCHITECTURE.md",
      "docs/blueprint/SEO_AND_GROWTH.md"
    ]
  },
  {
    context: "observability",
    keywords: ["observability", "logging", "logs", "log", "opensearch", "opentelemetry", "otel", "trace", "tracing", "span", "micrometer", "actuator", "prometheus", "dashboard", "stdout", "stderr", "ecs", "audit_log", "audit log", "fluent bit", "collector"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/observability-logging-rules.md",
      "docs/blueprint/TECH_STACK_DECISIONS.md"
    ],
    taskTypes: {
      logging: ["logging", "logs", "stdout", "ecs", "audit_log"],
      tracing: ["opentelemetry", "otel", "trace", "span"],
      metrics: ["micrometer", "actuator", "prometheus", "metrics"]
    }
  },
  {
    context: "media-storage",
    keywords: ["media", "object storage", "object-storage", "s3", "seaweedfs", "minio", "bucket", "presigned", "upload"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/license-policy.md",
      "docs/standards/local-infra-rules.md",
      "docs/adr/ADR-0006-seaweedfs-local-object-storage.md"
    ]
  },
  {
    context: "local-infra",
    keywords: ["local infra", "local-infra", "docker compose", "docker-compose", "local env", "opensearch", "seaweedfs", "minio", "postgres", "postgresql", "flyway", "bootrun", "debug", "graylog", "loki", "grafana"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/local-infra-rules.md",
      "docs/standards/license-policy.md",
      "docs/runbooks/local-development.md"
    ]
  },
  {
    context: "security",
    keywords: ["security", "jwt", "bearer", "oauth2", "resource server", "nimbus", "refresh token", "otp", "rate limit", "abuse", "authorization", "principal", "spring security"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/blueprint/SECURITY_MODEL.md",
      "docs/standards/identity-auth-rules.md"
    ]
  },
  {
    context: "compliance",
    keywords: ["compliance", "kvkk", "privacy", "pii", "sensitive", "audit", "audit_log", "consent", "license", "agpl", "gpl", "lgpl", "sspl"],
    canonicalDocs: [
      "AGENTS.md",
      "docs/standards/license-policy.md",
      "docs/standards/observability-logging-rules.md",
      "docs/blueprint/PRIVACY_AND_KVKK.md"
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

const HUGE_TASK_KEYWORDS = [
  "everything",
  "all flows",
  "full app",
  "complete platform",
  "finish full template",
  "complete all flows",
  "port all react screens",
  "build marketplace/payment/chat/business all at once"
];

const VERTICAL_SLICE_HINTS = [
  "identity-auth-backend",
  "auth-abuse-protection",
  "mobile-auth-client",
  "mobile-auth-ui",
  "profile-foundation",
  "observability-foundation",
  "search-foundation"
];

const GREENFIELD_RULES: RuleSummary[] = [
  { code: "greenfield", text: "Treat Viaverse as a greenfield implementation; prototypes are visual/product references only." },
  { code: "gradle-first", text: "All backend services use Java 25, Spring Boot, and Gradle Kotlin DSL conventions." },
  { code: "service-baseline", text: "Every JPA service needs Spring Data JPA, PostgreSQL driver, Flyway core/PostgreSQL support, migrations, and ddl-auto=validate." },
  { code: "typed-domain", text: "No hardcoded business role/status/type/capability/payment/request strings; use enums/value objects." },
  { code: "observability-default", text: "Application logs go to structured ECS JSON stdout/stderr; audit_log is not application logging; no local text-file app logs." },
  { code: "license-default", text: "Prefer MIT, Apache-2.0, BSD, ISC; avoid AGPL/GPL/LGPL/SSPL/source-available infrastructure unless ADR-approved." },
  { code: "vertical-slice", text: "Keep tasks as small vertical slices; split full-template or all-flow prompts before coding." },
  { code: "react-guard", text: "React template is allowed only for UI/screen tasks; never as architecture or domain source of truth." }
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

function unique(items: string[]): string[] {
  return Array.from(new Set(items));
}

function inferServiceNames(task: string): string[] {
  const serviceNames = [
    "identity-service",
    "marketplace-service",
    "payment-service",
    "messaging-service",
    "media-service",
    "notification-service",
    "search-service",
    "trust-gamification-service",
    "ads-monetization-service",
    "admin-bff"
  ];
  const lower = normalize(task);
  const found = serviceNames.filter(service => lower.includes(service));

  if (found.length > 0) return found;
  if (includesAny(lower, ["identity", "auth", "otp", "jwt", "session", "account"])) return ["identity-service"];
  if (includesAny(lower, ["marketplace", "request", "quote", "job", "offer"])) return ["marketplace-service"];
  if (includesAny(lower, ["payment", "iyzico", "stripe", "refund", "payout"])) return ["payment-service"];
  if (includesAny(lower, ["message", "chat", "conversation"])) return ["messaging-service"];
  if (includesAny(lower, ["media", "s3", "bucket", "upload", "object storage", "seaweedfs", "minio"])) return ["media-service"];
  if (includesAny(lower, ["notification", "email", "sms", "push"])) return ["notification-service"];
  if (includesAny(lower, ["search", "opensearch", "index", "discovery"])) return ["search-service"];

  return ["Unknown / must inspect repo"];
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
  if (includesAny(lower, ["observability", "logging", "opentelemetry", "otel", "metrics", "actuator"])) return "Observability foundation";
  if (includesAny(lower, ["minio", "seaweedfs", "object storage", "local infra"])) return "Local infrastructure / provider abstraction";
  if (includesAny(lower, ["jwt", "resource server", "bearer", "refresh token", "otp", "abuse"])) return "Identity/security vertical slice";
  if (includesAny(lower, ["monorepo", "gradle", "skeleton", "foundation"])) return "Repository / monorepo foundation";
  if (includesAny(lower, ["platform-service", "spring boot", "health endpoint", "backend foundation"])) return "Backend service foundation";
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
  if (includesAny(lower, HUGE_TASK_KEYWORDS)) return "Split task first";
  if (contexts.length > 3) return "Split task first";
  if (includesAny(lower, ["minio", "agpl", "gpl", "sspl", "graylog default", "loki default"])) {
    return "Missing information, ask for clarification";
  }
  if (includesAny(lower, ["unknown provider", "undecided provider", "pick any provider"])) {
    return "Missing information, ask for clarification";
  }
  return "Proceed";
}

function selectCanonicalDocs(primary: string, secondary: string | undefined, task: string, inferredContexts: string[]): string[] {
  const lower = normalize(task);
  const reactAllowed = includesAny(lower, ["ui", "screen", "visual", "ux", "compose", "theme"]);
  const contextOrder = unique([primary, secondary ?? "", ...inferredContexts].filter(Boolean));
  const paths = new Set<string>();

  for (const context of contextOrder) {
    const rule = rules.find(item => item.context === context);
    for (const path of rule?.canonicalDocs ?? []) {
      if (!reactAllowed && path.includes("react-to-kotlin")) continue;
      paths.add(path);
      if (paths.size >= 4) break;
    }
    if (paths.size >= 4) break;
  }

  if (paths.size === 0) {
    for (const path of FOUNDATION_DOCS.slice(0, 4)) paths.add(path);
  }
  return Array.from(paths).slice(0, 4);
}

function buildRulesForTask(task: string): RuleSummary[] {
  const lower = normalize(task);
  const selected = [...GREENFIELD_RULES];

  if (includesAny(lower, ["jwt", "bearer", "resource server", "auth", "otp", "refresh token"])) {
    selected.push({ code: "jwt-spring-security", text: "Do not manually build/sign/parse JWT; use Spring Security OAuth2 Resource Server plus Nimbus-backed JwtEncoder/JwtDecoder." });
    selected.push({ code: "auth-abuse", text: "OTP/rate limits/cooldowns/lockouts belong in focused abuse-protection classes, not giant auth services." });
  }
  if (includesAny(lower, ["minio", "object storage", "s3", "seaweedfs", "bucket"])) {
    selected.push({ code: "no-minio", text: "MinIO is forbidden unless a new ADR approves it; use generic object-storage ports and SeaweedFS only for local S3-compatible development." });
  }
  if (includesAny(lower, ["logging", "logs", "opensearch", "graylog", "loki", "audit_log", "app.log", "debug.log"])) {
    selected.push({ code: "logging-stdout", text: "Use structured ECS JSON stdout/stderr logs; OpenSearch is the default log/search store; Graylog is not default due SSPL; no file-based app logs." });
  }
  if (includesAny(lower, ["error", "exception", "validation", "unauthorized"])) {
    selected.push({ code: "typed-errors", text: "Do not hardcode user-facing service errors; throw exceptions with AppErrorCode and let GlobalExceptionHandler map ProblemDetail." });
  }
  if (includesAny(lower, ["ui", "screen", "compose", "visual", "ux"])) {
    selected.push({ code: "ui-reference", text: "For UI work, React/prototype references may guide screen intent and visual hierarchy only; do not convert React to Kotlin line by line." });
  }

  return selected.slice(0, 10);
}

function buildReferences(canonicalDocs: string[]): SourceReference[] {
  return canonicalDocs.map(path => ({
    path,
    reason:
      path === "AGENTS.md" ? "Highest-priority implementation rules and architectural guardrails." :
      path === "CODING_RULES.md" ? "General engineering, backend, testing, and review rules." :
      path.includes("backend-service-template") ? "Global Java 25/Spring Boot service structure, Gradle, Flyway, package, and quality gates." :
      path.includes("CLIENT_ARCHITECTURE") ? "Client module and foundation expectations." :
      path.includes("BACKEND_ARCHITECTURE") ? "Backend service structure and boundaries." :
      path.includes("SECURITY_MODEL") ? "Auth, token, and security-sensitive behavior." :
      path.includes("observability-logging-rules") ? "Structured logging, OpenTelemetry, OpenSearch, audit separation, and sensitive-data rules." :
      path.includes("identity-auth-rules") ? "Spring Security JWT, OTP, refresh-token, abuse-protection, and identity error rules." :
      path.includes("license-policy") ? "Permissive license policy and forbidden/copyleft infrastructure guardrails." :
      path.includes("local-infra-rules") ? "Local environment, Flyway, OpenSearch, SeaweedFS, and service startup rules." :
      path.includes("PRODUCT_MODEL") ? "Minimum product intent and flow ownership." :
      path.includes("ADR-0002") ? "Chosen KMP/Compose client decision." :
      path.includes("ADR-0004") ? "Typed domain and no hardcoded business strings." :
      path.includes("ADR-0006") ? "SeaweedFS local object-storage ADR and MinIO removal decision." :
      path.includes("react-to-kotlin") ? "Visual/product reference only for UI tasks." :
      "Directly relevant canonical source for this task."
  }));
}

function commonBackendBrief(contexts: string[], task: string): Pick<
  PreCodingBrief,
  "affectedServices" | "dbMigrations" | "dataStores" | "securityConsiderations" | "observabilityLoggingImpact" | "errorsValidation" | "testsToAdd" | "docsAdrUpdates" | "validationCommands"
> {
  const lower = normalize(task);
  const affectedServices = inferServiceNames(task);
  const touchesBackend = !includesAny(lower, ["mobile", "compose", "react", "next.js", "ui only", "documentation only"]);
  const touchesAuth = includesAny(lower, ["auth", "jwt", "otp", "session", "login", "refresh token", "bearer"]);
  const touchesObservability = includesAny(lower, ["observability", "logging", "logs", "otel", "opentelemetry", "opensearch", "metrics", "actuator"]);

  return {
    affectedServices,
    dbMigrations: touchesBackend
      ? [
          "Unknown / must inspect repo for affected service migrations.",
          "If JPA entities change, add Flyway migrations under src/main/resources/db/migration and verify all tables/columns exist before bootRun/debug.",
          "Hibernate ddl-auto must remain validate; do not use update/create for normal development."
        ]
      : ["Not applicable"],
    dataStores: touchesObservability
      ? ["PostgreSQL if service state changes", "OpenSearch as central log/search target through collector pipeline, not business-code logging dependency"]
      : ["PostgreSQL if persistent service state changes", "Valkey/Redis for rate limits or short-lived state if applicable", "OpenSearch only through dedicated search/observability adapters"],
    securityConsiderations: touchesAuth
      ? [
          "Use Spring Security filter chain and authenticated principal/context; do not scatter Authorization header parsing in services.",
          "Use Resource Server plus Nimbus-backed JwtEncoder/JwtDecoder; access token TTL configurable.",
          "Refresh tokens opaque/random, hashed, rotated, revocable; OTP hashed, expiring, attempt-limited, resend-cooled, and rate-limited."
        ]
      : [
          "Deny authorization by default.",
          "Check active account, ownership/capabilities/roles, and risk where relevant.",
          "Do not log tokens, Authorization headers, passwords, OTPs, API keys, client secrets, raw phone numbers, raw emails, or KVKK-sensitive data."
        ],
    observabilityLoggingImpact: touchesObservability
      ? [
          "Stage 1: structured ECS JSON logs to stdout/stderr, correlationId/requestId MDC, audit separation.",
          "Stage 2: OpenTelemetry Collector or Fluent Bit forwards logs to OpenSearch; OpenSearch Dashboards may visualize local/dev logs.",
          "Stage 3: trace/log correlation with traceId/spanId dashboards. Basic service tests must not require full observability stack."
        ]
      : [
          "Use structured ECS JSON stdout/stderr logs only.",
          "Preserve correlationId/requestId and include traceId/spanId when available.",
          "audit_log is for typed security/legal/account-critical events only, not application logging."
        ],
    errorsValidation: [
      "Use AppErrorCode plus default message/parameters and GlobalExceptionHandler ProblemDetail mapping.",
      "Do not throw UnauthorizedException(\"literal message\") or ValidationException(\"literal message\") without an error code.",
      contexts.includes("identity") || touchesAuth
        ? "Identity auth errors must include the canonical AUTH_* codes where applicable."
        : "Field validation errors must be structured and later localizable."
    ],
    testsToAdd: [
      "Add focused tests for the vertical slice; do not skip tests.",
      touchesAuth ? "Add security/auth tests for JWT, OTP attempts/expiry/rate limits, refresh-token rotation/reuse, and account-active checks as applicable." : "Add use-case, policy, controller, repository/Flyway, or adapter tests matching the changed layer.",
      "Basic service tests must not require OpenSearch/log collection unless the task explicitly targets observability integration."
    ],
    docsAdrUpdates: [
      "Update docs/rules if behavior or architecture guidance changes.",
      includesAny(lower, ["new provider", "new datastore", "agpl", "gpl", "sspl", "minio", "graylog", "loki"]) ? "ADR required before adding a new provider, datastore, or non-permissive/copyleft infrastructure dependency." : "Not applicable unless a decision changes."
    ],
    validationCommands: [
      "./gradlew check",
      "For affected backend service: ./gradlew :services:<service>:test and bootRun/debug after Flyway validation if module exists",
      "For MCP/rules repo changes: npm run mcp:build && npm run mcp:check && npm run mcp:smoke"
    ]
  };
}

function buildPreCodingBrief(primary: string, secondary: string | undefined, task: string, phase: string, inferredContexts: string[]): PreCodingBrief {
  const lower = normalize(task);
  const contexts = unique([primary, secondary ?? "", ...inferredContexts].filter(Boolean));
  const common = commonBackendBrief(contexts, task);
  const isFoundation = includesAny(lower, ["monorepo", "gradle", "skeleton", "foundation", "health endpoint", "health check"]);
  const isAuth = includesAny(lower, ["auth", "login", "register", "logout", "session", "me", "jwt", "otp", "refresh token"]);
  const isProfile = includesAny(lower, ["profile", "settings"]);
  const isObservability = includesAny(lower, ["observability", "logging", "logs", "opensearch", "otel", "opentelemetry", "metrics", "audit_log"]);

  if (isFoundation) {
    return {
      boundedContext: contexts,
      featureSlice: [phase],
      affectedServices: common.affectedServices,
      affectedModulesFiles: [
        "settings.gradle.kts",
        "build.gradle.kts",
        "gradle/libs.versions.toml",
        "services/<service>/build.gradle.kts",
        "services/<service>/src/main/java/... feature packages",
        "services/<service>/src/main/resources/db/migration if JPA exists"
      ],
      affectedRestContracts: ["GET /api/v1/health -> 200 with minimal typed status payload if this slice includes health."],
      affectedEvents: ["Not applicable unless service state changes must inform other contexts."],
      dbMigrations: common.dbMigrations,
      domainStatesEnumsValueObjects: ["No business domain models unless the slice explicitly needs typed states/enums/value objects."],
      policiesBusinessRules: ["Apply global service quality gate; controllers thin, use cases focused, package boundaries clear."],
      dataStores: common.dataStores,
      securityConsiderations: common.securityConsiderations,
      observabilityLoggingImpact: common.observabilityLoggingImpact,
      errorsValidation: common.errorsValidation,
      testsToAdd: common.testsToAdd,
      docsAdrUpdates: common.docsAdrUpdates,
      validationCommands: common.validationCommands,
      risksOpenQuestions: ["Unknown / must inspect repo for exact module paths and Gradle conventions."]
    };
  }

  if (isAuth || isProfile) {
    return {
      boundedContext: contexts,
      featureSlice: [isAuth ? "identity-auth-backend or auth-abuse-protection, depending on exact task" : "profile-foundation"],
      affectedServices: common.affectedServices,
      affectedModulesFiles: [
        "For backend: app.viaverse.<service>.<feature>.api/application/domain/infrastructure packages",
        "Identity example only: auth/account/consent/shared packages with focused use cases and dedicated ratelimit/otp/security infrastructure",
        "For mobile: auth/profile feature module, repository, Ktor client, session/token storage abstractions if task is client-side"
      ],
      affectedRestContracts: [isAuth ? "Auth start/verify OTP/register/login/refresh/logout/current-account endpoints, as scoped." : "Current account profile/settings read/update endpoints, as scoped."],
      affectedEvents: [isAuth ? "Account/session/security events through outbox if other contexts need the state change." : "Not applicable unless profile changes publish account/profile summary events."],
      dbMigrations: common.dbMigrations,
      domainStatesEnumsValueObjects: ["Typed account/session/profile states, AccountCapability, consent state, refresh-token/session value objects; no display-text-driven logic."],
      policiesBusinessRules: [
        "Focused use cases; avoid giant IdentityAuthService.",
        "Rate limiting/abuse protection in dedicated auth abuse/ratelimit classes.",
        "Temporary lockout preferred over permanent account lockout."
      ],
      dataStores: common.dataStores,
      securityConsiderations: common.securityConsiderations,
      observabilityLoggingImpact: common.observabilityLoggingImpact,
      errorsValidation: common.errorsValidation,
      testsToAdd: common.testsToAdd,
      docsAdrUpdates: common.docsAdrUpdates,
      validationCommands: common.validationCommands,
      risksOpenQuestions: [
        "Confirm token/session shape and refresh policy before implementing full auth persistence.",
        "Debug fixed OTP may exist only in local/test and must not bypass rate limits, expiry, max attempts, used-state, or session security.",
        "Out of scope domains must stay excluded from this slice."
      ]
    };
  }

  if (isObservability) {
    return {
      boundedContext: contexts,
      featureSlice: ["observability-foundation"],
      affectedServices: common.affectedServices,
      affectedModulesFiles: [
        "Service logging configuration",
        "Spring Boot Actuator/Micrometer/OpenTelemetry readiness configuration",
        "CorrelationId/requestId filter or interceptor",
        "Local infra compose/profile files only if the task explicitly targets local observability"
      ],
      affectedRestContracts: ["Not applicable unless exposing actuator/metrics endpoints or changing API error response headers."],
      affectedEvents: ["Not applicable for application logs; audit events remain typed security/legal/account-critical events only."],
      dbMigrations: [
        "Not applicable for application logs.",
        "If audit_log schema changes, add Flyway migration and typed AuditAction model; do not use audit_log for app logs."
      ],
      domainStatesEnumsValueObjects: ["Typed AuditAction values if audit events change; no random string actions."],
      policiesBusinessRules: [
        "Application logging and audit logging are separate concerns.",
        "Business code must not depend directly on OpenSearch for logging.",
        "OpenSearch collector stack must not be required for basic service tests."
      ],
      dataStores: common.dataStores,
      securityConsiderations: common.securityConsiderations,
      observabilityLoggingImpact: common.observabilityLoggingImpact,
      errorsValidation: common.errorsValidation,
      testsToAdd: common.testsToAdd,
      docsAdrUpdates: common.docsAdrUpdates,
      validationCommands: common.validationCommands,
      risksOpenQuestions: [
        "Unknown / must inspect repo for current logging config and local Docker profiles.",
        "Confirm whether this task is Stage 1 stdout logging, Stage 2 collector/OpenSearch ingestion, or Stage 3 dashboards/correlation."
      ]
    };
  }

  return {
    boundedContext: contexts,
    featureSlice: [phase],
    affectedServices: common.affectedServices,
    affectedModulesFiles: [`Assumption: start inside ${primary}${secondary ? ` and ${secondary}` : ""} modules only.`],
    affectedRestContracts: ["Unknown / must inspect repo"],
    affectedEvents: ["Unknown / must inspect repo; add outbox if state change matters to other contexts."],
    dbMigrations: common.dbMigrations,
    domainStatesEnumsValueObjects: ["Add only the minimal typed models directly required by the slice."],
    policiesBusinessRules: ["Use focused use cases and policies; avoid giant services mixing unrelated concerns."],
    dataStores: common.dataStores,
    securityConsiderations: common.securityConsiderations,
    observabilityLoggingImpact: common.observabilityLoggingImpact,
    errorsValidation: common.errorsValidation,
    testsToAdd: common.testsToAdd,
    docsAdrUpdates: common.docsAdrUpdates,
    validationCommands: common.validationCommands,
    risksOpenQuestions: ["Task may still be too broad if multiple business areas are required together."]
  };
}

function buildScopeGuard(task: string, resolution: TaskResolution): ScopeGuard {
  const lower = normalize(task);
  if (includesAny(lower, HUGE_TASK_KEYWORDS)) {
    return {
      outOfScope: ["Multiple vertical slices in one task", "Full template completion", "All-flow implementation", "Cross-product rewrites"],
      warning: `Split first. Suggested slices: ${VERTICAL_SLICE_HINTS.join(", ")}.`
    };
  }
  if (includesAny(lower, ["minio", "agpl", "gpl", "sspl"])) {
    return {
      outOfScope: ["MinIO", "AGPL/GPL/SSPL/copyleft infrastructure", "provider-specific object-storage APIs in application/domain code"],
      warning: "Refuse unless an explicit ADR approves the dependency; recommend SeaweedFS for local S3-compatible storage and generic object-storage abstractions."
    };
  }
  if (includesAny(lower, ["auth", "login", "register", "logout", "session", "me", "profile", "settings", "foundation", "monorepo", "gradle", "health"])) {
    return {
      outOfScope: FOUNDATION_SCOPE_EXCLUSIONS
    };
  }
  if (resolution.proceedDecision === "Split task first") {
    return {
      outOfScope: ["More than 3 bounded contexts in one pass."],
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
    "Inferred contexts:",
    `- ${resolvedContext.inferredBoundedContexts.join(", ")}`,
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
    `- bounded context: ${preCodingBrief.boundedContext.join("; ")}`,
    `- feature/slice: ${preCodingBrief.featureSlice.join("; ")}`,
    `- affected services: ${preCodingBrief.affectedServices.join("; ")}`,
    `- affected files: ${preCodingBrief.affectedModulesFiles.join("; ")}`,
    `- REST contracts: ${preCodingBrief.affectedRestContracts.join("; ")}`,
    `- events: ${preCodingBrief.affectedEvents.join("; ")}`,
    `- DB migrations: ${preCodingBrief.dbMigrations.join("; ")}`,
    `- domain states/enums/value objects: ${preCodingBrief.domainStatesEnumsValueObjects.join("; ")}`,
    `- policies/business rules: ${preCodingBrief.policiesBusinessRules.join("; ")}`,
    `- data stores: ${preCodingBrief.dataStores.join("; ")}`,
    `- security: ${preCodingBrief.securityConsiderations.join("; ")}`,
    `- observability/logging: ${preCodingBrief.observabilityLoggingImpact.join("; ")}`,
    `- errors: ${preCodingBrief.errorsValidation.join("; ")}`,
    `- tests: ${preCodingBrief.testsToAdd.join("; ")}`,
    `- docs/ADR: ${preCodingBrief.docsAdrUpdates.join("; ")}`,
    `- validation: ${preCodingBrief.validationCommands.join("; ")}`,
    `- risks/open questions: ${preCodingBrief.risksOpenQuestions.join("; ")}`,
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
  const inferredBoundedContexts = candidates.length > 0 ? candidates.map(x => x.boundedContext) : [primary];
  const primaryRule = rules.find(rule => rule.context === primary);
  const implementationPhase = inferImplementationPhase(task);
  const proceedDecision = inferProceedDecision(task, candidates);
  const canonicalDocs = selectCanonicalDocs(primary, secondary, task, inferredBoundedContexts);
  const warnings: string[] = [];
  const lower = normalize(task);

  if (candidates.length > 3) {
    warnings.push(`Compact budget exceeded: inferred contexts are ${candidates.map(x => x.boundedContext).join(", ")}.`);
    warnings.push("Recommend splitting the task instead of loading more than 3 contexts.");
  }
  if (includesAny(lower, ["jwt", "bearer", "resource server"])) {
    warnings.push("Manual JWT construction/parsing/signing is forbidden; use Spring Security OAuth2 Resource Server and Nimbus-backed JwtEncoder/JwtDecoder.");
  }
  if (includesAny(lower, ["minio"])) {
    warnings.push("MinIO is forbidden unless a new ADR explicitly approves it; use SeaweedFS for local S3-compatible storage and generic object-storage abstractions.");
  }
  if (includesAny(lower, ["agpl", "gpl", "lgpl", "sspl", "graylog", "loki", "grafana"])) {
    warnings.push("Non-permissive/copyleft or source-available infrastructure requires explicit ADR approval; OpenSearch is the default log/search store.");
  }
  if (includesAny(lower, ["app.log", "debug.log", "local.txt", "errors.txt", "file log", "text-file logging", "audit_log"])) {
    warnings.push("Application logs must be structured ECS JSON stdout/stderr; audit_log is not application logging; file-based default app logs are forbidden.");
  }
  if (includesAny(lower, HUGE_TASK_KEYWORDS)) {
    warnings.push(`Task is too broad; split into vertical slices such as ${VERTICAL_SLICE_HINTS.join(", ")}.`);
  }

  return {
    task,
    primaryBoundedContext: primary,
    secondaryBoundedContext: secondary,
    inferredBoundedContexts,
    taskType: inferTaskType(primaryRule, task),
    implementationPhase,
    canonicalDocs,
    relatedUmlParts: inferredBoundedContexts.flatMap(context => getRelatedUmls(context)).slice(0, 5),
    warnings,
    proceedDecision
  };
}

export function buildContextBundle(docs: DocItem[], boundedContext: string, task: string, limit = 4): MinimalContextResponse {
  const resolution = resolveTaskContext(task);
  const canonicalDocs = resolution.canonicalDocs.slice(0, Math.min(4, limit));
  const availablePaths = new Set(docs.map(doc => doc.path));
  const filteredCanonicalDocs = docs.length > 0 ? canonicalDocs.filter(path => availablePaths.has(path)) : canonicalDocs;
  const primary = boundedContext.toLowerCase();

  const minimalRelevantRules = buildRulesForTask(task);
  const requiredSourceReferences = buildReferences(filteredCanonicalDocs);
  const preCodingBrief = buildPreCodingBrief(
    primary,
    resolution.secondaryBoundedContext,
    task,
    resolution.implementationPhase,
    resolution.inferredBoundedContexts
  );
  const scopeGuard = buildScopeGuard(task, resolution);
  const compactText = buildCompactText(
    {
      ...resolution,
      primaryBoundedContext: primary
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
      primaryBoundedContext: primary
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
