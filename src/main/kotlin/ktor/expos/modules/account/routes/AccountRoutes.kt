package ktor.expos.modules.account.routes

import io.ktor.server.routing.*
import ktor.expos.modules.account.daos.AccountDataSource

interface AccountRoutes {

    fun Route.createAccount(accountDataSource: AccountDataSource)
    fun Route.getAllMyAccounts(
    accountDataSource: AccountDataSource
    )
    fun Route.getAccountInfoByAccountNumber(
    accountDataSource: AccountDataSource
    )
}