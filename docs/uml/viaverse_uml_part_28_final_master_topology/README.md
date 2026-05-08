# Viaverse UML Part 28 — End-to-End System Context / Final Master Topology

Bu part bütün önceki UML parçalarını tek master bağlama oturtur: client channels, edge, gateway, bounded contexts, data stores, event bus, observability, cross-cutting governance, source-of-truth ownership ve program state.

## Diagramlar

1. `01_final_system_context_topology`
   - KMP App
   - Next.js landing/admin
   - Route53 / CloudFront / WAF
   - API Gateway / BFF
   - identity / profile / taxonomy / marketplace / business / payment / chat / notification
   - search / trust / media / ai-assistant
   - PostgreSQL / Cassandra / Redis / Kafka / S3 / OpenSearch
   - Observability / Secrets

2. `02_request_to_job_master_flow`
   - Request creation
   - Taxonomy category/form
   - Marketplace request/offer/job
   - Search matching
   - Notification
   - Provider quote
   - Payment
   - Chat
   - Trust/review
   - Kafka/observability

3. `03_business_merchant_master_flow`
   - Business UI
   - Business capability
   - Business profile/catalog/staff
   - Media
   - Monetization plan gates
   - Search publish
   - Lead routing
   - Admin verification
   - Analytics/reporting

4. `04_cross_cutting_governance_map`
   - Security
   - Privacy/KVKK
   - Observability
   - Eventing
   - Contracts
   - Localization
   - Safety/moderation
   - Money governance
   - Data governance
   - Quality gates

5. `05_domain_ownership_matrix`
   - Source-of-truth ownership per bounded context
   - Which service owns what
   - Which events/projections connect contexts

6. `06_master_program_state`
   - IDEA / BLUEPRINT
   - FOUNDATION_READY
   - ALPHA_VERTICAL_SLICE
   - BETA_MARKETPLACE_READY
   - PAYMENT_TRUST_PILOT
   - BUSINESS_LAUNCH_READY
   - PUBLIC_TR_LAUNCHED
   - SCALE_READY
   - GLOBAL_READY
   - CONTINUOUS_EVOLUTION

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Every external request enters through edge/gateway.
- Domain services own source-of-truth.
- Search, AI and analytics consume projections, not raw authority.
- One broker language: Kafka through Spring Cloud Stream.
- Redpanda can be local-compatible only if intentionally selected as Kafka-compatible runtime, not a second broker concept.
- Request creation is a vertical slice connected to taxonomy, matching, notification, offer, payment, chat, completion and trust.
- Business publish requires verification + catalog + branch + subscription gates.
- Cross-cutting concerns are not optional decorations.
- Every bounded context must declare security, privacy, event, data, observability and test behavior.
- A service may read another service's IDs/projections, but it must not own or mutate another bounded context's aggregate.
