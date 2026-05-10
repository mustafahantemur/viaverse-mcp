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
