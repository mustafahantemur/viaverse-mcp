# Viaverse UML Part 07 — Quote / Offer Flow

Bu part worker/business tarafından teklif verilmesi ve requester tarafından tekliflerin karşılaştırılıp kabul edilmesi akışını anlatır.

## Diagramlar

1. `01_quote_use_case`
   - Worker/business request detail görür.
   - Quote draft oluşturur.
   - Price/message/schedule girer.
   - Quote gönderir, revize eder, geri çeker.
   - Requester quote alır, karşılaştırır, kabul/reddeder.

2. `02_quote_activity`
   - Eligibility validation
   - Quote draft
   - Price/currency
   - Schedule/duration
   - Message
   - QuoteSent
   - Requester comparison
   - Accept boundary to payment/job flow

3. `03_quote_sequence`
   - Worker Client → BFF → marketplace-service → identity-service → PostgreSQL → Kafka → notification-service → Requester Client.
   - Accept quote idempotent flow.

4. `04_quote_class_model`
   - Quote
   - QuoteRevision
   - QuotePolicy
   - ServiceRequest
   - Job
   - QuoteEvents
   - OutboxEvent
   - QuoteVisibilityRule

5. `05_quote_state`
   - DRAFT
   - SENT
   - QUESTIONED
   - REVISION_REQUESTED
   - REVISED
   - ACCEPTED
   - REJECTED
   - WITHDRAWN
   - EXPIRED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Sending quotes/offers is free.
- Requester kendi requestine gelen tüm quote’ları görebilir.
- Worker/business sadece kendi quote’unu görür.
- Competitor quote details görünmez.
- Quote acceptance idempotent olmalı.
- One accepted quote per request.
- Quote accepted olduğunda Job draft oluşur ve ödeme/job lifecycle partına geçilir.
