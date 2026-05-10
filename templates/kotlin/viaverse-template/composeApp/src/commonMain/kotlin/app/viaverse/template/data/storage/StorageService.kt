package app.viaverse.template.data.storage

import app.viaverse.template.domain.model.Account

interface StorageService {
    fun saveAccount(account: Account)
    fun saveAuthRecord(record: LocalAuthRecord)
    fun findAccount(identifier: String): Account?
    fun findAccountById(accountId: String): Account?
    fun findAuthRecord(identifier: String): LocalAuthRecord?
    fun saveSession(accountId: String)
    fun readSessionAccountId(): String?
    fun clearSession()
}

data class LocalAuthRecord(
    val account: Account,
    val password: String,
    val twoFactorEnabled: Boolean
)
