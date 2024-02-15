package ktor.expos.modules.transaction.daos

import ktor.expos.modules.bank.models.requests.BankCommission
import ktor.expos.modules.bank.models.responses.BankCommissionResponse
import ktor.expos.modules.transaction.models.requests.Transaction

interface TransactionDataSource {
    //add transaction information
    //get all transaction for account (whether the sender or the receiver)

    suspend fun createTransaction(transaction: Transaction): Boolean
    suspend fun getAllTransactionsOfAccount(accountNumber: String): List<Transaction>
}