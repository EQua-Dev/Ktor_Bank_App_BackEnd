package ktor.expos.modules.transaction.daos

import ktor.expos.modules.bank.models.requests.BankCommission
import ktor.expos.modules.bank.models.responses.BankCommissionResponse

interface TransactionDataSource {
    //add transaction commission information
    //get all transaction commission

    suspend fun createCommission(bankCommission: BankCommission): Boolean
    suspend fun getAllBankCommission(): BankCommissionResponse
}