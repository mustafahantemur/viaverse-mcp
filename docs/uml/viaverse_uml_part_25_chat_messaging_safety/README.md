# Viaverse UML Part 25 — Chat / Messaging / Safety / Matrix-like Secure Conversation Flow

Bu part job/order scoped messaging, realtime delivery, read receipts, attachments, Cassandra message timeline, PII/off-platform payment detection, moderation/reporting, support/dispute export and retention modelini anlatır.

## Diagramlar

1. `01_chat_messaging_use_case`
   - Open job conversation
   - Validate participants and job/order permissions
   - Send message
   - Deliver/read receipts
   - Send attachments
   - Unsafe content detection
   - PII/off-platform payment/scam/harassment checks
   - Block/warn/rate-limit
   - Notifications
   - Report conversation/message
   - Moderator scoped review
   - Moderation action
   - Dispute/legal export
   - Retention/deletion

2. `02_message_send_safety_activity`
   - Session/participant validation
   - Conversation/job state validation
   - Rate limit
   - Content classification
   - Safety scan
   - Warn/block/hold
   - Persist message
   - Delivery receipts
   - Notifications
   - Conversation list update

3. `03_chat_delivery_sequence`
   - Sender Client → Realtime BFF → chat-service → marketplace-service → safety-service → Cassandra → notification-service → Recipient Client

4. `04_chat_component`
   - KMP chat UI
   - Realtime BFF
   - Chat controllers/use cases/policies/ports
   - marketplace-service
   - safety/moderation-service
   - media-service
   - Cassandra
   - PostgreSQL
   - Kafka

5. `05_chat_class_model`
   - Conversation
   - ConversationParticipant
   - Message
   - MessageReceipt
   - MessageReport
   - ChatSafetyPolicy
   - ChatExport
   - ChatEvents

6. `06_conversation_message_state`
   - CONVERSATION_OPEN
   - ACTIVE
   - LOCKED
   - ARCHIVED
   - MESSAGE_DRAFT
   - SCANNING
   - SENT
   - HELD_FOR_REVIEW
   - BLOCKED
   - DELIVERED
   - READ

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Chat job/order/support context scoped olmalı.
- Matrix-like federation launch için gerekli değil; internal messaging modular tasarlanır.
- Realtime transport adapter-level kalmalı; core chat-service WebSocket/SSE/polling’den bağımsız çalışmalı.
- Kişisel bilgi, off-platform payment, scam, harassment ve unsafe media için safety policy çalışmalı.
- Unsafe message policy’ye göre blocked/warned/held/allowed olabilir; sessizce düşürülmez.
- Cassandra high-volume message timeline/read receipts için; PostgreSQL conversation metadata/report/policy state için kullanılır.
- Attachments chat DB’de binary değil, mediaId ref olarak tutulur.
- Conversation state ve message state ayrı tutulur.
- Locked conversation readable olabilir ama writable olmayabilir.
- Export dispute/legal/DSAR purpose ile policy-controlled yapılır.
