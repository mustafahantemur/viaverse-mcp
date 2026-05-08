# Viaverse UML Part 08 — Chat / Messaging Flow

Bu part quote/job bağlı messaging akışını anlatır.

## Diagramlar

1. `01_chat_use_case`
   - Conversation create/open
   - Context card
   - Send/read/mark-read
   - Media attachment
   - Contact/payment bypass detection
   - Report/block/support escalation
   - Audited admin/support review

2. `02_chat_activity`
   - Participant authorization
   - Optional media
   - Safety scan
   - Warn/block/risk signal
   - Persist message
   - Notify recipient
   - Mark read
   - Report/support case

3. `03_chat_sequence`
   - Client → BFF → messaging-service → marketplace-service → safety policy → media-service → PostgreSQL/Cassandra → notification-service

4. `04_chat_component`
   - MessagingController
   - Messaging use cases
   - Messaging policies
   - Conversation/message ports
   - Marketplace context
   - Media
   - Safety/moderation
   - Notification
   - Kafka/outbox

5. `05_chat_class_model`
   - Conversation
   - ConversationParticipant
   - Message
   - MessageAttachment
   - MessagingPolicy
   - SafetyDetection
   - MessageReport
   - MessagingEvents

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Chat generic social DM değildir; quote/job/support context’e bağlıdır.
- Participant authorization zorunludur.
- Contact sharing / off-platform payment bypass attempt kabul edilen job/payment state öncesinde tespit edilir.
- Media message içine binary olarak gömülmez; mediaId reference olarak bağlanır.
- Private chat content ad targeting için kullanılmaz.
- Admin/support access scoped, reasoned and audited olmalı.
- Future E2EE-ready boundary korunmalı; ilk fazda server-stored messages + strict policy/reporting kullanılabilir.
