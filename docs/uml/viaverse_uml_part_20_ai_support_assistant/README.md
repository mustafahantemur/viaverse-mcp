# Viaverse UML Part 20 — AI Assistant / Support / Safety Intelligence Flow

Bu part uygulama içi AI yardım, support escalation, knowledge retrieval, privacy guardrails, LLM adapter, human handoff ve support ticket akışlarını anlatır.

## Diagramlar

1. `01_ai_support_use_case`
   - Ask AI help
   - Scoped context
   - Safety/privacy guardrails
   - RAG knowledge retrieval
   - Intent classification
   - Guided action suggestion
   - Human escalation
   - Support ticket
   - Support summary
   - Agent AI assistance
   - Feedback
   - Evals and prompt/policy management

2. `02_ai_assistant_activity`
   - Session/capability check
   - Intent classification
   - Minimal context build
   - PII/secret redaction
   - Knowledge retrieval
   - Risky action branch
   - LLM call
   - Output safety checks
   - Fallback/human handoff
   - Privacy-safe logging

3. `03_ai_support_sequence`
   - Client → BFF → ai-assistant-service → policy/privacy guard → knowledge base/vector index → LLM adapter → support-service → Kafka

4. `04_ai_support_component`
   - AI help UI
   - BFF
   - AiAssistantController / SupportController
   - AI use cases
   - AI policies
   - LLM port
   - Knowledge retrieval port
   - Domain context adapters
   - support-service
   - PostgreSQL
   - Kafka

5. `05_ai_support_class_model`
   - AiSession
   - AiMessage
   - RetrievalCitation
   - SupportTicket
   - AiPolicy
   - AiFeedback
   - PromptConfig
   - AiSupportEvents

6. `06_ai_support_state`
   - AI_SESSION_STARTED
   - CONTEXT_READY
   - GENERATING
   - ANSWERED
   - FALLBACK
   - ESCALATED_TO_SUPPORT
   - TICKET_OPENED
   - AGENT_ASSIGNED
   - RESOLVED
   - FEEDBACK_RECEIVED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- AI layer domain source-of-truth değildir.
- AI business state’i doğrudan değiştirmez; domain workflow gerekir.
- AI ödeme, refund, suspension, account deletion gibi risky action’ları explicit policy-controlled confirmation olmadan yapmaz.
- Model call öncesinde minimal context + PII/secret redaction uygulanır.
- Full private chat, payment data, identity document veya secrets prompt’a konmaz.
- RAG source hints/citations internal olarak tutulur.
- Support escalation için conversation summary üretilir ama sensitive data minimization korunur.
- Raw prompt/context retention short-lived ve controlled olmalıdır.
