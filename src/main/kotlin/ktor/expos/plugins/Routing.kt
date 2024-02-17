package ktor.expos.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ktor.expos.modules.account.daos.AccountDataSource
import ktor.expos.modules.account.routes.AccountRoutes
import ktor.expos.modules.account.routes.AccountRoutesImpl
import ktor.expos.modules.account.routes.AccountRoutesImpl.createAccount
import ktor.expos.modules.account.routes.AccountRoutesImpl.getAccountInfoByAccountNumber
import ktor.expos.modules.account.routes.AccountRoutesImpl.getAllMyAccounts
import ktor.expos.modules.bank.daos.BankAccountDataSource
import ktor.expos.modules.transaction.daos.TransactionDataSource
import ktor.expos.modules.transaction.routes.TransactionRoutesImpl.getTransferHistory
import ktor.expos.modules.transaction.routes.TransactionRoutesImpl.transferFunds
import ktor.expos.modules.user.daos.UserDataSource
import ktor.expos.modules.user.routes.authenticate
import ktor.expos.modules.user.routes.getSecretInfo
import ktor.expos.modules.user.routes.signIn
import ktor.expos.modules.user.routes.signUp
import ktor.expos.security.hashing.HashingService
import ktor.expos.security.token.TokenConfig
import ktor.expos.security.token.TokenService

fun Application.configureRouting(
    userDataSource: UserDataSource,
    accountDataSource: AccountDataSource,
    bankAccountDataSource: BankAccountDataSource,
    transactionDataSource: TransactionDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {

    routing {
        signIn(hashingService, userDataSource, tokenService, tokenConfig)
        signUp(hashingService, userDataSource)
        createAccount(accountDataSource, bankAccountDataSource, transactionDataSource, userDataSource)
        getAllMyAccounts(accountDataSource)
        getAccountInfoByAccountNumber(accountDataSource)
        transferFunds(accountDataSource, bankAccountDataSource, transactionDataSource)
        getTransferHistory(accountDataSource, transactionDataSource)
        authenticate()
        getSecretInfo()
    }
}