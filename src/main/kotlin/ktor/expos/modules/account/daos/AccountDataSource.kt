package ktor.expos.modules.account.daos

import ktor.expos.modules.account.models.responses.AccountData
import ktor.expos.modules.account.models.responses.AccountInfoResponse

interface AccountDataSource {
    suspend fun getAccountByAccountNumber(accountNumber: String): AccountData?
    suspend fun insertAccount(accountData: AccountData): Boolean
    suspend fun getAllMyAccounts(ownerId: String): List<AccountData>
    suspend fun getAccountsByAccountNumber(accountNumber: String): AccountInfoResponse
}