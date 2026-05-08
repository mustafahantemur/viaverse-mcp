# Viaverse UML Part 13 — Media Upload / CDN / Moderation Flow

Bu part image/video upload, object storage, CDN delivery, metadata stripping, thumbnail/transcode, moderation ve media context attachment akışını anlatır.

## Diagramlar

1. `01_media_use_case`
   - Upload session
   - Media policy validation
   - Presigned URL
   - Client direct upload
   - Confirm upload
   - EXIF stripping
   - Thumbnail/transcode
   - Moderation scan
   - Approve/reject
   - Attach media to context
   - CDN delivery
   - Media report/admin review

2. `02_media_activity`
   - User selects media
   - Upload session validation
   - Presigned URL
   - Direct S3 upload
   - Processing
   - Moderation
   - Approved/rejected
   - CDN URL generation
   - Events

3. `03_media_sequence`
   - Client → BFF → media-service → S3 → media-worker → moderation-service → PostgreSQL → Kafka

4. `04_media_component`
   - Media controllers/use cases/policies/ports
   - ObjectStoragePort
   - CdnUrlPort
   - ModerationPort
   - S3
   - CloudFront
   - media-worker
   - PostgreSQL
   - Kafka

5. `05_media_class_model`
   - MediaAsset
   - UploadSession
   - MediaDerivative
   - MediaModerationResult
   - MediaAttachment
   - MediaReport
   - MediaPolicy
   - MediaEvents

6. `06_media_state`
   - PENDING_UPLOAD
   - UPLOADED
   - PROCESSING
   - PENDING_MODERATION
   - MANUAL_REVIEW
   - APPROVED
   - REJECTED
   - ATTACHED
   - DELETED

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Binary dosyalar backend üzerinden proxy edilmez; client presigned URL ile doğrudan object storage’a upload eder.
- App servisleri mediaId/reference tutar; binary blob saklamaz.
- Original object private tutulur.
- Public profile/search media CDN cache olabilir.
- Chat/job/delivery private media signed access ister.
- EXIF/location metadata stripping yapılır.
- Video için transcode/preview/thumbnail pipeline gerekir.
- Moderation approved olmadan public/profile/search surface’e çıkmaz.
- Domain services media object key/CDN policy sahibi değildir; sadece mediaId referansı kullanır.
