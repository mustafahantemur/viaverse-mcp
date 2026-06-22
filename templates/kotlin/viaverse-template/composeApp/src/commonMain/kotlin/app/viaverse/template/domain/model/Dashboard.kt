package app.viaverse.template.domain.model

enum class RequestLifecycleStatus {
    DRAFT,
    MATCHING,
    OFFER_RECEIVED,
    SCHEDULED,
    COMPLETED,
    CANCELLED
}

enum class WorkLifecycleStatus {
    ONBOARDING_REQUIRED,
    OPEN,
    OFFER_SENT,
    IN_PROGRESS
}

enum class ConversationStatus {
    NEEDS_REPLY,
    SAFE,
    SUPPORT_REVIEW
}

enum class BusinessWorkspaceStatus {
    NOT_STARTED,
    PENDING_VERIFICATION,
    PUBLISH_READY,
    PUBLISHED
}

data class RequestSummary(
    val id: String,
    val titleTr: String,
    val categoryId: ServiceCategoryId,
    val status: RequestLifecycleStatus,
    val locationTr: String,
    val budgetHintTr: String,
    val aiSummaryTr: String
)

data class WorkSummary(
    val id: String,
    val titleTr: String,
    val categoryId: ServiceCategoryId,
    val status: WorkLifecycleStatus,
    val payoutHintTr: String,
    val distanceKm: Double,
    val trustFitTr: String
)

data class ConversationPreview(
    val id: String,
    val titleTr: String,
    val lastMessageTr: String,
    val status: ConversationStatus,
    val contextTr: String
)

data class CapabilitySummary(
    val capability: AccountCapability,
    val titleTr: String,
    val bodyTr: String,
    val enabled: Boolean
)

data class BusinessWorkspaceSummary(
    val status: BusinessWorkspaceStatus,
    val titleTr: String,
    val verificationStepTr: String,
    val subscriptionStateTr: String,
    val aiReadinessTr: String
)

data class DashboardSnapshot(
    val requests: List<RequestSummary>,
    val work: List<WorkSummary>,
    val conversations: List<ConversationPreview>,
    val capabilities: List<CapabilitySummary>,
    val businessWorkspace: BusinessWorkspaceSummary,
    val insights: List<AiInsight>
)
