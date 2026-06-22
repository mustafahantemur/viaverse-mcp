package app.viaverse.template.domain.model

data class Account(
    val id: String,
    val displayName: String,
    val identifier: String,
    val status: AccountStatus,
    val capabilities: Set<AccountCapability>
)

enum class AccountStatus {
    ACTIVE,
    SUSPENDED
}

enum class AccountCapability {
    REQUEST_WORK,
    DO_WORK_INDIVIDUALLY,
    OPERATE_BUSINESS,
    SOCIAL_PARTICIPATION
}
