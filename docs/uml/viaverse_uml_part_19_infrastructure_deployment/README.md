# Viaverse UML Part 19 — Infrastructure / Deployment / Runtime Topology Flow

Bu part AWS hedef topolojisi, CI/CD, runtime request flow, infrastructure component boundaries, deployment config ve release state yönetimini anlatır.

## Diagramlar

1. `01_aws_infrastructure_topology`
   - Route 53
   - CloudFront
   - AWS WAF
   - ALB / Ingress
   - EKS private app subnets
   - RDS PostgreSQL
   - ElastiCache Redis
   - MSK Kafka / managed Kafka
   - S3
   - OpenSearch
   - KMS / Secrets Manager / External Secrets
   - Observability stack
   - GitHub Actions / ECR

2. `02_cicd_deployment_activity`
   - Protected branch merge
   - Tests
   - Quality gates
   - Build image
   - ECR push
   - Flyway migration gate
   - EKS deploy
   - Smoke tests
   - Observe metrics/logs
   - Rollback/promote

3. `03_runtime_request_sequence`
   - Client → CloudFront/WAF → ALB/Ingress → API Gateway/BFF → Domain Service → DB/Kafka/OTel

4. `04_infrastructure_component`
   - Client apps
   - Edge layer
   - Gateway layer
   - Service layer
   - Worker layer
   - Data layer
   - Security layer
   - Observability layer
   - Delivery layer
   - Ops layer

5. `05_infrastructure_config_class_model`
   - ServiceConfig
   - SecretRef
   - Release
   - DbMigration
   - DeploymentPolicy
   - ServiceSLO
   - BackupPlan
   - InfraEvents

6. `06_release_state`
   - DRAFT
   - CI_RUNNING
   - BLOCKED
   - READY_TO_DEPLOY
   - MIGRATING
   - DEPLOYING
   - VERIFYING
   - RELEASED
   - ROLLBACK_REQUIRED
   - ROLLED_BACK

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Managed and boring başlangıç: AWS edge + EKS + RDS + Redis + Kafka + S3 + OpenSearch.
- Hem Kafka hem Redpanda aynı ortamda kullanılmaz; tek broker dili seçilir.
- Service-to-service consistency için event/outbox tercih edilir; distributed transaction’dan kaçınılır.
- Database migration rolling deploy ile backward-compatible olmalıdır.
- Environment config declarative ve versioned olmalıdır.
- Secrets committed value değil, reference olarak tutulur.
- Rollback test edilmeli; DB için expand-contract migration yaklaşımı kullanılmalı.
- Topoloji microservice evolution desteklemeli ama gereksiz infra karmaşası yaratmamalı.
