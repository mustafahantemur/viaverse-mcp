# UML_INDEX.md

Canonical UML navigation index for the UML folders currently present in this repository.

## Storage Rule

```text
docs/uml/<folder>/
  README.md
  *.drawio
  *.svg
  *.puml
```

## Source of Truth Rule

```text
.drawio = visual editable source
.puml   = text / diff-friendly technical source
.svg    = rendered preview
README.md = AI/MCP-readable summary
```

## UML Part Index

| Part | Topic | Folder |
|---:|---|---|
| 01 | Authentication / Login / Onboarding | `docs/uml/viaverse_uml_part_01_auth/` |
| 02 | Account Capability / Mode Switching | `docs/uml/viaverse_uml_part_02_capability/` |
| 03 | Request Creation / İlan Açma Flow | `docs/uml/viaverse_uml_part_03_request_creation/` |
| 04 | Matching / Search / Recommendation Flow | `docs/uml/viaverse_uml_part_04_matching/` |
| 05 | Worker Onboarding / Work Mode Flow | `docs/uml/viaverse_uml_part_05_worker_onboarding/` |
| 06 | Business Account / Business Mode Flow | `docs/uml/viaverse_uml_part_06_business_mode/` |
| 07 | Quote / Offer Flow | `docs/uml/viaverse_uml_part_07_quote_offer/` |
| 08 | Chat / Messaging Flow | `docs/uml/viaverse_uml_part_08_chat_messaging/` |
| 09 | Payment / Wallet Flow | `docs/uml/viaverse_uml_part_09_payment_wallet/` |
| 10 | Job Lifecycle / Delivery Approval Flow | `docs/uml/viaverse_uml_part_10_job_lifecycle/` |
| 11 | Review / Trust / Badge / Gamification Flow | `docs/uml/viaverse_uml_part_11_trust_gamification/` |
| 12 | Dispute / Refund / Payout Flow | `docs/uml/viaverse_uml_part_12_dispute_refund_payout/` |
| 13 | Media Upload / CDN / Moderation Flow | `docs/uml/viaverse_uml_part_13_media_cdn_moderation/` |
| 14 | Notification / Inbox / Push / Email / SMS Flow | `docs/uml/viaverse_uml_part_14_notification/` |
| 15 | Admin / Moderation / Operations Flow | `docs/uml/viaverse_uml_part_15_admin_moderation/` |
| 16 | Search / SEO / Growth / AI Discovery Flow | `docs/uml/viaverse_uml_part_16_search_seo_growth/` |
| 17 | Analytics / Observability / Product Intelligence Flow | `docs/uml/viaverse_uml_part_17_analytics_observability/` |
| 18 | Security / Privacy / KVKK Flow | `docs/uml/viaverse_uml_part_18_security_privacy_kvkk/` |
| 19 | Infrastructure / Deployment / Runtime Topology Flow | `docs/uml/viaverse_uml_part_19_infrastructure_deployment/` |
| 20 | AI Assistant / Support / Safety Intelligence Flow | `docs/uml/viaverse_uml_part_20_ai_support_assistant/` |
| 21 | Localization / Multi-language / Regionalization Flow | `docs/uml/viaverse_uml_part_21_localization_regionalization/` |
| 22 | Business Account / Merchant Operations Flow | `docs/uml/viaverse_uml_part_22_business_account_merchant_ops/` |
| 23 | Monetization / Subscription / Ads / Commission Configuration Flow | `docs/uml/viaverse_uml_part_23_monetization_subscription_ads_commission/` |
| 24 | Category / Taxonomy / Dynamic Environment Configuration Flow | `docs/uml/viaverse_uml_part_24_category_taxonomy_dynamic_environment/` |
| 25 | Chat / Messaging / Safety / Matrix-like Secure Conversation Flow | `docs/uml/viaverse_uml_part_25_chat_messaging_safety/` |
| 26 | Roadmap / Release Phases / Team Execution Flow | `docs/uml/viaverse_uml_part_26_roadmap_release_team_execution/` |
| 27 | Repository / Package / Module Structure Flow | `docs/uml/viaverse_uml_part_27_repository_package_module_structure/` |
| 28 | End-to-End System Context / Final Master Topology | `docs/uml/viaverse_uml_part_28_final_master_topology/` |
| 29 | Final UML Index / Diagram Map | `docs/uml/viaverse_uml_part_29_final_uml_index_diagram_map/` |

## AI / MCP Rule

MCP should primarily index:

```text
docs/uml/UML_INDEX.md
docs/uml/**/README.md
docs/uml/**/*.json
docs/uml/**/*.csv
```

Do not load all `.drawio`, `.svg` and `.puml` files unless specifically needed.
