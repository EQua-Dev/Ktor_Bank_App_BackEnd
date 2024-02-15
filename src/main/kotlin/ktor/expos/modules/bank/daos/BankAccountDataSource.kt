package ktor.expos.modules.bank.daos

import ktor.expos.modules.account.models.responses.AccountData
import ktor.expos.modules.account.models.responses.AccountInfoResponse
import ktor.expos.modules.bank.models.requests.BankCommission
import ktor.expos.modules.bank.models.responses.BankCommissionResponse

interface BankAccountDataSource {
    //add transaction commission information
    //get all transaction commission

    suspend fun createCommission(bankCommission: BankCommission): Boolean
    suspend fun getAllBankCommission(): BankCommissionResponse
}