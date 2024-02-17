package ktor.expos.modules.account.routes

import io.ktor.server.routing.*
import ktor.expos.modules.account.daos.AccountDataSource
import ktor.expos.modules.bank.daos.BankAccountDataSource
import ktor.expos.modules.transaction.daos.TransactionDataSource
import ktor.expos.modules.user.daos.UserDataSource

interface AccountRoutes {

    fun Route.createAccount(accountDataSource: AccountDataSource, bankAccountDataSource: BankAccountDataSource, transactionDataSource: TransactionDataSource, userDataSource: UserDataSource)
    fun Route.getAllMyAccounts(
    accountDataSource: AccountDataSource
    )
    fun Route.getAccountInfoByAccountNumber(
    accountDataSource: AccountDataSource
    )
}