# Viaverse UML Part 12 — Dispute / Refund / Payout Flow

Bu part anlaşmazlık, evidence, payout block, admin resolution, refund, payout release ve risk update akışını anlatır.

## Diagramlar

1. `01_dispute_use_case`
   - Open dispute
   - Select reason
   - Submit evidence
   - Block payout
   - Notify parties
   - Party response
   - Admin review
   - Refund / payout / split resolution
   - Audit
   - Close dispute
   - Trust/risk update

2. `02_dispute_activity`
   - Validate dispute
   - Block payout
   - Collect evidence
   - Notify + wait response
   - Admin review
   - Resolution decision
   - Refund / payout / split
   - Audit + close + risk update

3. `03_dispute_sequence`
   - Client → BFF → dispute-service → marketplace-service → payment-service → messaging-service → PostgreSQL → Kafka

4. `04_dispute_class_model`
   - DisputeCase
   - DisputeEvidence
   - DisputeResponse
   - DisputeResolution
   - DisputePolicy
   - DisputeAuditLog
   - PaymentResolutionAction
   - DisputeEvents

5. `05_dispute_state`
   - OPENED
   - VALIDATING
   - EVIDENCE_COLLECTION
   - ADMIN_REVIEW
   - RESOLVED_REFUND
   - RESOLVED_PAYOUT
   - RESOLVED_SPLIT
   - REJECTED_INVALID
   - ESCALATED
   - CLOSED

6. `06_dispute_component`
   - Client/Admin UI
   - BFF
   - Dispute controllers/use cases/policies/ports
   - marketplace-service
   - payment-service
   - messaging-service
   - PostgreSQL
   - Kafka
   - trust/risk-service

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Dispute payout release öncesinde açılırsa payout block edilir.
- Dispute kapalı değilse payout release yapılamaz.
- Evidence mümkün olduğunca source record/media ref olarak tutulur.
- Private chat/media kopyalama minimize edilmeli ve erişim auditlenmeli.
- Refund/payout/split money movement payment-service tarafından yapılır.
- Dispute service case outcome ve audit sahibidir.
- Refund, payout release ve split idempotent olmalıdır.
- Her admin resolution reason + audit ister.
- Resolution sonrası trust/risk signal güncellenir.
