# Viaverse UML Part 17 — Analytics / Observability / Product Intelligence Flow

Bu part product analytics, event governance, observability, tracing, metrics, logs, dashboards, alerts ve funnel/experiment analizlerini anlatır.

## Diagramlar

1. `01_analytics_observability_use_case`
   - Track product event
   - Consent/privacy/PII minimization
   - Session/anonymous ID
   - Analytics ingestion
   - Distributed trace
   - Metrics
   - Structured logs
   - Trace/log/event correlation
   - Dashboards
   - SLO/error/anomaly alerts
   - Funnel and experiment analysis
   - Retention/deletion

2. `02_analytics_ingestion_activity`
   - Capture event
   - Consent/legal basis
   - Sanitize payload
   - Schema validation
   - DLQ/quality alert
   - Persist raw-safe event
   - Aggregate funnel/cohort/KPI
   - Retention policy

3. `03_observability_sequence`
   - Client/API Gateway/domain service/OpenTelemetry/Prometheus/Jaeger/OpenSearch/Alerting
   - traceparent and correlationId propagation

4. `04_analytics_observability_component`
   - Client instrumentation
   - API Gateway
   - Spring Boot services
   - analytics-service
   - OpenTelemetry Collector
   - Prometheus/Perses
   - Jaeger
   - OpenSearch
   - Analytics warehouse
   - Alertmanager/Ops
   - Kafka

5. `05_analytics_observability_class_model`
   - ProductEvent
   - EventSchema
   - AnalyticsConsent
   - ServiceMetric
   - TraceContext
   - StructuredLog
   - AlertRule
   - ObservabilityEvents

6. `06_analytics_event_state`
   - CAPTURED
   - CONSENT_CHECKED
   - SANITIZED
   - SCHEMA_VALIDATED
   - DLQ_SCHEMA_FAILED
   - STORED
   - AGGREGATED
   - RETENTION_EXPIRED
   - ALERT_RAISED
   - ALERT_RESOLVED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Product analytics privacy-safe olmalı.
- Observability data traceId/correlationId ile joinable olmalı.
- Secrets/raw personal data logs/traces/metrics içine sızmamalı.
- Product analytics consent/governance ister.
- Observability redaction ve secret filtering ister.
- Analytics event properties schema allowlist ile kabul edilmeli.
- Unknown schema production metric’e sessizce dahil edilmemeli; DLQ + quality alert gerekir.
- Event owner, schema version, retention class ve sensitivity classification zorunludur.
