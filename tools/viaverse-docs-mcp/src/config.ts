import path from "node:path";

export const REPO_ROOT = path.resolve(process.env.VIAVERSE_REPO_ROOT ?? process.cwd());

export const DOC_GLOBS = [
  "README.md",
  "AGENTS.md",
  "CODING_RULES.md",
  "AI_CODING_PROMPT.md",
  "MCP_SETUP_AND_USAGE.md",
  "docs/**/*.md",
  "docs/**/*.json",
  "docs/**/*.csv",
  "templates/**/*.md",
  "templates/**/*.kt",
  "templates/**/*.kts",
  "templates/**/*.ts",
  "templates/**/*.tsx",
  "templates/**/*.css",
  "templates/**/*.json",
  "templates/**/*.toml"
];

export const IGNORE_GLOBS = [
  "node_modules/**",
  "dist/**",
  "**/build/**",
  "**/.gradle/**",
  "**/.kotlin/**",
  "**/node_modules/**",
  "**/dist/**",
  "**/.next/**",
  ".git/**",
  "**/.env",
  "**/.env.*",
  "**/*.pem",
  "**/*.key",
  "**/credentials/**",
  "**/secrets/**"
];

export const MAX_DOC_CHARS = 120_000;

// Canonical ranking fixes. These are not hardcoded business logic;
// they are documentation retrieval priorities for AI context quality.
export const CANONICAL_CONTEXT_DOC_PRIORITY: Record<string, string[]> = {
  client: [
    "AGENTS.md",
    "docs/blueprint/CLIENT_ARCHITECTURE.md",
    "docs/blueprint/PRODUCT_MODEL.md",
    "docs/adr/ADR-0004-no-hardcoded-business-strings.md",
    "templates/kotlin/viaverse-template/KOTLIN_TEMPLATE_GUARDRAILS.md",
    "docs/templates/react-to-kotlin-compose-migration.md"
  ],
  template: [
    "AGENTS.md",
    "docs/blueprint/CLIENT_ARCHITECTURE.md",
    "templates/kotlin/viaverse-template/KOTLIN_TEMPLATE_GUARDRAILS.md",
    "docs/templates/react-to-kotlin-compose-migration.md"
  ],
  business: [
    "docs/blueprint/PRODUCT_MODEL.md",
    "docs/blueprint/MONETIZATION_MODEL.md",
    "docs/blueprint/PAYMENT_MODEL.md",
    "docs/blueprint/BACKEND_ARCHITECTURE.md",
    "docs/uml/viaverse_uml_part_22_business_account_merchant_ops/README.md",
    "docs/uml/viaverse_uml_part_23_monetization_subscription_ads_commission/README.md",
    "docs/uml/viaverse_uml_part_28_final_master_topology/README.md"
  ],
  marketplace: [
    "docs/blueprint/PRODUCT_MODEL.md",
    "docs/blueprint/BACKEND_ARCHITECTURE.md",
    "docs/blueprint/EVENT_ARCHITECTURE.md",
    "docs/uml/viaverse_uml_part_03_request_creation/README.md",
    "docs/uml/viaverse_uml_part_04_offer_job_lifecycle/README.md",
    "docs/uml/viaverse_uml_part_28_final_master_topology/README.md"
  ],
  payment: [
    "docs/blueprint/PAYMENT_MODEL.md",
    "docs/blueprint/SECURITY_MODEL.md",
    "docs/blueprint/PRIVACY_AND_KVKK.md",
    "docs/contracts/providers/payment-providers.md",
    "docs/uml/viaverse_uml_part_08_payment/README.md",
    "docs/uml/viaverse_uml_part_15_payment_provider_abstraction/README.md",
    "docs/uml/viaverse_uml_part_28_final_master_topology/README.md"
  ],
  chat: [
    "docs/blueprint/SECURITY_MODEL.md",
    "docs/blueprint/PRIVACY_AND_KVKK.md",
    "docs/blueprint/DATA_ARCHITECTURE.md",
    "docs/uml/viaverse_uml_part_25_chat_messaging_safety/README.md",
    "docs/uml/viaverse_uml_part_28_final_master_topology/README.md"
  ]
};

export const DEPRECATED_UML_HINTS: Record<string, string[]> = {
  business: [
    "docs/uml/viaverse_uml_part_06_business_mode/README.md"
  ]
};
