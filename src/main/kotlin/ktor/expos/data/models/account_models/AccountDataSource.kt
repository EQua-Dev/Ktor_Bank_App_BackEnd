package ktor.expos.data.models.account_models

import ktor.expos.data.models.user_models.UserData

interface AccountDataSource {
    suspend fun getAccountByAccountNumber(accountNumber: String): AccountData?
    suspend fun insertAccount(accountData: AccountData): Boolean
}