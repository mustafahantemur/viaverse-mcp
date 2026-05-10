package app.viaverse.template.data.storage

import app.viaverse.template.domain.model.Account

interface StorageService {
    fun saveAccount(account: Account)
    fun findAccount(identifier: String): Account?
    fun findAccountById(accountId: String): Account?
    fun saveSession(accountId: String)
    fun readSessionAccountId(): String?
    fun clearSession()
}
