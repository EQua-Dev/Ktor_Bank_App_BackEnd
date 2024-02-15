package ktor.expos.modules.account.routes

import io.ktor.server.routing.*
import ktor.expos.modules.account.daos.AccountDataSource
import ktor.expos.modules.bank.daos.BankAccountDataSource
import ktor.expos.modules.transaction.daos.TransactionDataSource

interface AccountRoutes {

    fun Route.createAccount(accountDataSource: AccountDataSource, bankAccountDataSource: BankAccountDataSource, transactionDataSource: TransactionDataSource)
    fun Route.getAllMyAccounts(
    accountDataSource: AccountDataSource
    )
    fun Route.getAccountInfoByAccountNumber(
    accountDataSource: AccountDataSource
    )
}