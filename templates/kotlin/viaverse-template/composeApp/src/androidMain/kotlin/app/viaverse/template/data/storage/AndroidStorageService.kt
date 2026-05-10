package app.viaverse.template.data.storage

import android.content.Context
import app.viaverse.template.domain.model.Account
import app.viaverse.template.domain.model.AccountCapability
import app.viaverse.template.domain.model.AccountStatus

class AndroidStorageService(context: Context) : StorageService {
    private val preferences = context.getSharedPreferences("viaverse_template_storage", Context.MODE_PRIVATE)

    override fun saveAccount(account: Account) {
        preferences.edit()
            .putString(accountKey(account.identifier), encode(account))
            .putString(accountIdKey(account.id), account.identifier)
            .apply()
    }

    override fun saveAuthRecord(record: LocalAuthRecord) {
        preferences.edit()
            .putString(accountKey(record.account.identifier), encode(record.account))
            .putString(accountIdKey(record.account.id), record.account.identifier)
            .putString(authRecordKey(record.account.identifier), encodeRecord(record))
            .apply()
    }

    override fun findAccount(identifier: String): Account? {
        return preferences.getString(accountKey(identifier), null)?.let(::decode)
    }

    override fun findAccountById(accountId: String): Account? {
        val identifier = preferences.getString(accountIdKey(accountId), null) ?: return null
        return findAccount(identifier)
    }

    override fun findAuthRecord(identifier: String): LocalAuthRecord? {
        return preferences.getString(authRecordKey(identifier), null)?.let(::decodeRecord)
    }

    override fun saveSession(accountId: String) {
        preferences.edit().putString(KEY_SESSION_ACCOUNT_ID, accountId).apply()
    }

    override fun readSessionAccountId(): String? {
        return preferences.getString(KEY_SESSION_ACCOUNT_ID, null)
    }

    override fun clearSession() {
        preferences.edit().remove(KEY_SESSION_ACCOUNT_ID).apply()
    }

    private fun accountKey(identifier: String): String = "account:${identifier.lowercase()}"

    private fun accountIdKey(accountId: String): String = "account_id:$accountId"

    private fun authRecordKey(identifier: String): String = "auth:${identifier.lowercase()}"

    private fun encode(account: Account): String {
        return listOf(
            account.id,
            account.displayName,
            account.identifier,
            account.status.name,
            account.capabilities.joinToString(",") { it.name }
        ).joinToString("|")
    }

    private fun decode(raw: String): Account? {
        val parts = raw.split("|")
        if (parts.size != 5) return null
        return Account(
            id = parts[0],
            displayName = parts[1],
            identifier = parts[2],
            status = runCatching { AccountStatus.valueOf(parts[3]) }.getOrNull() ?: AccountStatus.ACTIVE,
            capabilities = parts[4]
                .split(",")
                .filter { it.isNotBlank() }
                .mapNotNull { runCatching { AccountCapability.valueOf(it) }.getOrNull() }
                .toSet()
        )
    }

    private fun encodeRecord(record: LocalAuthRecord): String {
        return listOf(
            encode(record.account),
            record.password,
            record.twoFactorEnabled.toString()
        ).joinToString("||")
    }

    private fun decodeRecord(raw: String): LocalAuthRecord? {
        val parts = raw.split("||")
        if (parts.size != 3) return null
        val account = decode(parts[0]) ?: return null
        return LocalAuthRecord(
            account = account,
            password = parts[1],
            twoFactorEnabled = parts[2].toBooleanStrictOrNull() ?: false
        )
    }

    private companion object {
        const val KEY_SESSION_ACCOUNT_ID = "session_account_id"
    }
}
