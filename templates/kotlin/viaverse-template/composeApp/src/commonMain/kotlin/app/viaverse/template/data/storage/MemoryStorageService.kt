package app.viaverse.template.data.storage

import app.viaverse.template.domain.model.Account

class MemoryStorageService : StorageService {
    private val accountsByIdentifier = mutableMapOf<String, Account>()
    private val accountsById = mutableMapOf<String, Account>()
    private val authRecordsByIdentifier = mutableMapOf<String, LocalAuthRecord>()
    private var sessionAccountId: String? = null

    override fun saveAccount(account: Account) {
        accountsByIdentifier[account.identifier.lowercase()] = account
        accountsById[account.id] = account
    }

    override fun saveAuthRecord(record: LocalAuthRecord) {
        saveAccount(record.account)
        authRecordsByIdentifier[record.account.identifier.lowercase()] = record
    }

    override fun findAccount(identifier: String): Account? {
        return accountsByIdentifier[identifier.lowercase()]
    }

    override fun findAccountById(accountId: String): Account? {
        return accountsById[accountId]
    }

    override fun findAuthRecord(identifier: String): LocalAuthRecord? {
        return authRecordsByIdentifier[identifier.lowercase()]
    }

    override fun saveSession(accountId: String) {
        sessionAccountId = accountId
    }

    override fun readSessionAccountId(): String? = sessionAccountId

    override fun clearSession() {
        sessionAccountId = null
    }
}
