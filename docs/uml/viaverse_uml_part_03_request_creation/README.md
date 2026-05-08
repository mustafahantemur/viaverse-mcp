# Viaverse UML Part 03 — Request Creation / İlan Açma Flow

Bu part sadece iş yaptırmak isteyen kullanıcının service request / ilan oluşturma akışına odaklanır.

## Diagramlar

1. `01_request_create_use_case`
   - Requester, AI Assistant, Media Service, Map Provider
   - Create sheet, request service, description, category, budget, location, media, draft, preview, publish

2. `02_request_create_activity`
   - Draft-first activity flow
   - AI suggestion, budget, location, media optional branch, preview, draft/publish decision

3. `03_request_create_sequence`
   - Client → BFF → marketplace-service → AI/media/maps/PostgreSQL/Kafka sequence
   - Publish transaction and outbox events

4. `04_request_create_component`
   - RequestController, use cases, policies, repository ports, media-service, AI adapter, MapProvider, Kafka, search/matching consumers

5. `05_request_create_class_model`
   - ServiceRequest, Budget, RequestLocation, RequestTiming, RequestMediaRef, Category, RequestPolicy, RequestEvents, OutboxEvent

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Request önce DRAFT olarak oluşabilir.
- PUBLISH sırasında required field + REQUEST_WORK capability validation yapılır.
- AI category/budget önerileri editable olur.
- Media gerçek dosya olarak request içine gömülmez; mediaId reference olarak bağlanır.
- Exact address private kalır; matching/public tarafında approximate area kullanılır.
- Publish transaction source-of-truth state update + outbox event yazar.
- Publish sonrası `ServiceRequestPublished` ve `MatchingRequested` eventleri oluşur.
