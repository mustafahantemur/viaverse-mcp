# Viaverse UML Part 27 — Repository / Package / Module Structure Flow

Bu part Viaverse monorepo yapısı, Spring Boot backend service package yapısı, KMP/Compose Multiplatform client module yapısı, contract ownership, dependency rules ve branch/release state modelini anlatır.

## Diagramlar

1. `01_repository_tree_package`
   - `apps/`, `backend/`, `infra/`, `docs/`, `tools/`, `quality/`, `.github/workflows/`
   - `AGENTS.md`, `README.md`, `CODING_RULES.md`, `ARCHITECTURE.md`, `ROADMAP.md`

2. `02_backend_service_hexagonal_package`
   - `application`, `domain`, `adapter/in/web`, `adapter/out/persistence`, `adapter/out/events`, `adapter/in/security`, `config`, `src/test`, `resources/db/migration`, `api/`

3. `03_kmp_client_module_structure`
   - `composeApp`, `shared/core`, `shared/design-system`, `shared/domain`, `shared/data`, feature modules, platform source sets, network/testing modules

4. `04_contract_generation_sequence`
   - ADR/API/event change proposal, backend contract update, contracts folder, CI contract tests, KMP client generation/update, BFF mapping, release gate

5. `05_module_dependency_rules`
   - Domain, Application, Inbound adapter, Outbound adapter, Common libraries, Contract artifacts, Test modules, Forbidden coupling

6. `06_branch_environment_release_state`
   - main, develop, feature branch, pull request, release branch, staging deploy, production tag, hotfix, archived branch

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Monorepo kullanılabilir ama bounded context sınırları görünür kalmalı.
- Shared libs dumping ground olmamalı.
- Domain logic bounded context içinde yaşamalı.
- Controller repository ile direkt konuşmaz.
- Application use case orchestration yapar.
- Domain Spring/JPA/Ktor bağımlılığı taşımaz.
- API DTO’ları Compose screen’lere sızmaz.
- Design tokens centralized tutulur.
- Contracts versioned artifact’tır.
- Backend, BFF ve KMP client contract drift yaşamamalı.
- Breaking changes versioning veya migration plan ister.
- Dependencies inward yönlenir.
- Forbidden coupling ArchUnit/detekt/quality gate ile fail edilmelidir.
- Short-lived feature branches, immutable production tags, hotfix back-merge kuralı uygulanır.
