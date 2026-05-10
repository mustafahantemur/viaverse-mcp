package app.viaverse.template.data.service

import app.viaverse.template.config.AppConfig
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AccountCapability
import app.viaverse.template.domain.model.AccountStatus

class MockAuthService(initialUsers: List<Account>) {
    private val users = initialUsers.associateBy { normalize(it.identifier) }.toMutableMap()
    private val localPasswords = AppConfig.auth.mockCredentials
        .associate { normalize(it.identifier) to it.password }
        .toMutableMap()
    private val localTwoFactorFlags = AppConfig.auth.mockCredentials
        .associate { normalize(it.identifier) to it.twoFactorEnabled }
        .toMutableMap()

    fun findAccount(identifier: String): Account? {
        return users[normalize(identifier)]
    }

    fun requiresTwoFactor(identifier: String): Boolean {
        return localTwoFactorFlags[normalize(identifier)] == true
    }

    fun requestOtp(identifier: String): Boolean {
        return normalize(identifier).isNotBlank()
    }

    fun verifyPassword(identifier: String, password: String): Account? {
        val expectedPassword = localPasswords[normalize(identifier)] ?: return null
        if (password != expectedPassword) return null
        return users[normalize(identifier)]
    }

    fun verifyOtp(identifier: String, otp: String): Account? {
        if (otp != AppConfig.auth.mockOtp) return null
        return users[normalize(identifier)]
    }

    fun signUp(name: String, identifier: String, password: String, otp: String): Account? {
        if (otp != AppConfig.auth.mockOtp) return null
        if (password.length < 6) return null
        val account = Account(
            id = "acc_local_${normalize(identifier).hashCode().toString().replace("-", "n")}",
            displayName = name.ifBlank { "Yeni Viaverse Kullanıcısı" },
            identifier = normalize(identifier),
            status = AccountStatus.ACTIVE,
            capabilities = setOf(
                AccountCapability.REQUEST_WORK,
                AccountCapability.SOCIAL_PARTICIPATION
            )
        )
        users[normalize(identifier)] = account
        localPasswords[normalize(identifier)] = password
        localTwoFactorFlags[normalize(identifier)] = false
        return account
    }

    private fun normalize(identifier: String): String {
        return AppConfig.auth.normalizedIdentifier(identifier)
    }
}
