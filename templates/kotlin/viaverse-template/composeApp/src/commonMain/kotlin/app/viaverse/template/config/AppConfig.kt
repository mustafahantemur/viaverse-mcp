package app.viaverse.template.config

import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AccountCapability
import app.viaverse.template.domain.model.AccountStatus

object AppConfig {
    val auth = AuthConfig(
        mockOtp = "111111",
        allowDevSkip = false,
        apiBaseUrl = "https://api.viaverse.local/mock",
        mockCredentials = listOf(
            MockCredential(
                identifier = "zehra@viaverse.test",
                password = "zehra123",
                twoFactorEnabled = false
            ),
            MockCredential(
                identifier = "ahmet@viaverse.test",
                password = "ahmet123",
                twoFactorEnabled = true
            ),
            MockCredential(
                identifier = "merve@viaverse.test",
                password = "merve123",
                twoFactorEnabled = false
            ),
            MockCredential(
                identifier = "can@viaverse.test",
                password = "can123",
                twoFactorEnabled = true
            )
        )
    )

    val mockUsers = listOf(
        Account(
            id = "acc_requester_001",
            displayName = "Zehra Erdogan",
            identifier = "zehra@viaverse.test",
            status = AccountStatus.ACTIVE,
            capabilities = setOf(AccountCapability.REQUEST_WORK)
        ),
        Account(
            id = "acc_worker_001",
            displayName = "Ahmet Yilmaz",
            identifier = "ahmet@viaverse.test",
            status = AccountStatus.ACTIVE,
            capabilities = setOf(
                AccountCapability.REQUEST_WORK,
                AccountCapability.DO_WORK_INDIVIDUALLY
            )
        ),
        Account(
            id = "acc_business_draft_001",
            displayName = "Merve Kaya",
            identifier = "merve@viaverse.test",
            status = AccountStatus.ACTIVE,
            capabilities = setOf(
                AccountCapability.REQUEST_WORK,
                AccountCapability.OPERATE_BUSINESS
            )
        ),
        Account(
            id = "acc_business_live_001",
            displayName = "Can Demir",
            identifier = "can@viaverse.test",
            status = AccountStatus.ACTIVE,
            capabilities = setOf(
                AccountCapability.REQUEST_WORK,
                AccountCapability.DO_WORK_INDIVIDUALLY,
                AccountCapability.OPERATE_BUSINESS
            )
        )
    )
}

data class AuthConfig(
    val mockOtp: String,
    val allowDevSkip: Boolean,
    val apiBaseUrl: String,
    val mockCredentials: List<MockCredential>
) {
    fun normalizedIdentifier(identifier: String): String {
        return identifier.trim().lowercase()
    }

    fun passwordFor(identifier: String): String? {
        val normalized = normalizedIdentifier(identifier)
        return mockCredentials.firstOrNull { normalizedIdentifier(it.identifier) == normalized }?.password
    }

    fun isTwoFactorEnabled(identifier: String): Boolean {
        val normalized = normalizedIdentifier(identifier)
        return mockCredentials.firstOrNull { normalizedIdentifier(it.identifier) == normalized }?.twoFactorEnabled == true
    }
}

data class MockCredential(
    val identifier: String,
    val password: String,
    val twoFactorEnabled: Boolean
)
