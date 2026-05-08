# Viaverse UML Part 10 — Job Lifecycle / Delivery Approval Flow

Bu part quote kabulünden sonra işin başlaması, teslim edilmesi, onaylanması, tamamlanması ve payout eligibility durumunu anlatır.

## Diagramlar

1. `01_job_use_case`
   - Job creation from accepted quote
   - Payment authorization gate
   - Schedule/address rules
   - Start work
   - Job workspace
   - Job updates
   - Delivery
   - Delivery review
   - Approval
   - Complete job
   - Payout eligibility
   - Dispute/cancel branch

2. `02_job_activity`
   - QuoteAccepted
   - Job draft
   - PaymentAuthorized wait
   - Ready to start
   - In progress
   - Delivered
   - Approved / revision / dispute
   - Completed
   - PayoutEligible
   - Review prompt

3. `03_job_sequence`
   - Kafka QuoteAccepted → marketplace-service
   - PaymentAuthorized unlock
   - Job start/deliver/approve sequence
   - JobCompleted / PayoutEligible events

4. `04_job_class_model`
   - Job
   - JobDelivery
   - JobUpdate
   - JobPolicy
   - JobPaymentState
   - ReviewPrompt
   - JobDisputeRef
   - JobEvents

5. `05_job_component`
   - JobController
   - Job use cases
   - Job policies
   - Payment state port
   - Messaging port
   - PostgreSQL
   - Kafka
   - Trust/review prompt

6. `06_job_state`
   - CREATED
   - WAITING_PAYMENT
   - READY_TO_START
   - IN_PROGRESS
   - DELIVERED
   - REVISION_REQUESTED
   - COMPLETED
   - DISPUTED
   - CANCELLED
   - REFUNDED
   - PAYOUT_ELIGIBLE

## Formatlar

Her diagram için:

- `.drawio`
- `.svg`
- `.puml`

## Kritik Kurallar

- Job accepted quote sonrasında oluşur ama payment authorization olmadan başlanamaz.
- PaymentAuthorized job start gate’idir.
- Exact address / hassas iletişim paylaşımı yalnızca izinli job/payment state’inde açılır.
- Delivery approval job completion sınırıdır.
- Completed job review prompt ve payout eligibility üretir.
- Payout release payment/dispute policy tarafında yapılır.
- Dispute payout’u block eder.
- Job lifecycle work state’i yönetir; provider payment logic ödeme servisinde kalır.
