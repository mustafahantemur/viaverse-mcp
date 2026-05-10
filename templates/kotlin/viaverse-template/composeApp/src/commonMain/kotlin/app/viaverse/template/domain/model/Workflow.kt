package app.viaverse.template.domain.model

enum class RequestDraftStatus {
    EDITING,
    READY_TO_MATCH,
    SENT_TO_MATCHING
}

enum class SchedulePreference {
    TODAY,
    THIS_WEEK,
    FLEXIBLE
}

enum class ProviderSetupStepStatus {
    TODO,
    READY,
    DONE
}

enum class ProfileSettingKind {
    IDENTITY,
    SECURITY,
    PAYMENT,
    PRIVACY,
    NOTIFICATION,
    SUPPORT,
    BUSINESS
}

enum class ChatPolicyState {
    PRE_ACCEPTANCE_RESTRICTED,
    JOB_ACCEPTED,
    SUPPORT_REVIEW
}

enum class CustomerJobStatus {
    MATCHING,
    OFFER_RECEIVED,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    DISPUTED
}

enum class OfferStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    EXPIRED
}

enum class SafePaymentStatus {
    NOT_STARTED,
    AUTHORIZED,
    HELD_IN_ESCROW,
    RELEASED,
    REFUND_REVIEW,
    DISPUTED
}

enum class ReviewStatus {
    WAITING_FOR_YOU,
    WAITING_FOR_COUNTERPARTY,
    PUBLISHED
}

enum class SupportTicketStatus {
    OPEN,
    WAITING_FOR_USER,
    ESCALATED,
    RESOLVED
}

enum class ProviderAvailability {
    AVAILABLE,
    BUSY,
    PAUSED
}

data class RequestDraft(
    val titleTr: String,
    val descriptionTr: String,
    val categoryId: ServiceCategoryId,
    val locationTr: String,
    val budgetHintTr: String,
    val schedulePreference: SchedulePreference,
    val status: RequestDraftStatus,
    val aiSuggestionsTr: List<String>
)

data class CustomerJob(
    val id: String,
    val titleTr: String,
    val categoryId: ServiceCategoryId,
    val status: CustomerJobStatus,
    val locationTr: String,
    val scheduleTr: String,
    val descriptionTr: String,
    val offerCount: Int,
    val paymentStatus: SafePaymentStatus,
    val aiSummaryTr: String
)

data class OfferPreview(
    val id: String,
    val jobId: String,
    val providerNameTr: String,
    val priceTr: String,
    val ratingTr: String,
    val status: OfferStatus,
    val messageTr: String,
    val trustSignalsTr: List<String>
)

data class ProviderMetric(
    val titleTr: String,
    val valueTr: String,
    val bodyTr: String
)

data class ProviderOpportunity(
    val id: String,
    val titleTr: String,
    val categoryId: ServiceCategoryId,
    val requesterTr: String,
    val locationTr: String,
    val budgetTr: String,
    val descriptionTr: String,
    val fitTr: String
)

data class ProviderJob(
    val id: String,
    val titleTr: String,
    val status: WorkLifecycleStatus,
    val requesterTr: String,
    val payoutTr: String,
    val nextStepTr: String
)

data class ProviderDashboardSnapshot(
    val availability: ProviderAvailability,
    val metrics: List<ProviderMetric>,
    val todayTasksTr: List<String>,
    val opportunities: List<ProviderOpportunity>,
    val activeJobs: List<ProviderJob>,
    val insights: List<AiInsight>
)

data class WalletTransaction(
    val id: String,
    val titleTr: String,
    val amountTr: String,
    val status: SafePaymentStatus,
    val dateTr: String
)

data class WalletSnapshot(
    val escrowBalanceTr: String,
    val payoutBalanceTr: String,
    val providerTokenInfoTr: String,
    val transactions: List<WalletTransaction>,
    val safetyNotesTr: List<String>
)

data class ReviewSummary(
    val id: String,
    val titleTr: String,
    val counterpartyTr: String,
    val status: ReviewStatus,
    val ratingTr: String,
    val bodyTr: String
)

data class SupportTicket(
    val id: String,
    val titleTr: String,
    val status: SupportTicketStatus,
    val bodyTr: String,
    val nextStepTr: String
)

data class SettingDetail(
    val id: String,
    val titleTr: String,
    val bodyTr: String,
    val fieldsTr: List<String>,
    val riskNotesTr: List<String>
)

data class PublicProfileSnapshot(
    val displayNameTr: String,
    val headlineTr: String,
    val ratingTr: String,
    val completedJobsTr: String,
    val badgesTr: List<String>,
    val reviewHighlightsTr: List<String>,
    val portfolioTr: List<String>
)

data class ProviderSetupStep(
    val titleTr: String,
    val bodyTr: String,
    val status: ProviderSetupStepStatus
)

data class ProviderOnboardingSnapshot(
    val titleTr: String,
    val bodyTr: String,
    val selectedCategories: List<ServiceCategoryId>,
    val workAreaTr: String,
    val steps: List<ProviderSetupStep>,
    val insights: List<AiInsight>
)

data class BusinessSetupStep(
    val titleTr: String,
    val bodyTr: String,
    val status: ProviderSetupStepStatus
)

data class BusinessWorkspaceDetail(
    val summary: BusinessWorkspaceSummary,
    val steps: List<BusinessSetupStep>,
    val publishingChecksTr: List<String>,
    val insights: List<AiInsight>
)

data class ProfileSettingGroup(
    val titleTr: String,
    val kind: ProfileSettingKind,
    val items: List<ProfileSettingItem>
)

data class ProfileSettingItem(
    val id: String,
    val titleTr: String,
    val bodyTr: String,
    val statusTr: String
)

data class ChatMessage(
    val id: String,
    val fromCurrentUser: Boolean,
    val textTr: String,
    val timeTr: String
)

data class ChatThread(
    val id: String,
    val titleTr: String,
    val contextTr: String,
    val policyState: ChatPolicyState,
    val messages: List<ChatMessage>,
    val aiSafetyNotesTr: List<String>
)
