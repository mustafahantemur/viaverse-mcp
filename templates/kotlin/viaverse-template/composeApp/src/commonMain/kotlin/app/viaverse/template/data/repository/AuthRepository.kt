package app.viaverse.template.data.repository

import app.viaverse.template.config.AppConfig
import app.viaverse.template.data.service.MockAuthService
import app.viaverse.template.data.storage.StorageService
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AuthDecision

class AuthRepository(
    private val service: MockAuthService,
    private val storage: StorageService
) {
    fun requestOtp(identifier: String): Boolean {
        return service.requestOtp(normalize(identifier))
    }

    fun resolveIdentifier(identifier: String): AuthDecision {
        val normalizedIdentifier = normalize(identifier)
        val account = service.findAccount(normalizedIdentifier) ?: storage.findAccount(normalizedIdentifier)
        if (account == null) return AuthDecision.OTP_FOR_SIGN_UP
        return AuthDecision.PASSWORD_REQUIRED
    }

    fun requiresTwoFactor(identifier: String): Boolean {
        return service.requiresTwoFactor(normalize(identifier))
    }

    fun verifyOtp(identifier: String, otp: String): Account? {
        val normalizedIdentifier = normalize(identifier)
        val account = service.verifyOtp(normalizedIdentifier, otp)
            ?: if (otp == AppConfig.auth.mockOtp) storage.findAccount(normalizedIdentifier) else null
        if (account != null) {
            storage.saveAccount(account)
            storage.saveSession(account.id)
        }
        return account
    }

    fun signUp(name: String, identifier: String, otp: String): Account? {
        val account = service.signUp(name, normalize(identifier), otp)
        if (account != null) {
            storage.saveAccount(account)
            storage.saveSession(account.id)
        }
        return account
    }

    fun verifyPassword(identifier: String, password: String): Account? {
        val normalizedIdentifier = normalize(identifier)
        val account = service.verifyPassword(normalizedIdentifier, password)
            ?: if (AppConfig.auth.passwordFor(normalizedIdentifier) == password) {
                storage.findAccount(normalizedIdentifier)
            } else {
                null
        }
        if (account != null) {
            storage.saveAccount(account)
            if (!requiresTwoFactor(normalizedIdentifier)) {
                storage.saveSession(account.id)
            }
        }
        return account
    }

    fun restoreSession(): Account? {
        val sessionAccountId = storage.readSessionAccountId() ?: return null
        return storage.findAccountById(sessionAccountId)
    }

    fun logout() {
        storage.clearSession()
    }

    private fun normalize(identifier: String): String {
        return AppConfig.auth.normalizedIdentifier(identifier)
    }
}
